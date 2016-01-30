package org.sentinel.instrumentationserver.instrumentation;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.Test;
import org.sentinel.instrumentationserver.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import static org.junit.Assert.assertTrue;

/**
 * Test whether the image "protect.png" is in the APK.
 */
public class ShieldImageInApkTest extends InstrumentationTestBase {

    /**
     * Test whether the instrumented APK includes the file "protect.png" that can be found in
     * InstrumentationDependencies/resources. This file is included in the instrumented APK by DroidForce.
     */
    @Test
    public void testApkIncludesShieldImage() {
        MultiPart formDataMultiPart = getFormDataMultiPart();
        String apkHash = getApkHash(formDataMultiPart);
        CRC32 crc32 = new CRC32();
        try {
            crc32.update(IOUtils.toByteArray(new FileInputStream("InstrumentationDependencies/resources/protect.png")));

            while (target.path("instrument/" + apkHash).request().get().getStatus() != 200) {
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            net.lingala.zip4j.core.ZipFile zipFile = new ZipFile(new File(Main.INSTRUMENTATION_JOB_DIRECTORY + "/" + apkHash + "/alignedApk.apk"));
            List<FileHeader> fileHeaders = zipFile.getFileHeaders();
            Iterator<FileHeader> fileHeadersIterator = fileHeaders.iterator();

            boolean shieldImageIncludedInApk = false;

            while (fileHeadersIterator.hasNext() && !shieldImageIncludedInApk) {
                FileHeader fileHeader = fileHeadersIterator.next();
                if (fileHeader.getCrc32() == crc32.getValue() && fileHeader.getCrc32() != 0) {
                    shieldImageIncludedInApk = true;
                }
            }
            assertTrue(shieldImageIncludedInApk);

        } catch (IOException | ZipException e) {
            e.printStackTrace();
        }

    }

}
