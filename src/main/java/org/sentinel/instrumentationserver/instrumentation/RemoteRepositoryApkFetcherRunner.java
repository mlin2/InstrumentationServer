package org.sentinel.instrumentationserver.instrumentation;

import java.util.List;

/**
 * This class fetches APKs for instrumentation in a separate thread.
 */
public class RemoteRepositoryApkFetcherRunner implements Runnable {
    List<String> repositoryApkLinks;

    public RemoteRepositoryApkFetcherRunner(List<String> repositoryApkLinks) {
        this.repositoryApkLinks = repositoryApkLinks;
    }

    /**
     * Make the InstrumentationManager instrument all APKs from the list of links.
     */
    @Override
    public void run() {
        InstrumentationManager instrumentationManager = new InstrumentationManager();
        instrumentationManager.instrumentFromLinks(repositoryApkLinks);
    }
}
