package com.github.ironlz.redismanager.common;

import redis.clients.jedis.Jedis;

public interface RedisNode {

    /**
     * 获取当前节点的角色
     * @return 节点可能有多个角色
     */
    String[] getRoles();

    RedisCluster getCluster();

    RedisNode[] getFriends();

    void refreshNodeInfo();

    Jedis getJedis();

}
