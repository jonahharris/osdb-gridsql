#!/bin/sh
##########################################################################
#
# Copyright (C) 2010 EnterpriseDB Corporation.
#
# gs-agent.sh
#
#
# Starts an GridSQL agent process
#
##########################################################################


EXECCLASS=com.edb.gridsql.util.XdbAgent

DIRNAME=`dirname $0`

if [ -f $DIRNAME/../gridsql_env.sh -a -z "$GSPATH" ]
then
      source $DIRNAME/../gridsql_env.sh
fi

GSCONFIG=$GSPATH/config/gridsql_agent.config

# Adjust these if more memory is required
MINMEMORY=256M
MAXMEMORY=256M

echo "Starting...."

nohup java -classpath ${GSPATH}/bin/xdbengine.jar:${GSPATH}/bin/xdbserver.jar:${GSPATH}/lib/edb-jdbc14.jar:${GSPATH}/lib/log4j.jar:${CLASSPATH} -Xms${MINMEMORY} -Xmx${MAXMEMORY} -Dconfig.file.path=${GSCONFIG} $EXECCLASS $* > ${GSPATH}/log/agent.log 2>&1 &

PROCID=$!

sleep 3

ps $PROCID >/dev/null 2>/dev/null
CHECK=$?

if [ "$CHECK" -ne "0" ]
then
	echo "Error starting XDBAgent"
	echo " agent.log output:"
	cat ${GSPATH}/log/agent.log
	echo ""
	echo " tail of console.log output:"
	tail -10 ${GSPATH}/log/console.log
fi

