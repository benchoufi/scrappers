package org.avrosation.filter;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.avro.mapreduce.AvroJob;
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
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.avrosation.AvroWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by mehdibenchoufi on 23/06/15.
 */
public class ParseXML extends Configured implements Tool {

    public static class SequenceFileMapper extends Mapper<AvroKey<GenericRecord>, NullWritable, Text, Text> {

        private String[] tagsArray = {"brief_title","official_title","condition"};
        private ProjectConf project_conf = new ProjectConf(tagsArray);

        public void map(AvroKey<GenericRecord> key, NullWritable value,
                        Context context) throws IOException, InterruptedException {
            Text outkey = new Text(key.datum().get(
                    AvroWriter.FIELD_FILENAME).toString());
            ByteBuffer byte_value = (ByteBuffer) key.datum().get(AvroWriter.FIELD_CONTENTS);
            BytesWritable outvalue = new BytesWritable(byte_value.array());
            FilterXML filterXML = new FilterXML(outvalue, project_conf.getToparse_tags());
            Text filter_outvalue = filterXML.filter();
            context.write(outkey, filter_outvalue);
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
        Configuration conf = new Configuration();
        //param to fine tune conf.set("mapred.max.split.size","10000");
        Job job = Job.getInstance(conf, "filter avro file");
        job.setJarByClass(ParseXML.class);
        job.setMapperClass(SequenceFileMapper.class);
        job.setCombinerClass(SequenceFileReducer.class);
        job.setReducerClass(SequenceFileReducer.class);

        AvroJob.setInputKeySchema(job, AvroWriter.SCHEMA);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setInputFormatClass(AvroKeyInputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
