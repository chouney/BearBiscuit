#!bin/bash
profile=dev
if [ 1 == $# ] ; then
    profile=$1
fi
#shutdown
sh shutdown.sh

PROJECT_NAME=xkr
BASE_DIR=/home/admin
CODE_DIR=$BASE_DIR/www-logic/$PROJECT_NAME
cd $CODE_DIR
mvn clean install -Dmaven.test.skip=true
java_opt="--spring.profiles.active=${profile} -Xms256M -Xmx256M -Xmn64M -XX:ErrorFile=${CODE_DIR}/logs/jvm-error-pid.log  -XX:HeapDumpPath=${CODE_DIR}/logs/java_pid.hprof XX:-HeapDumpOnOutOfMemoryError"
if [ $profile != pro ] ; then
    java_opt=${java_opt}" -Xdebug -Xrunjdwp,transport=dt_socket,server=y,address=8301,suspend=n,onthrow=java.io.IOException,launch=/sbin/echo"
fi
echo ${JAVA_HOME}/bin/java -d64 -jar ${CODE_DIR}/target/xkr.jar $java_opt
nohup ${JAVA_HOME}/bin/java -jar ${CODE_DIR}/target/xkr.jar $java_opt > /dev/null &

#check app
APP_NAME=xkr.jar
tpid=`ps -ef|grep $APP_NAME|grep -v grep|grep -v kill|awk '{print $2}'`
for (( i=1; i<100;i++))
    do
        echo 'RUNNING APP HealthCheck!'
        if [ ${tpid} ]; then
            echo 'App is running SUCCESS!!!!!!.'
            break
        else
            sleep 1
        fi
    done
