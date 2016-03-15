package ai.h2o.hive.udf;

/*
    Prints out query with correct column names based on what POJO expects
 */

import hex.genmodel.GenModel;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class QueryGenerator {

    public static void main (String [] args) {
        GenModel [] _models;
        LinkedHashMap<String,Integer> columnMapping = new LinkedHashMap<>(); // we need this to maintain order

        // Instantiate all models
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

        // Build column map
        int columnMappingIndex = 0;
        for(int i = 0; i < _models.length; i++) {
            GenModel m = _models[i];
            String[] mcols = m.getNames();
            for (int j = 0; j < mcols.length; j++) {
                Integer index = columnMapping.get(mcols[j]);
                if (index == null) {
                    columnMapping.put(mcols[j], columnMappingIndex);
                    columnMappingIndex++;
                }
            }
        }

        // Build query
        Iterator it = columnMapping.entrySet().iterator();
        StringBuffer query = new StringBuffer();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String c = (String) entry.getKey();
            query.append(c);
            if (it.hasNext()) query.append(",");
        }

        //System.out.println(columnMappingIndex);
        System.out.println(query);
        System.out.println(columnMapping.size());

        // notes
        // GenModel.getNames() returns all column names that were part of the training set

        /*
            Get & instantiate all models in folder
            Build set of variables that
        */

        /*
        GenModel tmp = _models[0];

        GenModel m = tmp;
        HashMap<String, Integer> modelColumnNameToIndexMap;
        HashMap<Integer, HashMap<String, Integer>> domainMap;

        // Create map of column names to index number.
        System.out.println("Col names to index");
        modelColumnNameToIndexMap = new HashMap<>();
        String[] modelColumnNames = m.getNames();
        for (int i = 0; i < modelColumnNames.length; i++) {
            modelColumnNameToIndexMap.put(modelColumnNames[i], i);
            System.out.println(modelColumnNames[i] + " , " + i);
        }

        // Create map of input variable domain information.
        // This contains the categorical string to numeric mapping.
        System.out.println("Domain Mapping");
        domainMap = new HashMap<>();
        for (int i = 0; i < m.getNumCols(); i++) {
            String[] domainValues = m.getDomainValues(i);
            if (domainValues != null) {
                HashMap<String, Integer> m2 = new HashMap<>();
                for (int j = 0; j < domainValues.length; j++) {
                    m2.put(domainValues[j], j);
                }
                System.out.println(i + " , " + m2);
                domainMap.put(i, m2);
            }
        } */



        // BELOW IS COMPLETELY WRONG
        /*
        // Print out first 20 columns in the training set
        String[] all_cols = tmp.getNames();
        String[][] domains = tmp.getDomainValues();
        for (int i = 0; i < 20; i++) {
            if ( domains [i] != null ) // we are used in the model
                System.out.println(all_cols[i]);
            else // we were in the training frame for the model build but were not used
                System.out.println(all_cols[i] + " /* NULL ");
        } */


        //System.out.println("SELECT fn("+query+") FROM tableName");
    }
}