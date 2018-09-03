#!/usr/bin/env bash

# F_002
HOSTS[0]="118.31.39.192"
# G_001
HOSTS[1]="47.98.242.21"

USERNAME="root"
PASSWORD="yunche@2018"

COPY_JAR_SOURCE_PATH="/root/yunche-biz/pub/yunche-biz.jar"
COPY_JAR_DEST_DIR="/root/yunche-biz/pub/"

# 300s
SPAWN_TIME_OUT=300

# start.sh 路径
START_SH_PATH="/root/yunche-biz/pub/run.sh"


echo "发布开始 >>>>>>>>>>>>>>>>>>>>>>>>>>>>"


echo "开始 COPY_JAR >>>>>>>>>>>>>>"

for HOST in $HOSTS
do
    echo "[ COPY_JAR TO >>>>>>  $HOST ]"

/usr/bin/expect <<EOF
set timeout $SPAWN_TIME_OUT
	spawn scp $COPY_JAR_SOURCE_PATH $USERNAME@$HOST:$COPY_JAR_DEST_DIR
    expect {
     "(yes/no)?"
      {
        send "yes\n"
        expect "*assword:" { send "$PASSWORD\n"}
      }
     "*assword:"
      {
        send "$PASSWORD\n"
      }
    }
    expect "100%"
    expect eof
EOF
done

echo "结束 COPY_JAR <<<<<<<<<<<<<<"


echo "开始执行远程start.sh >>>>>>>>>>>>>>"

for HOST in $HOSTS
do
    echo "[ 执行远程start.sh  >>>>>>  $HOST ]"

/usr/bin/expect <<EOF
    spawn ssh $USERNAME@$HOST "sh $START_SH_PATH"
    expect {
     "(yes/no)?"
      {
        send "yes\n"
        expect "*assword:" { send "$PASSWORD\n"}
      }
     "*assword:"
      {
        send "$PASSWORD\n"
      }
    }
    expect "100%"
    expect eof
EOF
done

echo "结束执行远程start.sh <<<<<<<<<<<<<<"


echo "开始执行本地start.sh >>>>>>>>>>>>>>"

KILL_PROCESS="server.port=7002"
JAR_PATH="/root/yunche-biz/pub/yunche-biz.jar"
JAR_PORT="7002"
JAR_PROFILES="pub"

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

echo "结束执行本地start.sh <<<<<<<<<<<<<<"


echo "发布成功 <<<<<<<<<<<<<<<<<<<<<<<<<<<<"





