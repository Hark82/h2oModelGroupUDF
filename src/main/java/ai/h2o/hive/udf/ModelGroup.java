package ai.h2o.hive.udf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import hex.genmodel.GenModel;

/**
 * Created by nkkarpov on 3/16/16.
 */
public class ModelGroup extends ArrayList<GenModel> {
    public LinkedHashMap<String, Integer> _groupPredictors;

    public ModelGroup() {
        this._groupPredictors = new LinkedHashMap<>();
    }

    public void reflectAndAddModel(String cl) {
        try {
            this.addModel((GenModel)Class.forName(cl).newInstance());
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
    }

    public void reflectAndAddModels(String[] cl) {
        for (int i = 0; i < cl.length; i++)
            this.reflectAndAddModel(cl[i]);
    }

    public void addModel(GenModel m) {
        String[] predictors = m.getNames();
        for(int i = 0; i < predictors.length; i++) {
            if(this._groupPredictors.get(predictors[i]) == null) {
                // Add this new model's predictors to global predictors if doesn't exist
                this._groupPredictors.put(predictors[i], this._groupPredictors.size());
            }
        }

        this.add(m);
    }

    // for now assume data[] is already doubles, make more generic later
    public ArrayList<double[]> scoreAll(double[] data) {
        ArrayList<double[]> result_set = new ArrayList<>();

        for (int i = 0; i < this.size(); i++) {
            // Fill the appropriate data for each model using columnIndex and score
            GenModel m = this.get(i);
            String[] features = m.getNames();
            double[] model_data = new double[features.length];
            double[] model_response = new double[m.getPredsSize()];
            for(int j = 0; j < features.length; j++) {
                model_data[j] = data[this._groupPredictors.get(features[j])];
            }

            // get & add prediction to result
            double[] prediction = m.score0(model_data, model_response);
            result_set.add(prediction);
        }

        return result_set;
    }
}
