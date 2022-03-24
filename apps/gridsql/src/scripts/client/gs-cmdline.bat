@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-cmdline.bat
rem
rem
rem Used for getting a SQL command prompt
rem
rem ###########################################################################

set EXECCLASS=com.edb.gridsql.util.CmdLine

java -classpath %GSPATH%\bin\xdbutil.jar;%GSPATH%\bin\xdbengine.jar;%GSPATH%\lib\edb-jdbc14.jar;%GSPATH%\lib\jline-0.9.5.jar;%GSPATH%\lib\log4j.jar %EXECCLASS% %*%
