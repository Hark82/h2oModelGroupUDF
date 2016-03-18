1. Train models using H2O
2. Download POJOs & place in ./src/main/java/ai/h2o/hive/udf/models
3. Download h2o-genmodel & place into ./localjars
4. Run python ./scripts/setup.py to add package info to POJOs & create static class for reflection in the UDF
5. mvn clean && mvn compile... etc.
6. Run ScoreDataHQLGenerator to get Hive output, direct output into a new .hql file if you like & run 'source file.hql' in Hive. You may have to update the JAR paths.

If models are trained using different versions of H2O the behavior is undefined. YMMV.

TODO: Update this doc
