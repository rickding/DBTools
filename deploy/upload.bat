@echo off

set server_user=root
set server_pwd=LKTevPfLJAmCqNcs

set config_path=192.168.20.161:/data/rms/
pscp -r -l %server_user% -pw %server_pwd% .\rms-api.jar %config_path%
pscp -r -l %server_user% -pw %server_pwd% .\launch.sh %config_path%

@echo off
pause

