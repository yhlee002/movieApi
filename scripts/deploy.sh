#!/bin/bash

DEPLOY_PATH=$(ls /home/ec2-user/app/api/*.jar)
JAR_NAME=$(basename $DEPLOY_PATH)
echo ">>> build 파일명: $JAR_NAME" >> /home/ec2-user/app/api/deploy.log

echo ">>> 현재 실행중인 애플리케이션 pid 확인" >> /home/ec2-user/app/api/deploy.log
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo ">>> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다." >> /home/ec2-user/app/api/deploy.log
else
  echo ">>> kill -15 $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR=$DEPLOY_PATH
echo ">>> DEPLOY_JAR 배포"    >> /home/ec2-user/app/api/deploy.log
nohup java -jar -Dserver.port=8080 $DEPLOY_JAR > /home/ec2-user/app/api/logs/server.log 2>&1 < /dev/null &
