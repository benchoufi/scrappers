package org.avrosation;

import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.zookeeper.common.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Created by mehdibenchoufi on 26/07/15.
 */
public class AvroWriter {

    public static final String FIELD_FILENAME = "filename";
    public static final String FIELD_CONTENTS = "contents";
    public static final String SCHEMA_JSON =
            "{\"type\": \"record\", \"name\": \"FilterMetamap\", "
                    + "\"fields\": ["
                    + "{\"name\":\""
                    + FIELD_FILENAME
                    + "\", \"type\":\"string\"},"
                    + "{\"name\":\""
                    + FIELD_CONTENTS
                    + "\", \"type\":\"bytes\"}]}";
    public static final Schema SCHEMA = Schema.parse(SCHEMA_JSON);

    public static void writeToAvro(File srcPath,OutputStream outputStream) throws IOException {
        DataFileWriter<Object> writer =
                new DataFileWriter<Object>(
                        new GenericDatumWriter<Object>())
                        .setSyncInterval(100);
        writer.setCodec(CodecFactory.snappyCodec());
        writer.create(SCHEMA, outputStream);
        for (Object obj :
                FileUtils.listFiles(srcPath, null, false)) {
            File file = (File) obj;
            String filename = file.getAbsolutePath();
            byte content[] = FileUtils.readFileToByteArray(file);
            GenericRecord record = new GenericData.Record(SCHEMA);
            record.put(FIELD_FILENAME, filename);
            record.put(FIELD_CONTENTS, ByteBuffer.wrap(content));
            writer.append(record);
        }
        IOUtils.cleanup(null, writer);
        IOUtils.cleanup(null, outputStream);
    }

    public static void main(String... args) throws Exception {
        Configuration conf = new Configuration();
        FileSystem hdfs = FileSystem.get(conf);
        File sourceDir = new File(args[0]);
        Path destFile = new Path(args[1]);
        OutputStream os = hdfs.create(destFile);
        writeToAvro(sourceDir, os);
    }
}