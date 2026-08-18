# Production blue-green deployment

The production host runs two systemd instances, `ruoyi-admin@a` on port 8081 and
`ruoyi-admin@b` on port 8082. Nginx sends traffic to the active port through the
`ruoyi_backend` upstream. The inactive instance is started and health-checked
before Nginx is reloaded.

Each release keeps its JAR and static files under a dated directory. The `pc-current`
and `h5-current` links are switched atomically so old browser requests can finish
using the old hashed assets. The external `/project/ruoyi/config` directory is never
replaced by a release.

Before enabling both backend instances, apply `server/sql/quartz_cluster_migration.sql`
to the production database. The application uses JDBC Quartz clustering and does not
clear the scheduler during startup.
