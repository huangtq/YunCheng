# Production Deployment Guide

This guide releases the administrative `web` and backend `server`. It does not release `web-mobile`.

## Targets

- SSH: `ubuntu@124.223.26.157`, using `%USERPROFILE%\.ssh\yuncheng_tunnel`.
- Backend slots: `/project/ruoyi/backend-a` (8081) and `/project/ruoyi/backend-b` (8082).
- Release artifacts: `/project/ruoyi/releases/<release-id>`.
- Administrative static link: `/project/ruoyi/www/pc-current`.
- External production configuration: `/project/ruoyi/config/application.yml` and `/project/ruoyi/config/application-druid.yml`.
- Nginx serves the HTTP/HTTPS entry points through the `ruoyi_backend` upstream.

The external production data-source configuration must continue to use the production `ry-vue` database. Local development can use `ry-vue-dev`; never upload the local `application-druid.yml` to `/project/ruoyi/config`.

## Build

Run from the repository root:

```powershell
# Backend
mvn -f .\server\pom.xml clean package -DskipTests

# Administrative web client
Set-Location .\web
pnpm install
pnpm build:prod
```

The output files are `server/ruoyi-admin/target/ruoyi-admin.jar` and `web/dist`. If `pnpm build:prod` is blocked by a custom npm registry but dependencies are already installed, build with Vite directly:

```powershell
& 'D:\Program Files\nodejs\node.exe' .\node_modules\vite\bin\vite.js build --mode production
```

Create a uniquely named archive for the static files and calculate SHA-256 hashes. Calculate them again on the remote host after upload; stop the release if either hash differs.

```powershell
tar.exe -czf .\web\dist-release-YYYYMMDD.tgz -C .\web\dist .
Get-FileHash .\server\ruoyi-admin\target\ruoyi-admin.jar -Algorithm SHA256
Get-FileHash .\web\dist-release-YYYYMMDD.tgz -Algorithm SHA256
```

## Blue-Green Switch

The server is initialized by `deploy/scripts/bootstrap-bluegreen.sh`. Subsequent
releases use `/project/ruoyi/bin/promote-bluegreen.sh <release-id>` after uploading
`ruoyi-admin.jar` and `pc.tgz` to `/home/ubuntu/yuncheng-bluegreen-<release-id>`.

The promotion script:

1. Copies the JAR into the inactive systemd slot and extracts the static files into a dated release directory.
2. Starts the inactive slot with Quartz in standby mode and checks `/internal/health/ready`.
3. Reloads Nginx to send new requests to the healthy slot.
4. Stops the old slot, activates Quartz on the new slot, and atomically switches `pc-current`.
5. Records the active and previous slots in `/project/ruoyi/bluegreen-state`.

The services are `ruoyi-admin@a.service` and `ruoyi-admin@b.service`. Secrets are
copied only into root-owned, group-readable environment files under
`/project/ruoyi/env`; never put them in a command, release archive, or Git file.
Do not replace `/project/ruoyi/config`. Keep old release directories until the
release has been stable for at least seven days.

Before the first blue-green deployment, apply the additive
`server/sql/quartz_cluster_migration.sql`. Quartz uses the existing `QRTZ_*` tables
with JDBC clustering; startup no longer clears the scheduler.

## Verification

Run on the server:

```bash
# Backend process and ports
systemctl is-active ruoyi-admin@a
sudo ss -lntp | grep -E ':8081|:8082|:3306'

# Readiness and Quartz state
curl -sS http://127.0.0.1:8081/internal/health/ready
sudo mysql --protocol=socket ry-vue -Nse 'SELECT COUNT(*) FROM QRTZ_SCHEDULER_STATE'

# Backend endpoint
curl -k -sS -o /dev/null -w '%{http_code}\n' \
  'https://127.0.0.1/prod-api/portal/meeting/list?type=current'

# Administrative web page
curl -sS -o /dev/null -w '%{http_code}\n' \
  -H 'Host: yunchengmice.cn' http://127.0.0.1:8088/
```

Both HTTP status codes must be `200`. A `301` response on HTTP port `80` is the expected Nginx redirect to HTTPS.

If a slot fails to start, inspect its journal first:

```bash
sudo journalctl -u ruoyi-admin@a -n 200 --no-pager
```

- `Connection refused` for `127.0.0.1:3306`: check the MySQL service and port listener; do not assume the password is wrong.
- Permission denied when writing the log: repair the log file owner for `ubuntu`, then start the JAR again.

## Rollback

If a release is unhealthy after promotion, run:

```bash
sudo /project/ruoyi/bin/rollback-bluegreen.sh
```

The script starts the previous slot in Quartz standby, verifies it, switches Nginx
and `pc-current` back, then activates its scheduler. The external `config` directory
and database schema remain unchanged during rollback.
