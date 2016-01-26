package org.sentinel.instrumentationserver.fetch;

import org.sentinel.instrumentationserver.InstrumentationDAO;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by sebastian on 1/26/16.
 */
public class ApkFetcher {
    public void fetch() {
        InstrumentationDAO instrumentationDAO = InstrumentationDAO.getInstance();
        List<String> repositoryApkLinks = instrumentationDAO.getAllRepositoryApkLinks();
        ScheduledExecutorService executorServiceFirstHalf = Executors.newSingleThreadScheduledExecutor();
        ScheduledExecutorService executorServiceSecondHalf = Executors.newSingleThreadScheduledExecutor();
        FetcherRunner fetcherRunnerFirstHalf = new FetcherRunner();
        FetcherRunner fetcherRunnerSecondHalf = new FetcherRunner();
        fetcherRunnerFirstHalf.setRepositoryApkLinks(repositoryApkLinks.subList(0, repositoryApkLinks.size() / 2));
        fetcherRunnerSecondHalf.setRepositoryApkLinks(repositoryApkLinks.subList(repositoryApkLinks.size() / 2 + 1, repositoryApkLinks.size()));
        final Future futureFirstHalf = executorServiceFirstHalf.submit(fetcherRunnerFirstHalf);
        final Future futureSecondHalf = executorServiceSecondHalf.submit(fetcherRunnerSecondHalf);

        executorServiceFirstHalf.schedule(new Runnable() {
            @Override
            public void run() {
                futureFirstHalf.cancel(true);
            }
        }, 5, TimeUnit.MINUTES);

        executorServiceSecondHalf.schedule(new Runnable() {
            @Override
            public void run() {
                futureSecondHalf.cancel(true);
            }
        }, 5, TimeUnit.MINUTES);
    }

}

