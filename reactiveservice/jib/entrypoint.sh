#!/bin/sh

/scripts/rep-env-localize /app/resources

# initialize the variables for the service
if [ -f "/app/resources/init.sh" ]
then
	sh /app/resources/init.sh
fi

#FIXME: for debugging don't use exec
if [ -z "$RD_DEBUG" ]
then
	exec java ${DEBIAN_JAVA_OPTS} ${JAVA_OPTS} -cp /app/resources/:/app/classes/:/app/libs/* ${debianMainClass}  "$@"
else
	echo $0: Debugging mode.  POD will delay restarting for 8 hours.
	java ${DEBIAN_JAVA_OPTS} ${JAVA_OPTS} -cp /app/resources/:/app/classes/:/app/libs/* ${debianMainClass}  "$@"
	sleep 999d
fi
