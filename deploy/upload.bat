@echo off

set server_user=root
set server_pwd=LKTevPfLJAmCqNcs
set config_path=192.168.20.161:/data/rms/

rem set server_pwd=czM6qb9X
rem set config_path=192.168.1.151:/data/rms/

pscp -r -l %server_user% -pw %server_pwd% .\rms-api.jar %config_path%

@echo off
pause

