@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-dbstart.bat
rem
rem
rem Brings an GridSQL database online
rem
rem ###########################################################################

set EXECCLASS=com.edb.gridsql.util.XdbDbStart

set GSCONFIG=%GSPATH%\config\gridsql.config

java -classpath %GSPATH%\bin\xdbutil.jar;%GSPATH%\lib\edb-jdbc14.jar;%GSPATH%\lib\log4j.jar -Dconfig.file.path=%GSCONFIG% %EXECCLASS% %*%

