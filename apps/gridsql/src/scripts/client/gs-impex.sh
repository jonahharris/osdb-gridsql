#!/bin/sh
##########################################################################
#
# Copyright (C) 2010 EnterpriseDB Corporation.
# 
# gs-impex.sh
#
#
# Used for importing and exporting with GridSQL. 
# If populating with a large amount of data, use XDBLoader instead.
#
##########################################################################

EXECCLASS=com.edb.gridsql.util.XdbImpEx

DIRNAME=`dirname $0`

if [ -f $DIRNAME/../gridsql_env.sh -a -z "$GSPATH" ]
then
      source $DIRNAME/../gridsql_env.sh
fi

java -classpath ${GSPATH}/bin/xdbutil.jar:${GSPATH}/lib/edb-jdbc14.jar:${GSPATH}/lib/log4j.jar $EXECCLASS "$@"

