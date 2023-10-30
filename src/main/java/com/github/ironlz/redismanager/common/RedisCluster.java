package com.github.ironlz.redismanager.common;

public interface RedisCluster {

    RedisNode[] getRedisNodes();

    void refreshClusterInfo();

    void addRedisNode(RedisNode node);
}
