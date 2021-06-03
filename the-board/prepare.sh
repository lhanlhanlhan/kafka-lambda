#!/bin/sh

# Create casandra schema
docker exec b-cassandra cqlsh --username cassandra --password cassandra  -f /spark-schema.cql
# Create Kafka topic "board-data-event"
docker exec b-kafka kafka-topics --create --topic board-data-event --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zookeeper:2181
# Install libc6-compat lib i both sparks containers
#docker exec spark-master apk add --no-cache libc6-compat
#docker exec spark-worker-1 apk add --no-cache libc6-compat
# Create our folders on Hadoop file system and total permission to those
docker exec b-hadoop-namenode hdfs dfs -mkdir /lambda-arch
docker exec b-hadoop-namenode hdfs dfs -mkdir /lambda-arch/checkpoint
docker exec b-hadoop-namenode hdfs dfs -chmod -R 777 /lambda-arch
docker exec b-hadoop-namenode hdfs dfs -chmod -R 777 /lambda-arch/checkpoint



