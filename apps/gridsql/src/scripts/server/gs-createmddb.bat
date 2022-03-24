@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-createmddb.bat
rem
rem Creates the GridSQL metadata database
rem
rem ###########################################################################

set EXECCLASS=com.edb.gridsql.util.CreateMdDb

set GSCONFIG=%GSPATH%\config\gridsql.config

java -classpath %GSPATH%\bin\xdbengine.jar;%GSPATH%\bin\xdbserver.jar;%GSPATH%\lib\edb-jdbc14.jar;%GSPATH%\lib\log4j.jar;%CLASSPATH% -Dconfig.file.path=%GSCONFIG% %EXECCLASS% %*%

