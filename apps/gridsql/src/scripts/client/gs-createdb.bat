@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-createdb.bat
rem
rem Creates an GridSQL database
rem
rem ###########################################################################

set EXECCLASS=com.edb.gridsql.util.CreateDb

set GSCONFIG=%GSPATH%\config\gridsql.config

java -classpath %GSPATH%\bin\xdbutil.jar;%GSPATH%\lib\edb-jdbc14.jar;%GSPATH%\lib\log4j.jar;%CLASSPATH% -Dconfig.file.path=%GSCONFIG% %EXECCLASS% %*%

