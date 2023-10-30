package com.github.ironlz.redismanager.common;

public interface RedisNode {

    /**
     * 获取当前节点的角色
     * @return 节点可能有多个角色
     */
    String[] getRoles();

    RedisCluster getCluster();

    RedisNode[] getFriends();

    void refreshNodeInfo();

}
