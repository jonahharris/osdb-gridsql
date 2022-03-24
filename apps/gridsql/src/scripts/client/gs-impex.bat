@echo off
rem ###########################################################################
rem Copyright (C) 2010 EnterpriseDB Corporation.
rem 
rem
rem gs-impex.bat
rem
rem
rem Used for importing and exporting with GridSQL. 
rem If populating with a large amount of data, use XDBLoader instead.
rem
rem ###########################################################################

set EXECCLASS=com.edb.gridsql.util.XdbImpEx

java -classpath %GSPATH%\bin\xdbutil.jar;%GSPATH%\lib\edb-jdbc14.jar;%GSPATH%\lib\log4j.jar;%CLASSPATH% %EXECCLASS% %*%
