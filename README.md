## Overview

This tutorial describes how to wrap several models in a Hive UDF for scoring directly in Hive. We will use the Abalone dataset (https://archive.ics.uci.edu/ml/datasets/Abalone) to demonstrate training models in H2O, exporting the models as POJOs, wrapping them in a Hive UDF, and finally scoring data in Hive using the UDF.

## Requirements

- The code in this repository:
```
git clone https://github.com/nkarpov/h2oModelGroupUDF
```
- Hadoop/Hive
- Python to run setup.py (almost any version will work)
- Java & Maven (to compile & package UDF jar)

## Workflow

### 1. Train H2O Models
Load data into H2O
Train N models
Use any of the client APIs (Python, R, Flow)

### 2. Download H2O Pojos & Dependency JAR
Download each pojo & put them in src/main/java/ai/h2o/hive/udf/models/
Download h2o-genmodel & put it in /localjars

### 3. Run setup.py
Run setup.py to create static Models.java class & add package to the top of each model definition.

### 4. Compile & Package UDF JAR
From the root directory of this project, run:
```
mvn clean && mvn compile && mvn package -Dmaven.test.skip=true && java -cp ./localjars/h2o-genmodel.jar:./target/ScoreData-1.0-SNAPSHOT.jar:./localjars/commons-lang3-3.4.jar ai.h2o.hive.udf.ScoreDataHQLGenerator
```
This cleans any current builds, compiles & packages (skipping tests), & runs ScoreDataHQLGenerator -- which outputs the HQL that should be run in Hive. You can > the output directly into a h2omodelgroup.hql file & then run `source h2omodelgroup.hql` in Hive to apply (**check to make sure the paths to the two JARS are correct!**)

Upload the h2o-genmodel.jar & ScoreData-1.0-SNAPSHOT.jar somewhere you can access from Hive. You can keep it on the local filesystem or put it on the Hadoop FS - either way will work as long as you keep in mind the paths when running "ADD JAR ..."

### 5. Scoring in Hive
The above command will generate HQL similar to below:
```
-- model order (alphabetical)
-- ai.h2o.hive.udf.models.deeplearning_741ae095_5cc4_415c_a726_f4e26762f3fa
-- ai.h2o.hive.udf.models.drf_99268734_ddf8_43b3_87e4_84f092df5292
-- ai.h2o.hive.udf.models.gbm_094fceb2_48a2_4447_931b_2aeed114c08a
-- ai.h2o.hive.udf.models.glm_a00273fc_04a0_4c0c_b5a6_22fba753ca1b

-- add jars
ADD JAR localjars/h2o-genmodel.jar;
ADD JAR target/ScoreData-1.0-SNAPSHOT.jar;

-- create fn definition
CREATE TEMPORARY FUNCTION fn AS "ai.h2o.hive.udf.ScoreDataUDF";

-- column names reference
set hivevar:scoredatacolnames=C1,C2,C3,C4,C5,C6,C7,C8

-- sample query, returns nested array
-- select fn(${scoredatacolnames}) from TABLEWITHAPPROPRIATEDATA
```

- You must make sure the JARs are added correctly (double check the paths & update, particularly if you put them onto Hadoop FS itself)
- Run CREATE TEMPORARY FUN... 
- Refer to the sample select query to structure your scoring query
