package org.sentinel.instrumentationserver;

import org.sentinel.instrumentationserver.instrumentation.InstrumentationManager;

import java.util.List;

public class RemoteRepositoryApkFetcherRunner implements Runnable {
    long timeoutForInstrumentation;
    List<String> repositoryApkLinks;

    public RemoteRepositoryApkFetcherRunner(long timeoutForInstrumentation, List<String> repositoryApkLinks) {
        this.timeoutForInstrumentation = timeoutForInstrumentation;
        this.repositoryApkLinks = repositoryApkLinks;
    }

    @Override
    public void run() {
        InstrumentationManager instrumentationManager = new InstrumentationManager(timeoutForInstrumentation);
        instrumentationManager.instrumentFromLinks(repositoryApkLinks);
    }
}
