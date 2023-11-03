package com.github.ironlz.redismanager.entity;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * redis节点信息，用于存储从redis clusternodes， info等命令中解析到状态信息
 */
public class RedisStatus {

    private String nodeId;
    private String addressInfo;
    private String[] roles;
    private String masterId;
    private long pingSent;
    private long pongRecv;
    private long configEpoch;
    private String linkState;
    private final BitSet slots = new BitSet(16384);
    private final Map<Integer, String> importingSlots = new HashMap<>();
    private final Map<Integer, String> exportingSlots = new HashMap<>();

}
