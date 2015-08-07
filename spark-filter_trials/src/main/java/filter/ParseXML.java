package filter;

import org.apache.hadoop.io.BytesWritable;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.DataFrame;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;


import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by mehdibenchoufi on 23/06/15.
 */
public class ParseXML implements Serializable{

    private static String[] tagsArray = {"brief_title","official_title","condition"};
    private static ProjectConf project_conf = new ProjectConf(tagsArray);

    private Function<Row, String> SparkMap() throws IOException {
        return new Function<Row, String>() {
            public String call(Row row) throws IOException {
                String output = FilterAvro(row);
                return output;
            }
        };
    }

    private static String FilterAvro(Row row) throws IOException {
        ByteBuffer byte_value = ByteBuffer.wrap((byte[]) row.get(1));
        BytesWritable outvalue = new BytesWritable(byte_value.array());
        FilterXML filterXML = new FilterXML(outvalue, project_conf.getToparse_tags());
        String filter_outvalue = filterXML.filter();
        return  row.get(0) + "\t" + filter_outvalue;
    }

    public static void main(String[] args) throws Exception {
        ParseXML parseXML = new ParseXML();

        SparkConf conf = new SparkConf().setAppName("wordCount");
        conf.setMaster("local[2]");
        SparkContext sc = new SparkContext(conf);

        SQLContext sqlContext = new SQLContext(sc);
        DataFrame df = sqlContext.load(args[0], "com.databricks.spark.avro");

        JavaRDD<String> output = df.javaRDD().map(parseXML.SparkMap());
        output.saveAsTextFile(args[1]);
    }
}
