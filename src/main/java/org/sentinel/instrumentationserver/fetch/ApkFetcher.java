package org.sentinel.instrumentationserver.fetch;

import org.sentinel.instrumentationserver.InstrumentationDAO;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by sebastian on 1/26/16.
 */
public class ApkFetcher {
    public void fetch() {
        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
        List<String> repositoryApkLinks = instrumentationDAO.getAllRepositoryApkLinks();

        FetcherRunner fetcherRunnerFirstHalf = new FetcherRunner();
        fetcherRunnerFirstHalf.setRepositoryApkLinks(repositoryApkLinks.subList(0, repositoryApkLinks.size() / 2));
        ExecutorService executorServiceFirstHalf = Executors.newSingleThreadExecutor();
        executorServiceFirstHalf.execute(fetcherRunnerFirstHalf);
        executorServiceFirstHalf.shutdown();

        FetcherRunner fetcherRunnerSecondHalf = new FetcherRunner();
        fetcherRunnerSecondHalf.setRepositoryApkLinks(repositoryApkLinks.subList(repositoryApkLinks.size() / 2 + 1, repositoryApkLinks.size()));
        ExecutorService executorServiceSecondHalf = Executors.newSingleThreadExecutor();
        executorServiceSecondHalf.execute(fetcherRunnerSecondHalf);
        executorServiceSecondHalf.shutdown();

    }

}

