#!bin/bash
PROJECT_HOME=/home/admin/www-logic/xkr
mvn install:install-file -Dfile=${PROJECT_HOME}/lib/validator-1.0.jar -DgroupId=local -DartifactId=validator -Dversion=1.0 -Dpackaging=jar
mvn install:install-file -Dfile=${PROJECT_HOME}/lib/unrar-1.0.jar -DgroupId=local -DartifactId=unrar -Dversion=1.0 -Dpackaging=jar