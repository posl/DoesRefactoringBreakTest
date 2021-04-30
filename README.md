<H1>Replication package of "Does Refactoring Break Tests and to What Extent?"</H1>
This repository includes the replication package and results of an ICSME 2021's submission. 
This program can run on your local computer or Kubernetes server. We strongly recommend to run it on Kubernetes because builds, tests, and the impact analysis take numorous time to be completed. 

# Results
All the results that we used are located in "results_in_paper" directory
</Br>
</Br>
# Source code
Our source code is located in "src" directory. 

## Require
- JDK 13
- Git (>2.24)
- Maven (>3.6.1)
- Postgres (>12.1)

## Settings
**1. Install JDK on your local**

**2. Set JAVA Environment (JAVA_HOME)**


**3. Set Maven Environment Variables (M2_HOME)**

- e.g., export M2_HOME=/usr/local/Cellar/maven/3.6.3_1/


**4. Clone this repostitory and Change directory (cd) to the repository directory**

**5. modify ini files for your computer (optional)**

- settings/base.ini
	- data_dir=[directory where you want to put the output]
- settings/projects/[your project name].pj
	- name=[your project name]
	- abb=[your project abbr name]
	- url=[url of your project repository]


**6. Install and setup Postgres**

&nbsp;&nbsp;&nbsp;&nbsp;6.0. Install and login postgres

&nbsp;&nbsp;&nbsp;&nbsp;6.1. create role
```
CREATE ROLE test_break LOGIN PASSWORD 'PASSWORD HERE'; 
```

&nbsp;&nbsp;&nbsp;&nbsp;6.2. exit from postgres
  
&nbsp;&nbsp;&nbsp;&nbsp;6.3. login
```
psql -Utest_break
```
  
&nbsp;&nbsp;&nbsp;&nbsp;6.4. create a database
```
CREATE DATABASE test_break
```
  
&nbsp;&nbsp;&nbsp;&nbsp;6.5. give all the privileges on the created database to the created user
```
GRANT ALL PRIVILEGES ON DATABASE test_break TO test_break;
```

**7. Run a sql script (script/sql/init.sql) to create a database and schemas**
```
psql -Utest_break < scripts/sql/init.sql 
```

**8. Modify src/main/resource/META-INF/persistence.xml for your database**
- property name="hibernate.connection.url" value="jdbc:postgresql://[IP ADDRESS]:5432/test_break" 
- property name="hibernate.connection.password" value="[PASSWORD]" 

**9. Make jar files**
```
mvn package -Dmaven.test.skip
```

## Run programs
Run the following shell scripts on Kubernetes or local. 
To run on docker, you need the following four steps (If you want to run it on local, go to step 5):

**1. Make a docker image and push it to DockerHub (the image should be public)**
```
docker image build -t {docker image name}:{tag} .
docker push {docker image name}
```
**2. Fill your environment information for kubernetes and docker into "kube/sample.yaml"**
- {number of replicas}: How many containers do you want to run on a node
- {docker image name}: your docker container name in DockerHub

```
apiVersion: apps/v1
kind: Deployment
metadata:
  name: test-break
spec:
  selector:
    matchLabels:
      app: test-break
  replicas: **{number of replicas}**
  template:
    metadata:
      name: test-break
      labels:
        app: test-break
    spec:
      containers:
        - name: test-break
          image: **{docker image name}**
          imagePullPolicy: Always
          command:
          - "sh"
            - "-c"
            - "cd /usr/work/ \
                   && sh scripts/sh/**{shell script file name}**.sh"
```

**3. Create the following two files and copy-paste the contents of sample file.**

- run_both.yaml

- analyze.yaml


**4. Replace {shell script file name} with the name of the yaml file.**

For example,
- run_both.yaml: sh scripts/sh/run_both.sh
- analyze.yaml : sh scripts/sh/analyze.sh


**5. Run Programs**

&nbsp;&nbsp;&nbsp;&nbsp;5.1. Execute init.sh to add commits and refactorings data to database and select a number from the list of projects shown in your console. 

```
sh scripts/sh/init.sh 
```


&nbsp;&nbsp;&nbsp;&nbsp;5.2. Run build and test
- On Kubernetes
```
kubectl apply -f kube/run_both.yaml
```
- On local
```
sh scripts/sh/run_both.sh 
```

&nbsp;&nbsp;&nbsp;&nbsp;5-3. Run impact analysis
- On Kubernetes
```
kubectl apply -f kube/analyze.yaml
```
- On local
```
sh scripts/sh/analyze.sh
```


&nbsp;&nbsp;&nbsp;&nbsp;5-4. Calculate results for RQ1 and RQ2
```
sh scripts/sh/test-breaks.sh 
sh scripts/sh/modified-lines.sh 
```


&nbsp;&nbsp;&nbsp;&nbsp;5-5. You can download the results in csv file from database
These result will be shown in ./outputs/{project name}.csv
```
sh scripts/sh/get-results.sh 
```





