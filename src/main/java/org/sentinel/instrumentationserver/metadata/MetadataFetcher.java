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
            NodeList nodelist = null;
            if (document != null) {
                nodelist = document.getElementsByTagName("application");
            }

            MetadataDAO metadataDAO = new MetadataDAO();

            metadataDAO.connectToDatabase();
            if (nodelist != null) {
                for (int i = 0; i < nodelist.getLength(); i++) {
                    metadataDAO.saveMetadataFromXmlElement(nodelist.item(i));
                }
            }
            metadataDAO.disconnectFromDatabase();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create a document from the input stream.
     */
    private Document parseXML(InputStream inputStream) {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();

            return documentBuilder.parse(inputStream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
