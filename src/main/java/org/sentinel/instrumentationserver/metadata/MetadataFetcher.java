package org.sentinel.instrumentationserver.metadata;

import org.sentinel.instrumentationserver.InstrumentationDAO;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by sebastian on 1/26/16.
 */
public class MetadataFetcher {
    public void fetch() {
        try {
            URL url = new URL("https://f-droid.org/repo/index.xml");
            URLConnection urlConnection = url.openConnection();

            Document document = parseXML(urlConnection.getInputStream());
            NodeList nodelist = document.getElementsByTagName("application");

            InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();

            for(int i = 0; i < nodelist.getLength(); i++) {
                //System.out.println(nodelist.item(i).getTextContent());
                instrumentationDAO.saveMetadataFromXml(nodelist.item(i));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Document parseXML(InputStream inputStream) {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputStream);

            return document;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
