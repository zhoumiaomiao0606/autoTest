#!/usr/bin/env bash

KILL_PROCESS="server.port=7002"

JAR_PATH="/root/yunche-biz/pub/yunche-biz.jar"
JAR_PORT="7002"
JAR_PROFILES="pub"


echo "开始执行start.sh >>>>>>>>>>>>>>"

# kill
PIDS=`ps -ef|grep $KILL_PROCESS | grep -v grep|grep -v PPID|awk '{print $pid_}'`
for PID in $PIDS
do
  echo "Kill the $KILL_PROCESS process [ $PID ]"
  kill -9 $PID
done

# 部署
nohup  java -jar $JAR_PATH --server.port=$JAR_PORT --spring.profiles.active=$JAR_PROFILES &

rm -rf nohup.out

echo "结束执行start.sh <<<<<<<<<<<<<<"