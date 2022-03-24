@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-agent.bat 
rem
rem Starts an GridSQL agent process
rem
rem ###########################################################################

set GSCONFIG=%GSPATH%\config\gridsql_agent.config

set EXECCLASS=com.edb.gridsql.util.XdbAgent


rem  Adjust these if more memory is required

set MINMEMORY=256M
set MAXMEMORY=256M

java -classpath %GSPATH%\bin\xdbserver.jar;%GSPATH%\bin\xdbengine.jar;%GSPATH%\lib\edb-jdbc14.jar;%GSPATH%\lib\log4j.jar;%CLASSPATH% -Xms%MINMEMORY% -Xmx%MAXMEMORY% -Dconfig.file.path=%GSCONFIG% %EXECCLASS% %*%

