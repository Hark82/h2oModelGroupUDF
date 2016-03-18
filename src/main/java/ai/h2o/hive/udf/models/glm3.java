package ai.h2o.hive.udf.models;
/*
  Licensed under the Apache License, Version 2.0
    http://www.apache.org/licenses/LICENSE-2.0.html

  AUTOGENERATED BY H2O at 2016-03-16T13:30:21.750-07:00
  3.8.1.3
  
  Standalone prediction code with sample test data for GLMModel named glm3

  How to download, compile and execute:
      mkdir tmpdir
      cd tmpdir
      curl http://127.0.0.1:54321/3/h2o-genmodel.jar > h2o-genmodel.jar
      curl http://127.0.0.1:54321/3/Models.java/glm3 > glm3.java
      javac -cp h2o-genmodel.jar -J-Xmx2g -J-XX:MaxPermSize=128m glm3.java

     (Note:  Try java argument -XX:+PrintCompilation to show runtime JIT compiler behavior.)
*/
import java.util.Map;
import hex.genmodel.GenModel;
import hex.genmodel.annotations.ModelPojo;

@ModelPojo(name="glm3", algorithm="glm")
public class glm3 extends GenModel {
  public hex.ModelCategory getModelCategory() { return hex.ModelCategory.Regression; }

  public boolean isSupervised() { return true; }
  public int nfeatures() { return 3; }
  public int nclasses() { return 1; }

  // Names of columns used by model.
  public static final String[] NAMES = NamesHolder_glm3.VALUES;

  // Column domains. The last array contains domain of response column.
  public static final String[][] DOMAINS = new String[][] {
    /* C3 */ null,
    /* C4 */ null,
    /* C5 */ null,
    /* C9 */ null
  };
  // Prior class distribution
  public static final double[] PRIOR_CLASS_DISTRIB = null;
  // Class distribution used for model building
  public static final double[] MODEL_CLASS_DISTRIB = null;

  public glm3() { super(NAMES,DOMAINS); }
  public String getUUID() { return Long.toString(4010489787200248050L); }

  // Pass in data in a double[], pre-aligned to the Model's requirements.
  // Jam predictions into the preds[] array; preds[0] is reserved for the
  // main prediction (class for classifiers or value for regression),
  // and remaining columns hold a probability distribution for classifiers.
  public final double[] score0( double[] data, double[] preds ) {
    final double [] b = BETA.VALUES;
    for(int i = 0; i < 0; ++i) if(Double.isNaN(data[i])) data[i] = CAT_MODES.VALUES[i];
    for(int i = 0; i < 3; ++i) if(Double.isNaN(data[i + 0])) data[i+0] = NUM_MEANS.VALUES[i];
    double eta = 0.0;
    for(int i = 0; i < CATOFFS.length-1; ++i) {
      int ival = (int)data[i];
      if(ival != data[i]) throw new IllegalArgumentException("categorical value out of range");
      ival += CATOFFS[i];
      if(ival < CATOFFS[i + 1])
        eta += b[ival];
    }
    for(int i = 0; i < b.length-1-0; ++i)
      eta += b[0+i]*data[i];
    eta += b[b.length-1]; // reduce intercept
    double mu = hex.genmodel.GenModel.GLM_identityInv(eta);
    preds[0] = mu;
    return preds;
  }
    static class BETA implements java.io.Serializable {
      public static final double[] VALUES = new double[4];
      static {
        BETA_0.fill(VALUES);
      }
      static final class BETA_0 implements java.io.Serializable {
        static final void fill(double[] sa) {
          sa[0] = 12.0309559998607;
          sa[1] = 19.98057929746537;
          sa[2] = -0.09244846463243933;
          sa[3] = 2.3154804967230262;
        }
      }
}
// Imputed numeric values
    static class NUM_MEANS implements java.io.Serializable {
      public static final double[] VALUES = new double[3];
      static {
        NUM_MEANS_0.fill(VALUES);
      }
      static final class NUM_MEANS_0 implements java.io.Serializable {
        static final void fill(double[] sa) {
          sa[0] = 0.40788125448886786;
          sa[1] = 0.13951639932966242;
          sa[2] = 0.8287421594445772;
        }
      }
}
// Imputed categorical values.
    static class CAT_MODES implements java.io.Serializable {
      public static final int[] VALUES = new int[0];
      static {
      }
}
    // Categorical Offsets
    public static final int[] CATOFFS = {0};
}
// The class representing training column names
class NamesHolder_glm3 implements java.io.Serializable {
  public static final String[] VALUES = new String[3];
  static {
    NamesHolder_glm3_0.fill(VALUES);
  }
  static final class NamesHolder_glm3_0 implements java.io.Serializable {
    static final void fill(String[] sa) {
      sa[0] = "C3";
      sa[1] = "C4";
      sa[2] = "C5";
    }
  }
}

