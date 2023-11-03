package com.github.ironlz.redismanager.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class Node extends Jedis {
    private static final Logger LOGGER = LoggerFactory.getLogger(Node.class);
    private final HostAndPort hostAndPort;

    private Cluster cluster;
    private String pwd;
    private String user;


    public Node(String ip, int port, String user, String pwd) {
        super(ip, port);
        this.hostAndPort = new HostAndPort(ip, port);
        initAuth(user, pwd);
    }

    public Node(HostAndPort hostAndPort, String user, String pwd) {
        super(hostAndPort);
        this.hostAndPort = hostAndPort;
        initAuth(user, pwd);
    }

    private void initAuth(String user, String pwd) {
        this.user = user;
        this.pwd = pwd;
        if (this.pwd != null && !this.pwd.isEmpty()) {
            if (this.user != null && !this.user.isEmpty()) {
                auth(this.user, this.pwd);
            } else {
                auth(this.pwd);
            }
        }
    }

    public Cluster getCluster() throws ExecutionException, InterruptedException {
        if (this.cluster == null) {
            this.cluster = new Cluster(this);
        }
        return this.cluster;
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }

    public Map<HostAndPort, NodeStatus> fetchFriends() {
        Map<HostAndPort, NodeStatus> result = new HashMap<>();

        LOGGER.debug("[{}] start fetchNodes...", hostAndPort);
        String nodes = clusterNodes();
        if (nodes == null) {
            LOGGER.error("{} can't parse null string.", this.hostAndPort);
            throw new IllegalStateException("nodesInfo is null");
        }
        Scanner lines = new Scanner(nodes);
        while (lines.hasNextLine()) {
            String line = lines.nextLine();
            String[] nodeInfoParts = line.split("\\s+");
            if (nodeInfoParts.length < 8) {
                LOGGER.error("{} unknown node rsp: {}", this.hostAndPort, line);
                throw new IllegalStateException("unknown myself rsp: " + line);
            }
            // https://redis.io/commands/cluster-nodes/
            String nodeId = nodeInfoParts[0];
            String addressInfo = nodeInfoParts[1];
            String[] roles = nodeInfoParts[2].split(",");
            String masterId = nodeInfoParts[3];
            long pingSent = Long.parseLong(nodeInfoParts[4]);
            long pongRecv = Long.parseLong(nodeInfoParts[5]);
            long configEpoch = Long.parseLong(nodeInfoParts[6]);
            String linkState = nodeInfoParts[7];
            Map<Integer, String> importingSlots = new HashMap<>();
            Map<Integer, String> exportingSlots = new HashMap<>();
            BitSet slots = new BitSet(16384);
            if (nodeInfoParts.length > 8) {
                for (int i = 8; i < nodeInfoParts.length; i++) {
                    String slotInfo = nodeInfoParts[i];
                    if (slotInfo.startsWith("[") && slotInfo.endsWith("]")) {
                        slotInfo = slotInfo.substring(1, slotInfo.length() - 1);
                    }
                    String[] slotIndexRange = slotInfo.split("-");
                    if (slotIndexRange.length == 3) {
                        if (slotIndexRange[1].equals(">")) {
                            exportingSlots.put(Integer.parseInt(slotIndexRange[0]), slotIndexRange[2]);
                        } else if (slotIndexRange[1].equals("<")) {
                            importingSlots.put(Integer.parseInt(slotIndexRange[0]), slotIndexRange[2]);
                        } else {
                            LOGGER.error("{} unknown node format cause slot rsp: {}", this.hostAndPort, line);
                            throw new IllegalStateException("unknown slotInfo format: " + slotInfo);
                        }
                    } else if (slotIndexRange.length == 2) {
                        int startIndex = Integer.parseInt(slotIndexRange[0]);
                        int endIndex = Integer.parseInt(slotIndexRange[1]);
                        slots.set(startIndex, endIndex);
                    } else if (slotIndexRange.length == 1) {
                        slots.set(Integer.parseInt(slotIndexRange[0]));
                    } else {
                        LOGGER.error("{} unknown node format cause slot size rsp: {}", this.hostAndPort, line);
                        throw new IllegalStateException("unknown slotInfo format cause size: " + slotInfo);
                    }
                }
            }
            // todo 判定方式不好，存在极小的可能性会因为nodeId误判
            if (!line.contains("myself")) {
                NodeStatus status = new NodeStatus(nodeId, addressInfo, roles, masterId, pingSent, pongRecv, configEpoch, linkState);
                String[] ipAndPort = addressInfo.split("@")[0].split(":");
                result.put(new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1])), status);
            }
        }
        return result;

    }

    public String getPwd() {
        return pwd;
    }

    public String getUser() {
        return user;
    }
}
