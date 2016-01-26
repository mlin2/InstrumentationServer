package org.sentinel.instrumentationserver.fetch;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;
import org.sentinel.instrumentationserver.InstrumentationDAO;
import org.sentinel.instrumentationserver.InstrumentationRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by sebastian on 1/26/16.
 */
public class ApkFetcher implements Runnable {
    public void run() {
        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
        List<String> repositoryApkLinks = instrumentationDAO.getAllRepositoryApkLinks();
        Iterator<String> repositoryApkLinkIterator = repositoryApkLinks.iterator();
        while (repositoryApkLinkIterator.hasNext()) {
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
                            new FileInputStream("InstrumentationPEP/files/catSinks_Short.txt"), new FileInputStream("InstrumentationPEP/files/EasyTaintWrapperSource.txt"), apkBytes, sha512Hash, true);
                    Thread thread = new Thread(instrumentationRunner);
                    thread.start();
                    thread.join(300);
                    if(thread.isAlive()) {
                        thread.interrupt();
                    }

/*                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.invokeAll(Arrays.asList(thread), 5, TimeUnit.MINUTES);
                    executorService.shutdown();*/
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
