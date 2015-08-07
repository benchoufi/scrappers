package avro;

import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mehdibenchoufi on 26/07/15.
 */
public class AvroReader {

    private static final String FIELD_FILENAME = "filename";
    private static final String FIELD_CONTENTS = "contents";
    public static void readFromAvro(InputStream is) throws IOException {
        DataFileStream<Object> reader =
                new DataFileStream<Object>(
                        is, new GenericDatumReader<Object>());
        for (Object o : reader) {
            GenericRecord r = (GenericRecord) o;
        }
    }
    public static void main(String... args) throws Exception {
        Configuration config = new Configuration();
        FileSystem hdfs = FileSystem.get(config);
        Path destFile = new Path(args[0]);
        InputStream is = hdfs.open(destFile);
        readFromAvro(is);
    }
}
