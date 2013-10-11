#!/bin/bash
export PATH=/usr/local/cuda/bin:/usr/local/bin:/usr/bin:/bin:/usr/local/games:/usr/games:/usr/lib/jvm/java-7-oracle/bin:/usr/lib/jvm/java-7-oracle/db/bin:/usr/lib/jvm/java-7-oracle/jre/bin:/home/hadoop/bin:/home/hadoop
echo $PATH
export JAVA_HOME=/usr/lib/jvm/java-7-oracle
export HADOOP_HOME=/home/hadoop

NOW=$(date +"%m%d%Y")

dirpath="s3://stock-news-feed/$NOW/QUOTE/"
echo $dirpath

hive -hiveconf S3_PATH=$dirpath TODAY_DATE=$NOW -f /home/hadoop/hivescript.hql

