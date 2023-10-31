package com.github.ironlz.redismanager.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisCluster {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCluster.class);

    private final Map<String, RedisNode> nodeId2Node = new ConcurrentHashMap<>(); // 保存节点Id与节点的映射关系

    RedisNode getOrLoadNode(RedisNode redisNode) {
        if (nodeId2Node.containsKey(redisNode.getNodeId())) {

        }
        return null;
    }

}
