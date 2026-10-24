#!/bin/sh

# Set default values for variables
APP_BASE_NAME=`basename "$0"`
APP_HOME="`pwd -P`"
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Resolve links: $0 may be a link
PRG="$0"
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
        PRG="$link"
    else
        PRG=`dirname "$PRG"`"/$link"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
else
    JAVACMD="java"
fi

WARP_JAR="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# Download wrapper jar if not present
if [ ! -e "$WARP_JAR" ]; then
    mkdir -p "$APP_HOME/gradle/wrapper"
    echo "Downloading gradle-wrapper.jar..."
    curl -sLo "$WARP_JAR" https://raw.githubusercontent.com/gradle/gradle/v8.9.0/gradle/wrapper/gradle-wrapper.jar || true
fi

exec "$JAVACMD" "-Dorg.gradle.appname=$APP_BASE_NAME" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
