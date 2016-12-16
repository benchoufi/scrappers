package metamap;


import org.apache.hadoop.io.Text;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;

public class MetaProcess implements Serializable {

    public JavaPairRDD<String, String> mappingToPair(JavaRDD<String> input) {
        return input.mapToPair(new PairFunction<String, String, String>() {
            public Tuple2<String, String> call(String line) {
                String[] arrayList = line.split("\t", 2);
                return new Tuple2(arrayList[0], arrayList[1]);
            }
        });
    }

    public JavaPairRDD<String, String> mappingValues(JavaPairRDD<String, String> rdd, final String option) {
        return rdd.mapValues(
                new Function<String, String>() {

                    private String[] splitValue(String value) {
                        String[] splitArray = value.split("\t");
                        return splitArray;
                    }

                    private String[] splitTabValue(Text value) {
                        String[] splitArray = value.toString().split("\t");
                        return splitArray;
                    }

                    private String[] splitTagValue(String value) {
                        String[] splitArray = value.split("<cond>");
                        return splitArray;
                    }

                    public String call(String value) {
                        MetaMap metaMap = new MetaMap(option);

                        String[] splitArray = splitValue(value);
                        StringBuilder output = new StringBuilder();
                        try {
                            for (int index = 0; 2 * index < splitArray.length; index++) {
                                if (splitArray[1 + 2 * index].trim().length() > 0) {
                                    String[] splitTagArray = splitTagValue(splitArray[1 + 2 * index]);
                                    int len = splitTagArray.length;

                                    output.append("\t" + splitArray[2 * index]);

                                    for (int j = 1; j < len; j++) {
                                        metaMap.cleanOutput();
                                        String substr = splitTagArray[j].replace("</cond>", "");
                                        metaMap.processOutput(substr);
                                        output.append(metaMap.output());
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return output.toString();
                    }
                });
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        if (args.length >= 3) {
            //Config.getInstance().addOptions(args[2]);
        }
        String option = Config.getInstance().getOptions();

        MetaProcess metaProcess = new MetaProcess();

        SparkConf conf = new SparkConf().setAppName("spark metamap process");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> input = sc.textFile(args[0]);
        input = input.repartition(18);
        JavaPairRDD<String, String> int_put = metaProcess.mappingToPair(input);
        JavaPairRDD<String, String> output = metaProcess.mappingValues(int_put, option);
        output.saveAsTextFile(args[1]);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("elapsed time " + elapsedTime + "\n");
    }
}
