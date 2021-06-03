# KafkaLambda

Lambda

## Requirements

1. Docker (Desktop);
2. Node.js;
3. Vue.js;
4. Java Development Kit version 1.8, or 11 and above;
5. Maven; that which embedded inside IntelliJ IDEA is preferred;

## Steps

In directory ```the-board```, go through the following steps:

1. Create containers using

   ```shell
   docker compose -p kafka-lambda create
   ```

2. Start the containers using

   ```shell
   docker compose -p kafka-lambda start
   ```

   to run in daemon, or try

   ```shell
   docker compose -p kafka-lambda up
   ```

   to run containers with logs being printed to the console.

3. Prepare environment settings using the shell script:

   ```shell
   sh ./prepare.sh
   ```

   This is a essential startup step in which a series of bootstrap operations are performed, e.g. creating database tables, HDFS directories, Kafka topics, etc.

4. Maven Package for the ```Trade Board``` maven project.

5. Run Stream Processor inside the container ```b-spark-master```, through:

   ```shell
   docker exec b-spark-master /spark/bin/spark-submit --class cn.hanli.mw.board.stream.StreamProcessorApplication --master spark://spark-master:7077 /app/stream-processor-1.0.0.jar
   ```

6. If everything seems normal, run Data Producer on host machine:

   ```shell
   java -jar data-producer-1.0.0.jar
   ```

7. Run Big Board SpringBoot Backend program:

   ```shell
   java -jar big-board-1.0.0.jar
   ```

In directory ```frontend```, run the following:

1. Install node packages:

   ```shell
   npm install
   ```

2. Run development node.js server:

   ```shell
   npm run serve
   ```

3. Open ```http://localhost:3000/``` to see if the Big Board page is correctly rendered and if the numbers and/or charts are continuously updated.

To gracefully stop the development servers - 

1. Terminate the node.js development server by simultaneously tapping ```Ctrl + C``` in its corresponding terminal;

2. Terminate the Big Board SpringBoot Backend program and the Data Producer;

3. Terminate the Stream Processor program, likewise, by ```Ctrl + C```;

4. Stop all containers by running below inside the ```the-board``` directory:

   ```shell
   docker compose -p kafka-lambda stop
   ```

5. If necessary, remove the containers by running:

   ```
   docker compose -p kafka-lambda rm
   ```

   This may seem trivial if everything goes normally. Nevertheless, a remove and create operation towards the whole docker application may be helpful if something weird or inadequate happened during the aformentioned progress.

