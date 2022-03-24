#
# Makefile for GridSQL
#
# In working with the code, in many IDEs you can simply indicate
# the root source directory and add the libraries in the lib 
# directory to the project, and everything will compile without
# any problems.
#
# This Makefile is primarily used when building final installables
# and does some extra work like filtering out many debugging oriented
# logging statements that impact execution time, particularly if
# doing multi-threaded ETL.
#
# When using the Makefile, you may want to set DESTDIR, e.g.,
# make fullbuild DESTDIR=path (default /usr/local/gridsql).
#

#Directory where CVS modules are checked out
DEV_ROOT=../..
#Where is root directory for source files
PROJROOT=$(DEV_ROOT)/apps/gridsql
SRCROOT=$(PROJROOT)/src
#Where is directory for library files
LIBROOT=$(PROJROOT)/lib
#Where PERL scripts are stored (if log stripping is needed)
TOOLSPATH=$(DEV_ROOT)/tools
#For temp files
BUILDROOT=$(PROJROOT)/obj
#Java CLASSPATH
CLASSPATH=$(BUILDROOT)/bin:$(LIBROOT)/log4j.jar:$(LIBROOT)/jline-0_9_5.jar:$(LIBROOT)/edb-jdbc14.jar:$(LIBROOT)/postgresql.jar
#Where things are installed
DESTDIR = /usr/local/gridsql
DEBUGINFO = -g:none
######## 

fullbuild: GridSQL installGridSQL installAgent

build: GridSQL copyGridSQL copyAgentRest

develop: clean copy buildserver buildutil 
developCleanLogger: clean cleanLogger buildserver buildutil 

GridSQLDebug: clean cleanLogger buildserver buildutil 
GridSQL: clean cleanLogger buildserver buildutil   

installWindows: installAllDirs installServerJars installClientJars installServerScriptsWindows installAgentScriptsWindows installClientScriptsWindows installServerLibs installClientLibs installServerConfigWEWindows installAgentConfigWindows installDocs

	
copyGridSQL: installBaseGridSQL copyCommonConfig copyServerConfigWE installDocs

installBaseGridSQL: installAllDirs installServerJars installClientJars installServerScripts installClientScripts installServerLibs installClientLibs

installGridSQL: installBaseGridSQL installCommonConfig installServerConfigWE installDocs 

copyAgentRest: installAgentScripts copyAgentConfig 

installAgent: installExDirs installServerJars installAgentScripts installCommonConfig installAgentConfig installNotes installServerLibs 

installClient: installBaseDirs installClientJars installClientScripts installCommonConfig installNotes installClientLibs

installBaseDirs:
	test -d $(DESTDIR) || install -g edb -o enterprisedb -d $(DESTDIR)
	test -d $(DESTDIR)/bin || install -g edb -o enterprisedb -d $(DESTDIR)/bin
	test -d $(DESTDIR)/lib || install -g edb -o enterprisedb -d $(DESTDIR)/lib

installExDirs: installBaseDirs
	test -d $(DESTDIR)/config || install -g edb -o enterprisedb -d $(DESTDIR)/config
	test -d $(DESTDIR)/log || install -g edb -o enterprisedb -d $(DESTDIR)/log

installAllDirs: installExDirs
	test -d $(DESTDIR)/docs || install -g edb -o enterprisedb -d $(DESTDIR)/docs


installServerJars: 
	install -m 640 -g edb -o enterprisedb $(LIBROOT)/xdbengine.jar $(DESTDIR)/bin
	install -m 640 -g edb -o enterprisedb $(LIBROOT)/xdbprotocol.jar $(DESTDIR)/bin
	install -m 640 -g edb -o enterprisedb $(LIBROOT)/xdbserver.jar $(DESTDIR)/bin

installClientJars: 
	install -m 644 -g edb -o enterprisedb $(LIBROOT)/xdbutil.jar $(DESTDIR)/bin

