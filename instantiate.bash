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
gwaID=$(echo ${projectID} | tr "." "_")

if [[ ! ${orgID} =~ (^([a-z][a-z0-9]*)((\.[a-z][a-z0-9]*)*)$) ]]
then
  echo "ERROR: organization id is not suitable as a java package name"
  exit
fi

if [[ ! ${prodID} =~ (^([a-z][a-z0-9]*)((\.[a-z][a-z0-9]*)*)$) ]]
then
  echo "ERROR: artifact id is not suitable as a java package name"
  exit
fi

sed -i "s/archorg_arch_gwa/${gwaID}/g" src/main/java/archorg/arch/gwa/archorg_arch_gwa.gwt.xml
sed -i "s/archorg_arch_gwa/${gwaID}/g" src/main/webapp/WEB-INF/web.xml
sed -i "s/archorg_arch_gwa/${gwaID}/g" src/main/webapp/main.html
sed -i "s/archorg/${orgID}/g" pom.xml
sed -i "s/archorg/${orgID}/g" .project
sed -i "s/archorg/${orgID}/g" src/main/java/archorg/arch/gwa/client/Client.java
sed -i "s/archorg/${orgID}/g" src/main/java/archorg/arch/gwa/client/Properties.java
sed -i "s/archorg/${orgID}/g" src/main/java/archorg/arch/gwa/client/Service.java
sed -i "s/archorg/${orgID}/g" src/main/java/archorg/arch/gwa/client/ServiceAsync.java
sed -i "s/archorg/${orgID}/g" src/main/java/archorg/arch/gwa/server/ServiceImpl.java
sed -i "s/archorg/${orgID}/g" src/main/java/archorg/arch/gwa/shared/FieldVerifier.java
sed -i "s/archorg/${orgID}/g" src/main/java/archorg/arch/gwa/archorg_arch_gwa.gwt.xml
sed -i "s/archorg/${orgID}/g" src/main/webapp/WEB-INF/web.xml
sed -i "s/archorg/${orgID}/g" src/main/webapp/main.html
sed -i "s/${templateID}/${prodID}/g" pom.xml
sed -i "s/${templateID}/${prodID}/g" .project
sed -i "s/${templateID}/${prodID}/g" src/main/java/archorg/arch/gwa/client/Client.java
sed -i "s/${templateID}/${prodID}/g" src/main/java/archorg/arch/gwa/client/Properties.java
sed -i "s/${templateID}/${prodID}/g" src/main/java/archorg/arch/gwa/client/Service.java
sed -i "s/${templateID}/${prodID}/g" src/main/java/archorg/arch/gwa/client/ServiceAsync.java
sed -i "s/${templateID}/${prodID}/g" src/main/java/archorg/arch/gwa/server/ServiceImpl.java
sed -i "s/${templateID}/${prodID}/g" src/main/java/archorg/arch/gwa/shared/FieldVerifier.java
sed -i "s/${templateID}/${prodID}/g" src/main/java/archorg/arch/gwa/archorg_arch_gwa.gwt.xml
sed -i "s/${templateID}/${prodID}/g" src/main/webapp/WEB-INF/web.xml
sed -i "s/${templateID}/${prodID}/g" src/main/webapp/main.html


mv src/main/java/archorg/arch/gwa/archorg_arch_gwa.gwt.xml src/main/java/archorg/arch/gwa/${gwaID}.gwt.xml

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
mv ../archorg/arch/gwa/{[^.]*,.[^.]*,..?*} ${path}
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
mv ../archorg/arch/gwa/{[^.]*,.[^.]*,..?*} ${path}
rm -rf ../archorg
cd ../../..

git add .project
git add pom.xml
git rm -r --cached src/main/java/archorg
git rm -r --cached src/test/java/archorg
git add src
