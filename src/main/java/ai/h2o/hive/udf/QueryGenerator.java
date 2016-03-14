package ai.h2o.hive.udf;

/*
    Prints out query with correct column names based on what POJO expects
 */

import hex.genmodel.GenModel;

public class QueryGenerator {

    public static void main (String [] args) {
        GBM_C1 p = new GBM_C1();
        String[] cols = p.getNames();
        GenModel [] _models;

        // can clean up w/ StringUtils (apache common)
        StringBuffer query = new StringBuffer();
        for(int i=0;i<cols.length;++i){
            query.append(cols[i]);
            if (i != cols.length - 1) query.append(",");
        }

        String name = "ai.h2o.hive.udf.GBM_C";
        _models = new GenModel[10];
        for (int i = 1;i <= 10; ++i) {
            try {
                _models[i - 1] = (GenModel) Class.forName(name + i).newInstance();
            } catch (Throwable t) {
                t.printStackTrace();
                throw new RuntimeException(t);
            }
        }

        // notes
        // GenModel.getNames() returns all column names that were part of the training set

        GenModel tmp = _models[0];

        // Print out first 20 columns in the training set
        String[] all_cols = tmp.getNames();
        String[][] domains = tmp.getDomainValues();
        for (int i = 0; i < 20; i++) {
            if ( domains [i] != null )
                System.out.println(all_cols[i]);
            else
                System.out.println(all_cols[i] + " /* NULL */");
        }


        //System.out.println("SELECT fn("+query+") FROM tableName");
    }
}