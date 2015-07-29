package org.parser;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;

import java.io.IOException;

/**
 * Created by mehdibenchoufi on 23/06/15.
 */
public class ParseXML extends Configured implements Tool {


    public static class SequenceFileMapper extends Mapper<NullWritable, BytesWritable, Text, Text> {

        private String fileName = new String();
        private String[] tagsArray = {"brief_title","official_title","condition"};
        private ProjectConf project_conf = new ProjectConf(tagsArray);

        protected void setup(Context context) throws java.io.IOException, java.lang.InterruptedException
        {
            fileName = ((FileSplit) context.getInputSplit()).getPath().toString();
        }

        public void map(NullWritable key, BytesWritable value,
                        Context context) throws IOException, InterruptedException {
            FilterXML filterXML = new FilterXML(value, project_conf.getToparse_tags());
            Text mapOutput = filterXML.filter();
            context.write(new Text(fileName), mapOutput);
        }
    }


    public static class SequenceFileReducer extends Reducer<Text,Text,Text,Text> {
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

    @Override
    public int run(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        return 0;
    }

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        Configuration conf = new Configuration();
        //param to fine tune conf.set("mapred.max.split.size","10000");

        Job job = Job.getInstance(conf, "no word count");
        job.setJarByClass(ParseXML.class);
        job.setMapperClass(SequenceFileMapper.class);
        job.setCombinerClass(SequenceFileReducer.class);
        job.setReducerClass(SequenceFileReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(XMLFileInputFormat.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
