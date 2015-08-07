package filter;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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

    public String filter() throws IOException {
        StringBuilder parsed_string = new StringBuilder();
        for (String tag: filterTags) {
            parsed_string.append(tag + "\t");
            parsed_string.append(parser.parseDocument(tag) + "\t");
        }
        return parsed_string.toString();
    }

    static byte[] trim(byte[] bytes){
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0){
            --i;
        }
        return Arrays.copyOf(bytes, i + 1);
    }
}
