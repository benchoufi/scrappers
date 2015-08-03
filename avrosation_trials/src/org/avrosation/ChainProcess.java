package org.avrosation;

import org.apache.avro.mapred.AvroKey;
import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.avrosation.filter.ChainParseXML;
import org.avrosation.filter.ParseXML;
import org.avrosation.metamap.ChainMetaProcess;

/**
 * Created by mehdibenchoufi on 29/07/15.
 */
public class ChainProcess {

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        Configuration filter_conf = new Configuration();
        Job job = Job.getInstance(filter_conf, "filter avro file");
        job.setJarByClass(ParseXML.class);

        Path filterInput = new Path(args[0]);
        Path outputDirIntermediate = new Path(args[1]);

        ChainMapper.addMapper(
                job, ChainParseXML.SequenceFileMapper.class,
                AvroKey.class, NullWritable.class, Text.class, Text.class,
                filter_conf);

        ChainMapper.addMapper(
                job, ChainMetaProcess.TokenizerMapper.class,
                Text.class, Text.class, Text.class, Text.class,
                filter_conf);

        ChainReducer.setReducer(job, ChainMetaProcess.MetaMapReducer.class,
                Text.class, Text.class, Text.class, Text.class, filter_conf);


        job.setCombinerClass(ChainMetaProcess.MetaMapReducer.class);

        AvroJob.setInputKeySchema(job, AvroWriter.SCHEMA);
        job.setInputFormatClass(AvroKeyInputFormat.class);

        FileInputFormat.addInputPath(job, filterInput);
        FileOutputFormat.setOutputPath(job, outputDirIntermediate);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        int code = job.waitForCompletion(true) ? 0 : 1;

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println(elapsedTime);
        System.exit(code);
    }
}
