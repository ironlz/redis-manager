package com.github.ironlz.redismanager.core;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class NodeStatus {
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

    public NodeStatus(String nodeId, String addressInfo, String[] roles, String masterId, long pingSent, long pongRecv, long configEpoch, String linkState) {
        this.nodeId = nodeId;
        this.addressInfo = addressInfo;
        this.roles = roles;
        this.masterId = masterId;
        this.pingSent = pingSent;
        this.pongRecv = pongRecv;
        this.configEpoch = configEpoch;
        this.linkState = linkState;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getAddressInfo() {
        return addressInfo;
    }

    public String[] getRoles() {
        return roles;
    }

    public String getMasterId() {
        return masterId;
    }

    public long getPingSent() {
        return pingSent;
    }

    public long getPongRecv() {
        return pongRecv;
    }

    public long getConfigEpoch() {
        return configEpoch;
    }

    public String getLinkState() {
        return linkState;
    }

    public BitSet getSlots() {
        return slots;
    }

    public Map<Integer, String> getImportingSlots() {
        return importingSlots;
    }

    public Map<Integer, String> getExportingSlots() {
        return exportingSlots;
    }
}
