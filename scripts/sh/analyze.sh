#!/bin/bash
PGM_NAME=target/ImpactAnalyze.jar

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