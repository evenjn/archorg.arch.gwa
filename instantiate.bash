#!/bin/bash

if [ -z "$1" ] || [ -z "$2" ]
then
  echo "ERROR: no arguments provided"
  exit
fi

orgID=$1
prodID=$2
projectID=${orgID}.${prodID}
templateID=arch.gwa

if [[ ! ${orgID} =~ ([a-z][a-z0-9]*)(\.[a-z][a-z0-9]*)* ]]
then
  echo "ERROR: organization id is not suitable as a java package name"
  exit
fi

if [[ ! ${prodID} =~ ([a-z][a-z0-9]*)(\.[a-z][a-z0-9]*)* ]]
then
  echo "ERROR: artifact id is not suitable as a java package name"
  exit
fi

sed -i "s/archorg/${orgID}/g" pom.xml
sed -i "s/archorg.${templateID}/${projectID}/g" pom.xml
sed -i "s/${templateID}/${prodID}/g" pom.xml
sed -i "s/archorg.${templateID}/${projectID}/g" .project
sed -i "s/archorg.${templateID}/${projectID}/g" src/main/resources/applicationContext.xml
sed -i "s/archorg.${templateID}/${projectID}/g" src/main/java/archorg/arch/jar/webservice/server/Server.java
sed -i "s/archorg.${templateID}/${projectID}/g" src/main/java/archorg/arch/jar/webservice/server/ServiceImpl.java
sed -i "s/archorg.${templateID}/${projectID}/g" src/test/java/archorg/arch/jar/webservice/server/TestServer.java

arr=$(echo ${projectID} | tr "." "\n")
cd src/main/java
mv archorg ..
path=""
for x in ${arr}
do
  if [ -n "${path}" ]
  then
    path="${path}/"
  fi
  path="${path}${x}"
  mkdir ${path}
done
mv ../archorg/arch/jar/webservice/server/* ${path}
rm -rf ../archorg
cd ../../..
cd src/test/java
mv archorg ..
path=""
for x in ${arr}
do
  if [ -n "${path}" ]
  then
    path="${path}/"
  fi
  path="${path}${x}"
  mkdir ${path}
done
mv ../archorg/arch/jar/webservice/server/* ${path}
rm -rf ../archorg
cd ../../..

sed -i "s,archorg/arch/jar/webservice/server,${path},g" src/main/resources/start.bash

git add .project
git add pom.xml
git rm -r --cached src/main/java/archorg
git rm -r --cached src/test/java/archorg
git add src
