#!/usr/bin/env bash

# F_002-内网
HOSTS[0]="172.16.186.81"
# G_001-内网
HOSTS[1]="172.16.1.178"

USERNAME="root"
PASSWORD="yunche@2018"

COPY_JAR_SOURCE_PATH="/root/yunche-biz/pub/yunche-biz.jar"
COPY_JAR_DEST_DIR="/root/yunche-biz/pub/"

# 300s
SPAWN_TIME_OUT=300

# start.sh 路径˜
START_SH_PATH="/root/yunche-biz/pub/start.sh"


echo -e "发布开始 >>>>>>>>>>>>>>>>>>>>>>>>>>>>\n"


echo -e "开始 COPY_JAR >>>>>>>>>>>>>>\n"

for HOST in ${HOSTS[@]}
do
    echo -e "[ COPY_JAR TO >>>>>>  $HOST ]\n"

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

echo -e "结束 COPY_JAR <<<<<<<<<<<<<<\n"


echo -e "开始执行远程start.sh >>>>>>>>>>>>>>\n"

for HOST in ${HOSTS[@]}

do
    echo -e "[ 执行远程start.sh  >>>>>>  $HOST ]\n"

    /usr/bin/expect <<EOF
    set timeout $SPAWN_TIME_OUT
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

echo -e "结束执行远程start.sh <<<<<<<<<<<<<<\n"


echo -e "开始执行本地start.sh >>>>>>>>>>>>>>\n"

KILL_PROCESS="server.port=7002"
JAR_PATH="/root/yunche-biz/pub/yunche-biz.jar"
JAR_PORT="7002"
JAR_PROFILES="pub"

# kill      $2 -> result[1] - pid
PIDS=`ps -ef|grep $KILL_PROCESS | grep -v grep|grep -v PPID|awk '{print $2}'`
for PID in $PIDS
do
  echo -e "Kill the $KILL_PROCESS process [ $PID ]"
  kill -9 $PID
done

# 部署
nohup  java -jar $JAR_PATH --server.port=$JAR_PORT --spring.profiles.active=$JAR_PROFILES &

rm -rf nohup.out

echo -e "结束执行本地start.sh <<<<<<<<<<<<<<\n"


echo -e "发布成功 <<<<<<<<<<<<<<<<<<<<<<<<<<<<\n"





