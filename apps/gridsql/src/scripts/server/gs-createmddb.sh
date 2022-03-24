#!/bin/sh
##########################################################################
#
# Copyright (C) 2010 EnterpriseDB Corporation.
#
# gs-createmddb.sh
#
#
# Creates the GridSQL metadata database
#
##########################################################################

EXECCLASS=com.edb.gridsql.util.CreateMdDb

DIRNAME=`dirname $0`

if [ -f $DIRNAME/../gridsql_env.sh -a -z "$GSPATH" ]
then
      source $DIRNAME/../gridsql_env.sh
fi

GSCONFIG=$GSPATH/config/gridsql.config

java -classpath ${GSPATH}/bin/xdbengine.jar:${GSPATH}/bin/xdbserver.jar:${GSPATH}/lib/edb-jdbc14.jar:${GSPATH}/lib/log4j.jar:${CLASSPATH} -Dconfig.file.path=${GSCONFIG} $EXECCLASS $* 

