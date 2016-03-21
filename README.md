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
Download the abalone dataset

### 2. Download H2O Pojos & Dependency JAR
Download each pojo & put it src/main/java/ai/h2o/hive/udf/models/
Download h2o-genmodel & put it in /localjars

### 3. Run setup.py
Run setup.py to create static Models.java class & add package to the top of each model file

### 4. Compile & Package UDF JAR
...

### 5. Generate HQL & run in Hive
...



2. Download POJOs & place in ./src/main/java/ai/h2o/hive/udf/models
3. Download h2o-genmodel & place into ./localjars
4. Run python ./scripts/setup.py to add package info to POJOs & create static class for reflection in the UDF
5. Compile & Package UDF
6. Generate .hql file, then load & and score data in Hive



##

1. Create Models
Load training data into H2O & train N models
2. Download POJOs & place in ./src/main/java/ai/h2o/hive/udf/models
3. Download h2o-genmodel & place into ./localjars
4. Run python ./scripts/setup.py to add package info to POJOs & create static class for reflection in the UDF
5. Compile & Package UDF
6. Generate .hql file, then load & and score data in Hive