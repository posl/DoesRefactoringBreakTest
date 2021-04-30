#!/bin/bash
while true
do
  java -jar target/STRAIGHT.jar
  RETURN_CODE=$?
  if [ $RETURN_CODE = 0 ]; then
     echo "STRAIGHT－Completed"
  else
     echo "STRAIGHT－Error"
     break
  fi
done

while true
do
  java -jar target/CROSS.jar
  RETURN_CODE=$?
  if [ $RETURN_CODE = 0 ]; then
     echo "CROSS－Completed"
  else
     echo "CROSS－Error"
     break
  fi
done