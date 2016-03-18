package ai.h2o.hive.udf;

/**
 * Created by nkkarpov on 3/16/16.
 */

import java.util.ArrayList;

public class GroupScorer {

    public static void main (String[] args) {
        ModelGroup _models = new ModelGroup();

        _models.reflectAndAddModels(Models.NAMES);

        double[] data = { 2, 0.4550, 0.3650, 0.0950, 0.5140, 0.2245, 0.1010, 0.1500, 15.0 };

        ArrayList<ArrayList<Double>> scores = _models.scoreAll(data);

        for(ArrayList<Double> prediction: scores) {
            for(int i = 0; i < prediction.size(); i++) System.out.print(prediction.get(i) + " ");
            System.out.println();
        }
    }
}
