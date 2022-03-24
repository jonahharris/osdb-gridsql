#!/bin/sh
##########################################################################
#
# Copyright (C) 2010 EnterpriseDB Corporation.
# 
# gs-execdb.sh
#
#
# Executes a command against all underlying database instances
#
##########################################################################

EXECCLASS=com.edb.gridsql.util.ExecDb

DIRNAME=`dirname $0`

if [ -f $DIRNAME/../gridsql_env.sh -a -z "$GSPATH" ]
then
      source $DIRNAME/../gridsql_env.sh
fi

GSCONFIG=$GSPATH/config/gridsql.config

java -classpath ${GSPATH}/bin/xdbutil.jar:${GSPATH}/lib/edb-jdbc14.jar:${GSPATH}/lib/log4j.jar -Dconfig.file.path=${GSCONFIG} ${EXECCLASS} "$@"

