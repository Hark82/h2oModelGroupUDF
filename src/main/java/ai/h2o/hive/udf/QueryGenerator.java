package ai.h2o.hive.udf;

public class QueryGenerator {
    public static void main (String [] args) {
        GBMModel p = new GBMModel();
        String[] cols = p.getNames();

        // can clean up w/ StringUtils (apache common)
        StringBuffer query = new StringBuffer();
        for(int i=0;i<cols.length;++i){
            query.append(cols[i] + " ");
        }

        System.out.println("SELECT fn("+query+") FROM tableName");
    }
}