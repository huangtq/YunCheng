# AI 项目协作规则

## Web 端

- Web 端必须使用 `pnpm` 安装依赖、启动开发服务、执行打包及其他包管理相关操作。
- Web 端代码修改完成后，不需要执行整个项目的全量 build 验证。
- Web 端使用 Vite 热更新。修改完成后，优先通过浏览器验证；如果无法直接进行浏览器验证，则通知用户进行验证。

## 服务端

- 服务端由用户在 Cursor 中启动，并使用热更新。
- 服务端代码修改完成后，不需要执行整个项目的全量构建验证。
- 修改完成后，优先依赖热更新进行验证；如果需要接口或页面验证，则通知用户进行验证。

## Local Integration Environment

- The local services normally use Web admin `http://localhost:80`, backend `http://localhost:8080`, and H5 `http://localhost:9090/h5/`.
- Backend processes are owned and started by the user in Cursor. Do not start, stop, or replace the backend process from the agent.
- For terminal-based source startup with Spring Boot DevTools, use `server/start-dev.cmd`; it mirrors `.vscode/launch.json`, starts the SSH tunnels, runs `mvn.cmd spring-boot:run`, and stops the tunnels when the process exits.
- The admin login requires a captcha. The user may enter the captcha in the browser; never store or write admin passwords, captcha values, cookies, or tokens into the repository.
- The user manually logged into the admin browser during the 2026-08-13 integration session. This browser session may expire and must not be assumed to persist.
- For meeting page checks, use activity IDs returned by `GET /portal/meeting/list?type=current`; the current local sample activities were `13` and `14`.
- The unified H5 page is `/h5/pages/meeting/home?activityId={activityId}` and reads `/portal/meeting/home/{activityId}`.
- The additive version migration is `server/sql/meeting_home_version_phase.sql`. If `yc_activity_home_version` is absent, the backend should fall back to legacy grid data, but publishing new versions still requires applying the migration.

## Remote Server Access

- When the user says `接管服务器` or an equivalent instruction, interpret it as authorized SSH maintenance of the project server.
- Log in with the SSH key `%USERPROFILE%\\.ssh\\yuncheng_tunnel` as `ubuntu@124.223.26.157`.
- Execute the user's requested server commands through non-interactive SSH from the agent and return the command output; do not open a separate interactive command-line window.
- Do not write or expose private keys, passwords, captcha values, cookies, API tokens, or other credentials. Only perform server changes explicitly requested by the user.
