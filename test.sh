hive -v -e '
set mapred.child.java.opts="-JJ -Xmx10G -XX:PermSize=512m -XX:MaxPermSize=512m -XX:+UseConcMarkSweepGC -XX:-UseGCOverheadLimit";
set hive.mapred.local.mem = 163840;
set mapreduce.map.memory.mb=163840;
set mapreduce.reduce.memory.mb=163840;
set hive.merge.mapfiles=false;
set hive.input.format=org.apache.hadoop.hive.ql.io.HiveInputFormat;
set hive.metastore.connect.retries = 1;
set mapreduce.input.fileinputformat.split.minsize = 1000;
set mapred.map.tasks = 10;
set mapred.reduce.tasks = 10;
ADD JAR localjars/h2o-genmodel.jar;
ADD JAR target/ScoreData-1.0-SNAPSHOT.jar;
CREATE TEMPORARY FUNCTION fn AS "ai.h2o.hive.udf.ScoreDataUDF";
'

