package org.avrosation;

import org.apache.avro.mapreduce.AvroJob;
import org.apache.avro.mapreduce.AvroKeyInputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.avrosation.filter.ParseXML;
import org.avrosation.metamap.MetaProcess;

/**
 * Created by mehdibenchoufi on 29/07/15.
 */
public class Process {

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        Configuration filter_conf = new Configuration();
        Job filter_job = Job.getInstance(filter_conf, "filter avro file");
        filter_job.setJarByClass(ParseXML.class);

        Path filterInput = new Path(args[0]);
        Path outputDirIntermediate = new Path(args[1]);

        filter_job.setMapperClass(ParseXML.SequenceFileMapper.class);
        filter_job.setCombinerClass(ParseXML.SequenceFileReducer.class);
        filter_job.setReducerClass(ParseXML.SequenceFileReducer.class);

        AvroJob.setInputKeySchema(filter_job, AvroWriter.SCHEMA);
        filter_job.setOutputKeyClass(Text.class);
        filter_job.setOutputValueClass(Text.class);
        filter_job.setInputFormatClass(AvroKeyInputFormat.class);

        FileInputFormat.addInputPath(filter_job, filterInput);
        FileOutputFormat.setOutputPath(filter_job, outputDirIntermediate);

        int code = filter_job.waitForCompletion(true) ? 0 : 1;

        if(code==0){
            Configuration metamap_conf = new Configuration();
            Job metamap_job = Job.getInstance(metamap_conf, "run  metamap");

            metamap_job.setJarByClass(MetaProcess.class);
            metamap_job.setMapperClass(MetaProcess.TokenizerMapper.class);
            metamap_job.setCombinerClass(MetaProcess.MetaMapReducer.class);
            metamap_job.setReducerClass(MetaProcess.MetaMapReducer.class);

            metamap_job.setOutputKeyClass(Text.class);
            metamap_job.setOutputValueClass(Text.class);

            FileInputFormat.addInputPath(metamap_job, outputDirIntermediate);
            FileOutputFormat.setOutputPath(metamap_job, new Path(args[2]));

            code = metamap_job.waitForCompletion(true) ? 0 : 1;
        }
        FileSystem.get(filter_conf).delete(outputDirIntermediate, true);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.exit(code);
        System.out.println(elapsedTime);
    }
}
