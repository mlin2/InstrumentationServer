package org.sentinel.instrumentationserver.instrumentation;

import java.util.List;

public class RemoteRepositoryApkFetcherRunner implements Runnable {
    List<String> repositoryApkLinks;

    public RemoteRepositoryApkFetcherRunner(List<String> repositoryApkLinks) {
        this.repositoryApkLinks = repositoryApkLinks;
    }

    @Override
    public void run() {
        InstrumentationManager instrumentationManager = new InstrumentationManager();
        instrumentationManager.instrumentFromLinks(repositoryApkLinks);
    }
}
