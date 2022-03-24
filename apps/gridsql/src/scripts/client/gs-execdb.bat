@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-execdb.bat
rem
rem
rem Executes a command against all underlying database instances
rem
rem ###########################################################################

set EXECCLASS=com.edb.gridsql.util.ExecDb

set GSCONFIG=%GSPATH%\config\gridsql.config

java -classpath %GSPATH%\bin\xdbutil.jar;%GSPATH%\lib\edb-jdbc14.jar;%GSPATH%\lib\log4j.jar -Dconfig.file.path=%GSCONFIG% %EXECCLASS% %*%



