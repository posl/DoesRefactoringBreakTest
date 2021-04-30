#!/bin/bash
mvn package -Dmaven.test.skip
java -jar target/ExeInitialization.jar