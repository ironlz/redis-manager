package com.github.ironlz.redismanager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NodeExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(NodeExecutor.class);
    private static final NodeExecutor EXECUTOR = new NodeExecutor();

    public static NodeExecutor getInstance() {
        return EXECUTOR;
    }

    private volatile ExecutorService defaultExecutorService = Executors.newCachedThreadPool();

    public NodeExecutor() {
    }

    public NodeExecutor(ExecutorService defaultExecutorService) {
        this.defaultExecutorService = defaultExecutorService;
    }


    public ExecutorService getExecutorService() {
        return this.defaultExecutorService;
    }

}
