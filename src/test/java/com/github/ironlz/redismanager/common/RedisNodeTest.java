package com.github.ironlz.redismanager.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RedisNodeTest {

    @Test
    public void TestClusterCmd() {
        try (RedisNodeImpl node = new RedisNodeImpl("9.134.14.176", 9002, "CJkh3dfgd49xPaOMSCK1BFVrVWmqjMVthIgAbSbX9h03NCx6")) {
            node.refreshNodeInfo();
            System.out.println(node);
        }
    }

}