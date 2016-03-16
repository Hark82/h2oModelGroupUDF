package ai.h2o.hive.udf;

/**
 * Created by nkkarpov on 3/16/16.
 */

import java.util.ArrayList;

public class GroupScorer {

    public static void main (String[] args) {
        ModelGroup _models = new ModelGroup();

        String[] model_names = {"ai.h2o.hive.udf.models.glm1",
                                "ai.h2o.hive.udf.models.glm2",
                                "ai.h2o.hive.udf.models.glm3",
                                "ai.h2o.hive.udf.models.glm4",
                                "ai.h2o.hive.udf.models.glm5",
                                "ai.h2o.hive.udf.models.glm6",
                                "ai.h2o.hive.udf.models.gbm1",
                                "ai.h2o.hive.udf.models.gbm2",
                                "ai.h2o.hive.udf.models.gbm3",
                                "ai.h2o.hive.udf.models.gbm4",
                                "ai.h2o.hive.udf.models.gbm5",
                                "ai.h2o.hive.udf.models.gbm6"};

        _models.reflectAndAddModels(model_names);

        double[] data = { 2, 0.4550, 0.3650, 0.0950, 0.5140, 0.2245, 0.1010, 0.1500, 15.0 };

        ArrayList<double[]> scores = _models.scoreAll(data);

        for(double[] prediction: scores) {
            for(int i = 0; i < prediction.length; i++) System.out.print(prediction[i] + " ");
            System.out.println();
        }
    }
}
