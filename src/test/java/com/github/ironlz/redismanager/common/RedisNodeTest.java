package com.github.ironlz.redismanager.common;

import com.github.ironlz.redismanager.core.Cluster;
import com.github.ironlz.redismanager.core.Node;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.HostAndPort;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class RedisNodeTest {

    @Test
    public void TestClusterCmd() throws ExecutionException, InterruptedException {
        Node n = new Node("9.134.14.176", 9003, null, "CJkh3dfgd49xPaOMSCK1BFVrVWmqjMVthIgAbSbX9h03NCx6");
        Cluster cluster = n.getCluster();
        Map<HostAndPort, Node> nodes = cluster.getNodes();
        nodes.forEach((hp, nd) -> {
            System.out.println(hp);
        });
    }

}