@echo off

set server_user=root
set server_pwd=czM6qb9X

set config_path=192.168.1.151:/data/
pscp -r -l %server_user% -pw %server_pwd% .\rms-api.jar %config_path%
pscp -r -l %server_user% -pw %server_pwd% .\launch.sh %config_path%

@echo off
pause

