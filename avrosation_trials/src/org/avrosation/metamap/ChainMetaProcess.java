package org.avrosation.metamap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class ChainMetaProcess {

    public static class TokenizerMapper
            extends Mapper<Text, Text, Text, Text>{

        private Text word = new Text();
        private MetaMap metaMap;

        public void map(Text key, Text value, Context context
        ) throws IOException, InterruptedException {
            String[] splitArray = splitValue(value);
            metaMap = new MetaMap();
            Text info = new Text();
            StringBuilder output = new StringBuilder();
            try {
                for(int index=0;2*index<splitArray.length;index++){
					if(splitArray[1 + 2 * index].trim().length() > 0) {
					                    String[] splitTagArray = splitTagValue(splitArray[1 + 2 * index]);
					                    int len = splitTagArray.length;
					                    output.append("\t" + splitArray[2 * index]);
					                   
					                    for (int j = 0; 2*j < len; j++) {
					                        metaMap.processOutput(splitTagArray[2 * j + 1]);
					                        output.append(metaMap.output());
					                    }
					                }
									metaMap.cleanOutput();
									
                }
                info.set(output.toString());
                word.set(key.toString());
                context.write(word, info);
            } catch (Exception e) {
                e.printStackTrace();
            }
            context.write(word, info);
        }

        private String[] splitValue(Text value) {
            String[] splitArray = value.toString().split("\t");
            return splitArray;
        }
    }

    public static class MetaMapReducer
            extends Reducer<Text,Text,Text,Text> {
        private Text result = new Text();

        public void reduce(Text key, Iterable<Text> values,
                           Context context
        ) throws IOException, InterruptedException {
            Text output = new Text();
            for (Text val : values) {
                output.append(val.getBytes(), 0, val.getLength());
            }
            result.set(output);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();

        Job job = Job.getInstance(configuration, "metamap process");
        job.setJarByClass(MetaProcess.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setCombinerClass(MetaMapReducer.class);
        job.setReducerClass(MetaMapReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
    }
}
