package ai.h2o.hive.udf;

/**
 * Created by nkkarpov on 3/16/16.
 */

import java.util.ArrayList;

public class ScoreDataHQLGenerator {

    public static void main (String[] args) {
        ModelGroup _models = new ModelGroup();
        _models.reflectAndAddModels(Models.NAMES);

        System.out.println("-- model order (alphabetical)");
        for(String m: Models.NAMES) System.out.println("-- " + m);
        System.out.println();
        System.out.println("-- add jars");
        System.out.println("ADD JAR localjars/h2o-genmodel.jar;");
        System.out.println("ADD JAR target/ScoreData-1.0-SNAPSHOT.jar;");
        System.out.println();
        System.out.println("-- create fn definition");
        System.out.println("CREATE TEMPORARY FUNCTION fn AS \"ai.h2o.hive.udf.ScoreDataUDF\";");
        System.out.println();
        System.out.println("-- column names reference");
        System.out.println("set hivevar:scoredatacolnames=" + _models.getColNamesString());
        System.out.println();
        System.out.println("-- sample query, returns nested array");
        System.out.println("-- select fn(${scoredatacolnames}) from TABLEWITHAPPROPRIATEDATA");
    }
}
