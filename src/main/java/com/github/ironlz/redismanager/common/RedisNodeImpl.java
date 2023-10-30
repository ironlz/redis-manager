package com.github.ironlz.redismanager.common;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


class RedisNodeImpl extends Jedis implements RedisNode {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisNodeImpl.class);

    private String ip;
    private int port;
    private String pwd;

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

    private RedisCluster redisCluster;


    @Override
    public String[] getRoles() {
        return new String[0];
    }

    @Override
    public RedisCluster getCluster() {
        return null;
    }

    @Override
    public RedisNode[] getFriends() {
        return new RedisNode[0];
    }

    @Override
    public Jedis getJedis() {
        return null;
    }

    @Override
    public void refreshNodeInfo() {
        String nodes = this.clusterNodes();
        parseClusterNodes(nodes);
    }

    private void parseClusterNodes(String nodes) {
        LOGGER.debug("{}:{} parse cluster nodes rsp: {}", this.ip, this.port, nodes);
        if (nodes == null) {
            throw new IllegalStateException("nodesInfo is null");
        }
        Scanner lines = new Scanner(nodes);
        while (lines.hasNextLine()) {
            String line = lines.nextLine();
            if (line.contains("myself")) {
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
            }
        }
    }

    @Override
    public String toString() {
        return "RedisNodeImpl{" + "ip='" + ip + '\'' + ", port=" + port + ", pwd='" + pwd + '\'' + ", nodeId='" + nodeId + '\'' + ", addressInfo='" + addressInfo + '\'' + ", roles=" + Arrays.toString(
                roles) + ", masterId='" + masterId + '\'' + ", pingSent=" + pingSent + ", pongRecv=" + pongRecv + ", configEpoch=" + configEpoch + ", linkState='" + linkState + '\'' + ", slots=" + slots + ", importingSlots=" + importingSlots + ", exportingSlots=" + exportingSlots + ", redisCluster=" + redisCluster + '}';
    }

    private void initPwdAndAuth(String pwd) {
        this.pwd = pwd;
        auth(pwd);
    }

    public RedisNodeImpl(String ip, int port, String pwd) {
        super(ip, port);
        this.ip = ip;
        this.port = port;
        initPwdAndAuth(pwd);
    }


}
