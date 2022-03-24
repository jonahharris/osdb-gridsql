##########################################################################
#
# Copyright (C) 2010 EnterpriseDB Corporation.
#
# gridsql_env.sh
#
#
# File to source for adding to user environment for executing
# GridSQL programs. It can be referenced in user environments.
# 
# Examples:
#  source /usr/local/edb/gridsql/gridsql_env.sh
#  . /usr/local/edb/gridsql/gridsql_env.sh
#
##########################################################################

# ROOT directory of where GridSQL is installed
export GSPATH=--INSTALL_DIR--

# Add any referenced JDBC drivers here, like EnterpriseDB's driver
export CLASSPATH=$GSPATH/lib/edb-jdbc14.jar:$CLASSPATH

# Include scripts in user path
export PATH=$GSPATH/bin:$PATH


