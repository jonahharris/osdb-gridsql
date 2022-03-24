#!/bin/sh
##########################################################################
#
# Copyright (C) 2010 EnterpriseDB Corporation.
#
# gs-server.sh
#
#
# Starts the main GridSQL server process
#
##########################################################################


EXECCLASS=com.edb.gridsql.util.XdbServer

DIRNAME=`dirname $0`

if [ -f $DIRNAME/../gridsql_env.sh -a -z "$GSPATH" ]
then
      source $DIRNAME/../gridsql_env.sh
fi

GSCONFIG=$GSPATH/config/gridsql.config

# Adjust these if more memory is required
MINMEMORY=512M
MAXMEMORY=512M
STACKSIZE=256K
XDEBUG="-agentlib:jdwp=transport=dt_socket,address=localhost:18000,server=y,suspend=n"

echo "Starting...."

nohup java -classpath ${GSPATH}/bin/xdbengine.jar:${GSPATH}/bin/xdbprotocol.jar:${GSPATH}/bin/xdbserver.jar:${GSPATH}/lib/edb-jdbc14.jar:${GSPATH}/lib/log4j.jar:${CLASSPATH} -Xms${MINMEMORY} -Xmx${MAXMEMORY} -Xss${STACKSIZE} -Dconfig.file.path=${GSCONFIG} $EXECCLASS $* > ${GSPATH}/log/server.log 2>&1 &

PROCID=$!

sleep 8

ps $PROCID >/dev/null 2>/dev/null
CHECK=$?

if [ "$CHECK" -ne "0" ]
then
	echo "Error starting XDBServer"
	echo " server.log output:"
	cat ${GSPATH}/log/server.log
	echo ""
	echo " tail of console.log output:"
	tail -10 ${GSPATH}/log/console.log
fi

