@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-shutdown.bat
rem
rem
rem Shuts down the GridSQL server
rem
rem ###########################################################################

set EXECCLASS=com.edb.gridsql.util.XdbShutdown

set GSCONFIG=%GSPATH%\config\gridsql.config

java -classpath %GSPATH%\bin\xdbutil.jar;%GSPATH%\lib\log4j.jar;%GSPATH%\lib\edb-jdbc14.jar -Dconfig.file.path=%GSCONFIG% %EXECCLASS% %*%

