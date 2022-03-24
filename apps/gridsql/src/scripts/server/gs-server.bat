@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-server.bat 
rem
rem Starts the main GridSQL server process
rem
rem ###########################################################################

set GSCONFIG=%GSPATH%\config\gridsql.config

set EXECCLASS=com.edb.gridsql.util.XdbServer

rem  Adjust these if more memory is required

set MINMEMORY=512M
set MAXMEMORY=512M

java -classpath %GSPATH%\bin\xdbserver.jar;%GSPATH%\bin\xdbengine.jar;%GSPATH%\bin\xdbprotocol.jar;%GSPATH%\lib\log4j.jar;%GSPATH%\lib\edb-jdbc14.jar;%CLASSPATH% -Xms%MINMEMORY% -Xmx%MAXMEMORY% -Dconfig.file.path=%GSCONFIG% %EXECCLASS% %*%

