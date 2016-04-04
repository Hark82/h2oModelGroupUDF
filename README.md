## Overview

The code in this repo allows you to group H2O POJO models together into a Hive UDF for scoring on Hive tables.

Supports:
- Different model types 
- Different predictors

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
Train your H2O models as you would normally using the WebUI (Flow), or Python/R client APIs

### 2. Download H2O Pojos & Dependency JAR
- Download the POJOs and the h2o-genmodel.jar dependency
- Place h2o-genmode.jar into localjars/
- Place the H2O POJOs into src/main/java/ai/h2o/hive/udf/models/
Note: The h2o-genmodel.jar must be from the save version that was used to build the models!

### 3. Run setup.py 
The setup.py script in scripts/ takes 1 argument & does 3 things:
- Writes package info (package ai.h2o.hive.udf.models;) to the top of each POJO
- Creates Models.java, a static class used to store the H2O POJO names for reflection during runtime
- Generates a pom.xml file w/ the artifactId = [argument]

Ex:
```
python setup.py MyModels
```

setup.py will also output the command you should run to compile your final JAR & generate the HQL to be used in Hive:

```
mvn clean && mvn compile && mvn package -Dmaven.test.skip=true && java -cp ./localjars/h2o-genmodel.jar:./target/MyModels-1.0-SNAPSHOT.jar ai.h2o.hive.udf.ScoreDataHQLGenerator
```

### 4. Compile & Package UDF JAR

From the root directory of this project, run:
```
mvn clean && mvn compile && mvn package -Dmaven.test.skip=true && java -cp ./localjars/h2o-genmodel.jar:./target/MyModels-1.0-SNAPSHOT.jar ai.h2o.hive.udf.ScoreDataHQLGenerator
```
This cleans any current builds, compiles & packages (skipping tests), & runs ScoreDataHQLGenerator -- which outputs the HQL that should be run in Hive. You can > the output directly into a h2omodelgroup.hql file & then run `source h2omodelgroup.hql` in Hive to apply (**check to make sure the paths to the two JARS are correct!**)

Upload the localjars/h2o-genmodel.jar & target/MyModels-1.0-SNAPSHOT.jar somewhere you can access from Hive. You can keep it on the local filesystem or put it on the Hadoop FS - either way will work as long as you keep in mind the paths when running "ADD JAR ..."

### 5. Scoring in Hive
The above command will generate HQL similar to below. 

```
-- model order (alphabetical)
-- Name: ai.h2o.hive.udf.models.deeplearning_741ae095_5cc4_415c_a726_f4e26762f3fa
--   Category: Regression
--   Hive Select: scores[0][0 - 1]
-- Name: ai.h2o.hive.udf.models.drf_99268734_ddf8_43b3_87e4_84f092df5292
--   Category: Regression
--   Hive Select: scores[1][0 - 1]
-- Name: ai.h2o.hive.udf.models.gbm_094fceb2_48a2_4447_931b_2aeed114c08a
--   Category: Regression
--   Hive Select: scores[2][0 - 1]
-- Name: ai.h2o.hive.udf.models.glm_a00273fc_04a0_4c0c_b5a6_22fba753ca1b
--   Category: Regression
--   Hive Select: scores[3][0 - 1]

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

TABLEWITHAPPROPRIATEDATA MUST have **ALL** columns required by the models. If they are not -- the UDF will fail to score!!
