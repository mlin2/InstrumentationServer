package org.sentinel.instrumentationserver.metadata;

import org.sentinel.instrumentationserver.Main;
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
 * This class fetches metadata from the metadata XML url.
 */
public class MetadataFetcher {

    /**
     * Fetch the xml and use every application element for metadata fetching.
     */
    public void fetch() {
        try {
            URL url = new URL(Main.METADATA_XML_URI);
            URLConnection urlConnection = url.openConnection();

            Document document = parseXML(urlConnection.getInputStream());
            NodeList nodelist = document.getElementsByTagName("application");

            MetadataDAO metadataDAO = MetadataDAO.getInstance();

            metadataDAO.connectToDatabase();
            for (int i = 0; i < nodelist.getLength(); i++) {
                metadataDAO.saveMetadataFromXmlElement(nodelist.item(i));
            }
            metadataDAO.disconnectFromDatabase();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create a document from the input stream.
     */
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
