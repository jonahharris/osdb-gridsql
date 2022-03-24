#!/bin/sh
##########################################################################
#
# Copyright (C) 2010 EnterpriseDB Corporation.
#
# gs-cmdline.sh
#
#
# Used for getting a SQL command prompt
#
##########################################################################

EXECCLASS=com.edb.gridsql.util.CmdLine

DIRNAME=`dirname $0`

if [ -f $DIRNAME/../gridsql_env.sh -a -z "$GSPATH" ]
then
      source $DIRNAME/../gridsql_env.sh
fi

java -classpath ${GSPATH}/bin/xdbutil.jar:${GSPATH}/lib/jline-0_9_5.jar:${GSPATH}/lib/edb-jdbc14.jar:${GSPATH}/lib/log4j.jar:${CLASSPATH} $EXECCLASS $* 
