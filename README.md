# KafkaLambda



docker exec b-spark-master /spark/bin/spark-submit --class cn.hanli.mw.board.stream.StreamProcessorApplication --master spark://spark-master:7077 /app/stream-processor-1.0.0.jar