package ai.h2o.hive.udf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

import hex.genmodel.GenModel;

/**
 * Created by nkkarpov on 3/16/16.
 */
public class ModelGroup extends ArrayList<GenModel> {

    class Predictor {
        public int index;
        public String[] domains;
        public Predictor(int index, String[] domains) {
            this.index = index;
            this.domains = domains;
        }
        public String toString() {
            if (this.domains != null)
                return Integer.toString(this.index) + " " + Arrays.asList(this.domains);
            else
                return Integer.toString(this.index) + " numerical";
        }
    }

    public LinkedHashMap<String, Predictor> _groupPredictors;  // needs to guarantee order so, linked
    public ArrayList<String> _groupIdxToColNames;

    public ModelGroup() {
        this._groupPredictors = new LinkedHashMap<String, Predictor>();
        this._groupIdxToColNames = new ArrayList<String>();
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
                this._groupPredictors.put(predictors[i], new Predictor(this._groupPredictors.size(), m.getDomainValues(i)));
                this._groupIdxToColNames.add(predictors[i]);
            }
        }

        this.add(m);
    }

    public int mapEnum(int colIdx, String enumValue) {
        String[] domain = this._groupPredictors.get(this._groupIdxToColNames.get(colIdx)).domains;
        if (domain==null || domain.length==0) return -1;
        for (int i=0; i<domain.length;i++) if (enumValue.equals(domain[i])) return i;
        return -1;
    }

    // for now assume data[] is already doubles, make more generic later
    public ArrayList<ArrayList<Double>> scoreAll(double[] data) {
        ArrayList<ArrayList<Double>> result_set = new ArrayList<ArrayList<Double>>();

        for (int i = 0; i < this.size(); i++) {
            // Fill the appropriate data for each model using columnIndex and score
            GenModel m = this.get(i);
            String[] features = m.getNames();
            double[] model_data = new double[features.length];
            double[] model_response = new double[m.getPredsSize()];
            for(int j = 0; j < features.length; j++) {
                model_data[j] = data[this._groupPredictors.get(features[j]).index];
            }

            // get & add prediction to result
            double[] prediction = m.score0(model_data, model_response);
            ArrayList<Double> p = new ArrayList<Double>();
            for(double d: prediction) p.add(d);

            result_set.add(p);
        }

        return result_set;
    }

    public String getColNamesString () {
        return StringUtils.join(this._groupIdxToColNames, ",");
    }
}
