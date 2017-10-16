package com.github.jjYBdx4IL.cms.solr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedThreadFactory;

@Startup
@Singleton
public class IndexingTask implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(IndexingTask.class);

    private Thread taskThread = null;
    private final CountDownLatch shutdownLatch = new CountDownLatch(1);
    
    @Resource
    private ManagedThreadFactory threadFactory;

    @PostConstruct
    public void postConstruct() {
        taskThread = threadFactory.newThread(this);
        taskThread.start();
    } 

    @PreDestroy
    public void preDestroy(){
        shutdownLatch.countDown();
        try {
            taskThread.join();
        } catch (InterruptedException ex) {
            LOG.warn("interrupted while waiting for " + taskThread + " to shut down", ex);
        } 
    }
    
    @Override
    public void run() {
        LOG.info("started");
        try {
            while (!shutdownLatch.await(100, TimeUnit.MILLISECONDS)) {
            }
        } catch (InterruptedException ex) {
            LOG.warn("", ex);
        }
        LOG.info("stopped");
    } 
}
