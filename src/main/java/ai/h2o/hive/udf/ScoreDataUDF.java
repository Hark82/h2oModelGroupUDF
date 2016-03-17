package ai.h2o.hive.udf;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import hex.genmodel.GenModel;
import org.apache.commons.logging.Log;
import org.apache.hadoop.hive.ql.exec.MapredContext;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.exec.UDFArgumentLengthException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.mapred.JobConf;
import org.apache.log4j.Logger;

@UDFType(deterministic = true, stateful = false)
@Description(name="scoredata", value="_FUNC_(*) - Returns a score for the given row",
        extended="Example:\n"+"> SELECT scoredata(*) FROM target_data;")

class ScoreDataUDF extends GenericUDF {
  private PrimitiveObjectInspector[] inFieldOI;

  GenModel [] _models;
  private final int NUMMODEL = 96;

  ModelGroup _mg;

  public void log (String s) {
    System.out.println("ScoreDataUDF: " + s);
  }

  @Override
  public String getDisplayString(String[] args) {
    return "scoredata("+Arrays.asList(_models[0].getNames())+").";
  }

  @Override
  public void configure(MapredContext context) {
    super.configure(context);
    context.toString();
    JobConf jc = context.getJobConf();
  }

  @Override
  public ObjectInspector initialize(ObjectInspector[] args) throws UDFArgumentException {

    long start = System.currentTimeMillis();
    log("Begin: initialize()");

    _mg = new ModelGroup();

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

    _mg.reflectAndAddModels(model_names);

    // Basic argument count check
    // Expects one less argument than model used; results column is dropped
    if (args.length != _mg._groupPredictors.size()) {
      throw new UDFArgumentLengthException("Incorrect number of arguments." +
              "  scoredata() requires: "+ Arrays.asList(_mg._groupPredictors.keySet())
              +", in the listed order. Received "+args.length+" arguments.");
    }

    //Check input types
    inFieldOI = new PrimitiveObjectInspector[args.length];
    PrimitiveObjectInspector.PrimitiveCategory pCat;
    for (int i = 0; i < args.length; i++) {
      if (args[i].getCategory() != ObjectInspector.Category.PRIMITIVE)
        throw new UDFArgumentException("scoredata(...): Only takes primitive field types as parameters");
      pCat = ((PrimitiveObjectInspector) args[i]).getPrimitiveCategory();
      if (pCat != PrimitiveObjectInspector.PrimitiveCategory.STRING
              && pCat != PrimitiveObjectInspector.PrimitiveCategory.DOUBLE
              && pCat != PrimitiveObjectInspector.PrimitiveCategory.FLOAT
              && pCat != PrimitiveObjectInspector.PrimitiveCategory.LONG
              && pCat != PrimitiveObjectInspector.PrimitiveCategory.INT
              && pCat != PrimitiveObjectInspector.PrimitiveCategory.SHORT)
        throw new UDFArgumentException("scoredata(...): Cannot accept type: " + pCat.toString());
      inFieldOI[i] = (PrimitiveObjectInspector) args[i];
  }

    long end = System.currentTimeMillis() - start;
    log("End: initialize(), took: " + Long.toString(end));

    return ObjectInspectorFactory.getStandardListObjectInspector(ObjectInspectorFactory.getStandardListObjectInspector(PrimitiveObjectInspectorFactory.javaDoubleObjectInspector));
  }

  @Override
  public Object evaluate(DeferredObject[] record) throws HiveException {
    long start = System.currentTimeMillis();
    log("Begin: evaluate()");

    if (record != null) {
      if (record.length == _mg._groupPredictors.size()) {
        double[] data = new double[record.length];

        //Sadly, HIVE UDF doesn't currently make the field name available.
        //Thus this UDF must depend solely on the arguments maintaining the same
        // field order seen by the original H2O model creation.
        for (int i = 0; i < record.length; i++) {
          try {
            Object o = inFieldOI[i].getPrimitiveJavaObject(record[i].get());
            if (o instanceof java.lang.String) {
              log("o is String");
              log(o.toString());
              // Hive wraps strings in double quotes, remove
              // Hack for now on this specific data set
              data[i] = _mg.mapEnum(i, ((String) o).replace("\"", ""));
              if (data[i] == -1)
                throw new UDFArgumentException("scoredata(...): The value " + (String) o
                    + " is not a known category for column " + _mg._groupIdxToColNames.get(i));
            } else if (o instanceof Double) {
              data[i] = ((Double) o).doubleValue();
            } else if (o instanceof Float) {
              data[i] = ((Float) o).doubleValue();
            } else if (o instanceof Long) {
              data[i] = ((Long) o).doubleValue();
            } else if (o instanceof Integer) {
              data[i] = ((Integer) o).doubleValue();
            } else if (o instanceof Short) {
              data[i] = ((Short) o).doubleValue();
            } else if (o == null) {
              return null;
            } else {
              throw new UDFArgumentException("scoredata(...): Cannot accept type: "
                  + o.getClass().toString() + " for argument # " + i + ".");
            }
          } catch (Throwable e) {
            throw new UDFArgumentException("Unexpected exception on argument # " + i + ". " + e.toString());
          }

        }

        try {
          ArrayList<ArrayList<Double>> result_set = _mg.scoreAll(data);

          long end = System.currentTimeMillis() - start;
          log("End: evaluate(), took: " + Long.toString(end));

          return result_set;
        } catch (Throwable e) {
          throw new UDFArgumentException("H2O predict function threw exception: " + e.toString());
        }
      } else {
        throw new UDFArgumentException("Incorrect number of arguments." +
            "  scoredata() requires: " + Arrays.asList(_mg._groupPredictors.size()) + ", in order. Received "
            +record.length+" arguments.");
      }
    } else { // record == null
      return null; //throw new UDFArgumentException("scoredata() received a NULL row.");
    }
  }
}
