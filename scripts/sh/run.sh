#!/bin/bash
if [ "$1" = "CROSS" ];then
  PGM_NAME=target/CROSS.jar
elif [ "$1" = "STRAIGHT" ]; then
  PGM_NAME=target/STRAIGHT.jar
else
  echo "an arg is needed: 'CROSS' or 'STRAIGHT'"
  exit 1
fi

while true
do
  java -jar $PGM_NAME
  RETURN_CODE=$?
  if [ $RETURN_CODE = 0 ]; then
     echo "${PGM_NAME}－Completed"
  else
     echo "${PGM_NAME}－Error"
     break
  fi
done