installServerScripts: 
	install -m 750 -g edb -o enterprisedb $(SRCROOT)/scripts/server/*.sh $(DESTDIR)/bin

installServerScriptsWindows:
	install -m 750 -g edb -o enterprisedb $(SRCROOT)/scripts/server/*.bat $(DESTDIR)/bin

installAgentScripts:
	install -m 750 -g edb -o enterprisedb $(SRCROOT)/scripts/agent/*.sh $(DESTDIR)/bin

installAgentScriptsWindows:
	install -m 750 -g edb -o enterprisedb $(SRCROOT)/scripts/agent/*.bat $(DESTDIR)/bin


installClientScripts:
	install -m 755 -g edb -o enterprisedb $(SRCROOT)/scripts/client/*.sh $(DESTDIR)/bin

installClientScriptsWindows:        
	install -m 755 -g edb -o enterprisedb $(SRCROOT)/scripts/client/*.bat $(DESTDIR)/bin


installCommonConfig:
	test -f $(DESTDIR)/gridsql_env.sh || \
		sed -e "s/--INSTALL_DIR--/`echo $(DESTDIR) | sed -e 's/\//\\\&/g'`/g" $(SRCROOT)/scripts/common/gridsql_env.sh > $(BUILDROOT)/gridsql_env.sh && \
		install -m 644 -g edb -o enterprisedb $(BUILDROOT)/gridsql_env.sh $(DESTDIR) || echo "Can not install: $(BUILDROOT)/gridsql_env.sh"

copyCommonConfig:
	install -m 644 -g edb -o enterprisedb $(SRCROOT)/scripts/common/gridsql_env.sh $(DESTDIR)


installServerConfigWE:
	# do not overwrite config files !
	for f in `ls -1 $(SRCROOT)/config/server`; do \
	    test -f $(DESTDIR)/config/$$f || \
		sed -e "s/--INSTALL_DIR--/`echo $(DESTDIR) | sed -e 's/\//\\\&/g'`/g" $(SRCROOT)/config/server/$$f > $(BUILDROOT)/$$f && \
		install -m 640 -g edb -o enterprisedb $(BUILDROOT)/$$f $(DESTDIR)/config || echo "Can not install: $(SRCROOT)/config/server/$$f"; \
	done

installServerConfigWEWindows:
	# do not overwrite config files !
	for f in `ls -1 $(SRCROOT)/config/server`; do \
	    test -f $(DESTDIR)/config/$$f || \
		sed -e "s/--INSTALL_DIR--/\/enterprisedb\/gridsql/g" $(SRCROOT)/config/server/$$f > $(BUILDROOT)/$$f && \
		install -m 640 -g edb -o enterprisedb $(BUILDROOT)/$$f $(DESTDIR)/config || echo "Can not install: $(SRCROOT)/config/server/$$f"; \
	done

copyServerConfigWE:
	# do not overwrite config files !
	for f in `ls -1 $(SRCROOT)/config/server`; do \
		install -m 640 -g edb -o enterprisedb $(SRCROOT)/config/server/$$f $(DESTDIR)/config; \
	done


copyAgentConfig:
	for f in `ls -1 $(SRCROOT)/config/agent`; do \
		install -m 640 -g edb -o enterprisedb $(SRCROOT)/config/agent/$$f $(DESTDIR)/config; \
	done

installAgentConfig:
	# do not overwrite config files !
	for f in `ls -1 $(SRCROOT)/config/agent`; do \
	    test -f $(DESTDIR)/config/$$f || \
		sed -e "s/--INSTALL_DIR--/`echo $(DESTDIR) | sed -e 's/\//\\\&/g'`/g" $(SRCROOT)/config/agent/$$f > $(BUILDROOT)/$$f && \
		install -m 640 -g edb -o enterprisedb $(BUILDROOT)/$$f $(DESTDIR)/config || echo "Can not install: $(SRCROOT)/config/agent/$$f"; \
	done

installAgentConfigWindows:
	# do not overwrite config files !
	for f in `ls -1 $(SRCROOT)/config/agent`; do \
	    test -f $(DESTDIR)/config/$$f && \
		sed -e "s/--INSTALL_DIR--/\/enterprisedb\/gridsql/g" $(SRCROOT)/config/agent/$$f > $(BUILDROOT)/$$f && \
		install -m 640 -g edb -o enterprisedb $(BUILDROOT)/$$f $(DESTDIR)/config || echo "Can not install: $(SRCROOT)/config/agent/$$f"; \
	done
	
installDocs: installNotes
	install -m 644 -g edb -o enterprisedb $(PROJROOT)/docs/*.PDF $(DESTDIR)/docs
	
installNotes:	
	install -m 644 -g edb -o enterprisedb $(PROJROOT)/misc/*.txt $(DESTDIR)

installServerLibs:
	install -m 644 -g edb -o enterprisedb $(LIBROOT)/log4j* $(LIBROOT)/edb* $(DESTDIR)/lib

installClientLibs:
	install -m 644 -g edb -o enterprisedb $(LIBROOT)/jline* $(LIBROOT)/log4j* $(LIBROOT)/edb* $(LIBROOT)/postgresql* $(DESTDIR)/lib

# careful with this
deleteSource:
	rm -rf $(SRCROOT)/com/edb/gridsql/common
	rm -rf $(SRCROOT)/com/edb/gridsql/communication
	rm -rf $(SRCROOT)/com/edb/gridsql/constraintchecker
	rm -rf $(SRCROOT)/com/edb/gridsql/engine
	rm -rf $(SRCROOT)/com/edb/gridsql/exception
	rm -rf $(SRCROOT)/com/edb/gridsql/metadata
	rm -rf $(SRCROOT)/com/edb/gridsql/misc
	rm -rf $(SRCROOT)/com/edb/gridsql/optimizer
	rm -rf $(SRCROOT)/com/edb/gridsql/parser
	rm -rf $(SRCROOT)/com/edb/gridsql/planner
	rm -rf $(SRCROOT)/com/edb/gridsql/protocol
	rm -rf $(SRCROOT)/com/edb/gridsql/queryproc
	rm -rf $(SRCROOT)/com/edb/gridsql/server
	rm -rf $(SRCROOT)/com/edb/gridsql/util

generateParser:
	cd edb/gridsql/Parser/ParserCore;make generate	

clean:
	- rm -R -d -f $(BUILDROOT)/*
	- rm -f $(LIBROOT)/xdbengine.jar
	- rm -f $(LIBROOT)/xdbengine_api.jar
	- rm -f $(LIBROOT)/xdbprotocol.jar
	- rm -f $(LIBROOT)/xdbserver.jar
	- rm -f $(LIBROOT)/xdbutil.jar

copy:
	rm -rf $(BUILDROOT)/*
	mkdir -p $(BUILDROOT)/cpy 
	cp -R $(SRCROOT)/* $(BUILDROOT)/cpy

cleanLogger: copy
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/engine 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/optimizer 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/metadata 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/metadata/scheduler 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/util
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/misc 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/misc/combinedresultset
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/parser 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/parser/handler 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/queryproc 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/exception 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/server 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/engine/datatypes 
	perl -w $(TOOLSPATH)/stripall_thorough.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/engine/datatypes 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/engine/io 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/common 
	perl -w $(TOOLSPATH)/stripone.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/common/util/SQLErrorHandler.java 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/constraintchecker 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/planner 
	perl -w $(TOOLSPATH)/stripall.pl  $(TOOLSPATH) \
		$(BUILDROOT)/cpy/com/edb/gridsql/communication 

buildengine: 
	mkdir -p $(BUILDROOT)/bin
	rm -fR $(BUILDROOT)/bin/*
	mkdir -p $(BUILDROOT)/src
	rm -fR $(BUILDROOT)/src/*
	cp -R  $(BUILDROOT)/cpy/* $(BUILDROOT)/src
	javac $(DEBUGINFO) -d $(BUILDROOT)/bin -classpath $(CLASSPATH) -sourcepath $(BUILDROOT)/src \
		$(BUILDROOT)/src/com/edb/gridsql/communication/ConnectorFactory.java \
		$(BUILDROOT)/src/com/edb/gridsql/communication/BroadcastConnector.java \
		$(BUILDROOT)/src/com/edb/gridsql/communication/ClusteredBroadcastConnector.java \
		$(BUILDROOT)/src/com/edb/gridsql/communication/ClusteredConnector.java \
		$(BUILDROOT)/src/com/edb/gridsql/communication/SocketConnector.java \
		$(BUILDROOT)/src/com/edb/gridsql/engine/io/RequestMessage.java \
		$(BUILDROOT)/src/com/edb/gridsql/parser/SqlAddNodesToDB.java \
		$(BUILDROOT)/src/com/edb/gridsql/parser/SqlDropNodesFromDB.java \
		$(BUILDROOT)/src/com/edb/gridsql/common/ErrorMessages.java \
		$(BUILDROOT)/src/com/edb/gridsql/protocol/ProtocolManager.java \
		$(BUILDROOT)/src/com/edb/gridsql/protocol/PgProtocolHandler.java
	jar cf $(LIBROOT)/xdbengine.jar \
		-C $(BUILDROOT)/src com/edb/gridsql/common/util/SQLErrorMessages.properties \
		-C $(BUILDROOT)/bin .
	jar cf $(LIBROOT)/xdbengine_api.jar \
		-C $(BUILDROOT)/bin com/edb/gridsql/common \
		-C $(BUILDROOT)/bin com/edb/gridsql/communication/AbstractAgent.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/communication/CoordinatorAgent.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/communication/NodeAgent.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/Engine.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/ExecutableRequest.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/ExecutionResult.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/XDBSessionContext.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/copy/CopyManager.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/io \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/loader/IUniqueValueProvider.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/loader/Loader.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/engine/loader/TableColumnDescription.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/exception/XDBGeneratorException.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/exception/XDBSecurityException.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/exception/XDBServerException.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/metadata/MetaData.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/metadata/SysColumn.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/metadata/SysLogin.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/metadata/NodeDBConnectionInfo.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/metadata/partitions/PartitionMap.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/metadata/scheduler/ILockCost.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/misc/Timer.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/parser/core/ParseException.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/parser/core/visitor/ObjectDepthFirst.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/parser/handler/TypeConstants.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/parser/IXDBSql.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/parser/Parser.class \
		-C $(BUILDROOT)/bin com/edb/gridsql/protocol/ProtocolManager.class 
		

buildprotocol: buildengine 
	mkdir -p $(BUILDROOT)/bin
	rm -fR $(BUILDROOT)/bin/*
	mkdir -p $(BUILDROOT)/src
	rm -fR $(BUILDROOT)/src/*
	mkdir -p $(BUILDROOT)/src/com/edb/gridsql/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/protocol $(BUILDROOT)/src/com/edb/gridsql/
	rm $(BUILDROOT)/src/com/edb/gridsql/protocol/ProtocolManager.java  
	javac $(DEBUGINFO) -d $(BUILDROOT)/bin -classpath $(CLASSPATH):$(LIBROOT)/xdbengine_api.jar -sourcepath $(BUILDROOT)/src \
		$(BUILDROOT)/src/com/edb/gridsql/protocol/PgProtocolHandler.java \
		$(BUILDROOT)/src/com/edb/gridsql/protocol/PgProtocolMessage.java \
		$(BUILDROOT)/src/com/edb/gridsql/protocol/PgProtocolSession.java 
	jar cf $(LIBROOT)/xdbprotocol.jar -C $(BUILDROOT)/bin .

buildserver: buildengine buildprotocol 
	mkdir -p $(BUILDROOT)/bin
	rm -fR $(BUILDROOT)/bin/*
	mkdir -p $(BUILDROOT)/src
	rm -fR $(BUILDROOT)/src/*
	mkdir -p $(BUILDROOT)/src/com/edb/gridsql/util/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/server $(BUILDROOT)/src/com/edb/gridsql/
	cp $(BUILDROOT)/cpy/com/edb/gridsql/util/CreateMdDb.java $(BUILDROOT)/src/com/edb/gridsql/util/
	cp $(BUILDROOT)/cpy/com/edb/gridsql/util/DbGateway.java $(BUILDROOT)/src/com/edb/gridsql/util/
	cp $(BUILDROOT)/cpy/com/edb/gridsql/util/ParseArgs.java $(BUILDROOT)/src/com/edb/gridsql/util/
	cp $(BUILDROOT)/cpy/com/edb/gridsql/util/PasswordPrompt.java $(BUILDROOT)/src/com/edb/gridsql/util/
	cp $(BUILDROOT)/cpy/com/edb/gridsql/util/XdbAgent.java $(BUILDROOT)/src/com/edb/gridsql/util/
	cp $(BUILDROOT)/cpy/com/edb/gridsql/util/XdbServer.java $(BUILDROOT)/src/com/edb/gridsql/util/
	javac $(DEBUGINFO) -d $(BUILDROOT)/bin -classpath $(CLASSPATH):$(LIBROOT)/xdbengine_api.jar:$(LIBROOT)/xdbprotocol.jar -sourcepath $(BUILDROOT)/src \
		$(BUILDROOT)/src/com/edb/gridsql/util/XdbAgent.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/CreateMdDb.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/XdbServer.java
	jar cf $(LIBROOT)/xdbserver.jar -C $(BUILDROOT)/bin .

buildutil:  
	mkdir -p $(BUILDROOT)/bin
	rm -fR $(BUILDROOT)/bin/*
	mkdir -p $(BUILDROOT)/src
	rm -fR $(BUILDROOT)/src/*
	mkdir -p $(BUILDROOT)/src/com/edb/gridsql/common/util
	mkdir -p $(BUILDROOT)/src/com/edb/gridsql/engine/io
	mkdir -p $(BUILDROOT)/src/com/edb/gridsql/engine/loader
	mkdir -p $(BUILDROOT)/src/com/edb/gridsql/exception
	mkdir -p $(BUILDROOT)/src/com/edb/gridsql/misc
	mkdir -p $(BUILDROOT)/src/com/edb/gridsql/parser/handler
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/common/util/OutputFormatter.java $(BUILDROOT)/src/com/edb/gridsql/common/util/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/common/util/ParseCmdLine.java $(BUILDROOT)/src/com/edb/gridsql/common/util/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/common/util/Property.java $(BUILDROOT)/src/com/edb/gridsql/common/util/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/common/util/Props.java $(BUILDROOT)/src/com/edb/gridsql/common/util/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/common/util/XLevel.java $(BUILDROOT)/src/com/edb/gridsql/common/util/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/common/util/XLogger.java $(BUILDROOT)/src/com/edb/gridsql/common/util/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/engine/copy $(BUILDROOT)/src/com/edb/gridsql/engine/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/engine/io/DataTypes.java $(BUILDROOT)/src/com/edb/gridsql/engine/io/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/engine/io/MessageTypes.java $(BUILDROOT)/src/com/edb/gridsql/engine/io/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/engine/io/XMessage.java $(BUILDROOT)/src/com/edb/gridsql/engine/io/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/engine/loader/TableColumnDescription.java $(BUILDROOT)/src/com/edb/gridsql/engine/loader/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/exception/ErrorMessageRepository.java $(BUILDROOT)/src/com/edb/gridsql/exception/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/exception/XDBServerException.java $(BUILDROOT)/src/com/edb/gridsql/exception/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/misc/Timer.java $(BUILDROOT)/src/com/edb/gridsql/misc/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/parser/handler/TypeConstants.java $(BUILDROOT)/src/com/edb/gridsql/parser/handler/
	cp -R  $(BUILDROOT)/cpy/com/edb/gridsql/util $(BUILDROOT)/src/com/edb/gridsql/
	javac $(DEBUGINFO) -d $(BUILDROOT)/bin -classpath $(CLASSPATH) -sourcepath $(BUILDROOT)/src \
		$(BUILDROOT)/src/com/edb/gridsql/util/CmdLine.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/CreateDb.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/DropDb.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/ExecDb.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/XdbDbStart.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/XdbDbStop.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/XdbImpEx.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/XdbLoader.java \
		$(BUILDROOT)/src/com/edb/gridsql/util/XdbShutdown.java

	jar cf $(LIBROOT)/xdbutil.jar -C $(BUILDROOT)/bin . 
		
