package filter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mehdibenchoufi on 24/06/15.
 */
public class Parser {

    private Document document;
    private Element element;
    public Parser (InputStream inputStream){
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(inputStream);
            element = document.getDocumentElement();
        }catch(IOException ioe) {
            ioe.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder parseDocument(String tag){
        NodeList nodelist = element.getElementsByTagName(tag);
        StringBuilder parsed_string = new StringBuilder();
        if(nodelist != null && nodelist.getLength() > 0) {
            for(int i = 0 ; i < nodelist.getLength();i++) {
                Element el = (Element) nodelist.item(i);
                parsed_string.append(" " + el.getFirstChild().getNodeValue().toString() + " ");
            }
        }
        return parsed_string;
    }
}
