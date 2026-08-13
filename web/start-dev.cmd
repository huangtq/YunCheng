@echo off
setlocal
pushd "%~dp0"
call pnpm.cmd dev
set "exitCode=%ERRORLEVEL%"
popd
exit /b %exitCode%
