package com.github.ironlz.redismanager.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * Redis节点的映射，代表一个Redis节点
 */
public class RedisNode extends Jedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisNode.class);

    private final String ip;
    private final int port;

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

    private final Map<String, RedisNode> friends = new HashMap<>();// 保存观测到的集群其它节点

    public RedisNode(String host, int port, String ip, int port1, String pwd) {
        super(host, port);
        this.ip = ip;
        this.port = port1;
    }


    /**
     * 加载当前节点的基本信息：nodeId，slot信息、角色，关联的节点等
     */
    public void refreshNodeInfo() {
        LOGGER.debug("{}:{} start refreshNodeInfo...", this.ip, this.port);
        String nodes = this.clusterNodes();
        parseClusterNodes(nodes);
        LOGGER.debug("{}:{} end refreshNodeInfo...", this.ip, this.port);
    }

    private void parseClusterNodes(String nodes) {
        LOGGER.debug("{}:{} nodeInfo is {}", this.ip, this.port, nodes);
        if (nodes == null) {
            LOGGER.error("{}:{} can't parse null string.", this.ip, this.port);
            throw new IllegalStateException("nodesInfo is null");
        }
        Scanner lines = new Scanner(nodes);
        while (lines.hasNextLine()) {
            String line = lines.nextLine();
            String[] nodeInfoParts = line.split("\\s+");
            if (nodeInfoParts.length < 8) {
                LOGGER.error("{}:{} unknown node rsp: {}", this.ip, this.port, line);
                throw new IllegalStateException("unknown myself rsp: " + line);
            }
            // https://redis.io/commands/cluster-nodes/
            this.nodeId = nodeInfoParts[0];
            this.addressInfo = nodeInfoParts[1];
            this.roles = nodeInfoParts[2].split(",");
            this.masterId = nodeInfoParts[3];
            this.pingSent = Long.parseLong(nodeInfoParts[4]);
            this.pongRecv = Long.parseLong(nodeInfoParts[5]);
            this.configEpoch = Long.parseLong(nodeInfoParts[6]);
            this.linkState = nodeInfoParts[7];
            if (nodeInfoParts.length > 8) {
                this.exportingSlots.clear();
                this.importingSlots.clear();
                for (int i = 8; i < nodeInfoParts.length; i++) {
                    String slotInfo = nodeInfoParts[i];
                    if (slotInfo.startsWith("[") && slotInfo.endsWith("]")) {
                        slotInfo = slotInfo.substring(1, slotInfo.length() - 1);
                    }
                    String[] slotIndexRange = slotInfo.split("-");
                    if (slotIndexRange.length == 3) {
                        if (slotIndexRange[1].equals(">")) {
                            this.exportingSlots.put(Integer.parseInt(slotIndexRange[0]), slotIndexRange[2]);
                        } else if (slotIndexRange[1].equals("<")) {
                            this.importingSlots.put(Integer.parseInt(slotIndexRange[0]), slotIndexRange[2]);
                        } else {
                            LOGGER.error("{}:{} unknown node format cause slot rsp: {}", this.ip, this.port, line);
                            throw new IllegalStateException("unknown slotInfo format: " + slotInfo);
                        }
                    } else if (slotIndexRange.length == 2) {
                        int startIndex = Integer.parseInt(slotIndexRange[0]);
                        int endIndex = Integer.parseInt(slotIndexRange[1]);
                        this.slots.set(startIndex, endIndex);
                    } else if (slotIndexRange.length == 1) {
                        this.slots.set(Integer.parseInt(slotIndexRange[0]));
                    } else {
                        LOGGER.error("{}:{} unknown node format cause slot size rsp: {}", this.ip, this.port, line);
                        throw new IllegalStateException("unknown slotInfo format cause size: " + slotInfo);
                    }
                }
            }
            if (line.contains("myself")) {

            } else {

            }
        }

    }


    public String getNodeId() {
        return nodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RedisNode redisNode)) {
            return false;
        }
        return port == redisNode.port && Objects.equals(ip, redisNode.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }
}
