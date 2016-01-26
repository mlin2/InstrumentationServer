package org.sentinel.instrumentationserver.fetch;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.sentinel.instrumentationserver.InstrumentationDAO;
import org.sentinel.instrumentationserver.InstrumentationRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sebastian on 1/26/16.
 */
public class FetcherRunner implements Runnable {
    private List<String> repositoryApkLinks;


    public void setRepositoryApkLinks(List<String> repositoryApkLinks) {
        this.repositoryApkLinks = repositoryApkLinks;
    }

    @Override
    public void run() {
        Iterator<String> repositoryApkLinkIterator = repositoryApkLinks.iterator();
        while (repositoryApkLinkIterator.hasNext()) {
            InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
            String apkLink = repositoryApkLinkIterator.next();
            URL url = null;
            try {
                url = new URL(apkLink);
                URLConnection urlConnection = url.openConnection();
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
                byte[] apkBytes = IOUtils.toByteArray(urlConnection.getInputStream());
                String sha512Hash = String.valueOf(Hex.encodeHex(messageDigest.digest(apkBytes)));

                if (!instrumentationDAO.checkIfApkAlreadyInstrumented(sha512Hash)) {
                    InstrumentationRunner instrumentationRunner = new InstrumentationRunner(new FileInputStream("InstrumentationPEP/files/catSources_Short.txt"),
                            new FileInputStream("InstrumentationPEP/files/catSinks_Short.txt"), new FileInputStream("InstrumentationPEP/files/EasyTaintWrapperSource.txt"),
                            apkBytes, sha512Hash, true);
                    Thread thread = new Thread(instrumentationRunner);
                    thread.start();
                    thread.join();
                }

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}