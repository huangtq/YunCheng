# Production Deployment Guide

This guide releases the administrative `web` and backend `server`. It does not release `web-mobile`.

## Targets

- SSH: `ubuntu@124.223.26.157`, using `%USERPROFILE%\.ssh\yuncheng_tunnel`.
- Backend JAR: `/project/ruoyi/ruoyi-admin.jar`.
- Administrative static files: `/project/ruoyi/www/pc`.
- External production configuration: `/project/ruoyi/config/application.yml` and `/project/ruoyi/config/application-druid.yml`.
- Nginx serves the HTTP/HTTPS entry points; the backend listens on `8080`.

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

## Server Switch

1. Upload the JAR and static archive to a unique staging directory, such as `/home/ubuntu/yuncheng-release-YYYYMMDD-HHMMSS`.
2. Back up the existing `ruoyi-admin.jar` and `www/pc` under `/project/ruoyi/backup/YYYYMMDD-HHMMSS`.
3. Extract the static archive into a new sibling directory such as `www/pc-new`, and set its owner to `www-data:www-data`.
4. Stop the previous Java process. Move the previous JAR and `pc` directory to the backup, then move the staged JAR and new `pc` directory into the production paths.
5. Start the JAR as `ubuntu` with `-Xms512m -Xmx1024m`. Supply the database password only through the process environment variable `RUOYI_DB_PASSWORD`; do not put it in shell history, repository files, or server configuration.
6. Ensure `/project/ruoyi/ruoyi-admin.log` is writable by `ubuntu`, otherwise the artifact may be switched while the process fails to start.

Use `sudo` for the filesystem switch because the production directory is system-owned. Do not modify `/project/ruoyi/config`. Keep the dated backup until the new version is stable.

## Verification

Run on the server:

```bash
# Backend process and ports
ps -eo pid,user,args | grep '[r]uoyi-admin.jar'
sudo ss -lntp | grep -E ':8080|:3306'

# Backend endpoint
curl -sS -o /dev/null -w '%{http_code}\n' \
  'http://127.0.0.1:8080/portal/meeting/list?type=current'

# Administrative web HTTPS page
curl -k -sS -o /dev/null -w '%{http_code}\n' \
  -H 'Host: 124.223.26.157' https://127.0.0.1/
```

Both HTTP status codes must be `200`. A `301` response on HTTP port `80` is the expected Nginx redirect to HTTPS.

If the backend fails to start, inspect `/project/ruoyi/ruoyi-admin.log` first:

- `Connection refused` for `127.0.0.1:3306`: check the MySQL service and port listener; do not assume the password is wrong.
- Permission denied when writing the log: repair the log file owner for `ubuntu`, then start the JAR again.

## Rollback

If a health check fails, stop the new process, move the release backup's `ruoyi-admin.jar` and `pc` directory back to the production paths, start the previous JAR, and repeat the verification. The external `config` directory remains unchanged during rollback as well.
