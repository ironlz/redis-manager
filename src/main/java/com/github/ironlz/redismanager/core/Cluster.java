package com.github.ironlz.redismanager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Cluster {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cluster.class);

    private final Map<HostAndPort, Node> nodes = new ConcurrentHashMap<>();

    Cluster(Node initNode) throws ExecutionException, InterruptedException {
        loadClusterNodes(initNode);
    }

    private void loadClusterNodes(final Node initNode) throws ExecutionException, InterruptedException {
        boolean loadFlag = false;
        if (!nodes.containsKey(initNode.getHostAndPort())) {
            synchronized (nodes) {
                if (nodes.containsKey(initNode.getHostAndPort())) {
                    return;
                } else {
                    nodes.put(initNode.getHostAndPort(), initNode);
                    loadFlag = true;
                }
            }
        }
        if (loadFlag) {
            Future<?> submit = NodeExecutor.getInstance().getExecutorService().submit(() -> {
                Map<HostAndPort, NodeStatus> nodeFriends = initNode.fetchFriends();
                nodeFriends.forEach((friendNodeHost, friendNode) -> {
                    if (!nodes.containsKey(friendNodeHost)) {
                        try {
                            loadClusterNodes(new Node(friendNodeHost, initNode.getUser(), initNode.getPwd()));
                        } catch (ExecutionException | InterruptedException e) {
                            throw new RuntimeException(friendNodeHost + " load failed", e);
                        }
                    }
                });
            });
            // todo 此处在固定线程数量下会死锁，需修改
            submit.get();
        }
    }

    public Map<HostAndPort, Node> getNodes() {
        return nodes;
    }
}
