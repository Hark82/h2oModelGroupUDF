package ai.h2o.hive.udf;

public class QueryGenerator {
    public static void main (String [] args) {
        GBM_C1 p = new GBM_C1();
        String[] cols = p.getNames();

        // can clean up w/ StringUtils (apache common)
        StringBuffer query = new StringBuffer();
        for(int i=0;i<cols.length;++i){
            query.append(cols[i]);
            if (i != cols.length - 1) query.append(",");
        }

        System.out.println("SELECT fn("+query+") FROM tableName");
    }
}