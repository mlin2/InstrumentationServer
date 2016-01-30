package org.sentinel.instrumentationserver.instrumentation;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.junit.Test;
import org.sentinel.instrumentationserver.Main;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;

/**
 * This class tests the parts of the server that handle the instrumentation.
 */
public class InstrumentationWithoutMetadataTest extends InstrumentationTestBase {

    /**
     * This test is used for making sure the basic use case of instrumenting an APK without metadata returns the correct
     * APK.
     */
    @Test
    public void testInstrumentWithoutMetadata() {
        MultiPart formDataMultiPartResponseBytes = getFormDataMultiPart();
        byte[] responseBytes = getResponseBytes(formDataMultiPartResponseBytes);

        MultiPart formDataMultiPartApkHash = getFormDataMultiPart();
        String apkHash = getApkHash(formDataMultiPartApkHash);

        try {
            assertArrayEquals(IOUtils.toByteArray(new FileInputStream(
                    Main.INSTRUMENTATION_JOB_DIRECTORY + "/" + apkHash + "/alignedApk.apk")), responseBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
