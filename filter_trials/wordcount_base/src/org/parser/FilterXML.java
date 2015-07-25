package org.parser;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.Text;

import java.io.*;
import java.util.Arrays;

public class FilterXML {

    private final String[] filterTags;
    private final BytesWritable value;
    private Parser parser;

    public FilterXML(BytesWritable val, String[] tags) throws UnsupportedEncodingException {
        value = val;
        InputStream inputStream = new ByteArrayInputStream(trim(value.getBytes()));
        if(inputStream == null)
            throw new IllegalArgumentException("Should parse a non null element");
        parser = new Parser(inputStream);
        filterTags = tags;
    }

    public Text filter() throws IOException {
        StringBuilder parsed_string = new StringBuilder();
        for (String tag: filterTags) {
            parsed_string.append(tag + "\t");
            parsed_string.append(parser.parseDocument(tag) + "\t");
        }
        Text output = new Text(parsed_string.toString());
        return output;
    }

    static byte[] trim(byte[] bytes){
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0){
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }
}
