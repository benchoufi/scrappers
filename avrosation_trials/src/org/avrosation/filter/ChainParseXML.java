package org.avrosation.filter;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.mapred.AvroKey;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.avrosation.AvroWriter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by mehdibenchoufi on 23/06/15.
 */
public class ChainParseXML extends Configured {

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
}
