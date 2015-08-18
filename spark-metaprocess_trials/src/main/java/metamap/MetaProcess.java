package metamap;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class MetaProcess implements Serializable {

    public JavaPairRDD<String, String> mappingToPair(JavaRDD<String> input){
        return input.mapToPair( new PairFunction<String, String, String>() {
            public Tuple2<String, String> call(String line) {
                String[] arrayList = line.split("\t",2);
                return new Tuple2(arrayList[0], arrayList[1]); }
        });
    }

    public JavaPairRDD<String,String> mappingValues(JavaPairRDD<String,String> rdd, final String option){
        return rdd.mapValues(
                new Function<String, String>() {

                    private String[] splitValue(String value) {
                        String[] splitArray = value.split("\t");
                        return splitArray;
                    }

                    public String call(String value) {
                        MetaMap metaMap = new MetaMap(option);

                        String[] splitArray = splitValue(value);
                        StringBuilder output = new StringBuilder();
                        try {
                            for (int index = 1; 2 * index < splitArray.length; index++) {
                                metaMap.processOutput(splitArray[2 * index]);
                                output.append(metaMap.output());
                            }
                            System.out.print(output.toString() + "\n");
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
            Config.getInstance().addOptions(args[2]);
        }
        String option = Config.getInstance().getOptions();

        MetaProcess metaProcess = new MetaProcess();

        SparkConf conf = new SparkConf().setAppName("spark metamap process");
        conf.setMaster("local[2]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> input = sc.textFile(args[0]);
        JavaPairRDD<String, String> int_put = metaProcess.mappingToPair(input);
        JavaPairRDD<String, String> output = metaProcess.mappingValues(int_put, option);

        output.saveAsTextFile(args[1]);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
    }
}
