package com.github.ironlz.redismanager.common;

import redis.clients.jedis.Connection;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisSocketFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.util.Arrays;


public class RedisNodeImpl extends Jedis implements RedisNode {

    private String[] roles;
    private String pwd;
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
    public void refreshNodeInfo() {
        String nodes = this.clusterNodes();
        parseClusterNodes(nodes);
    }

    private void parseClusterNodes(String nodes) {
        String[] lines = nodes.split("\n");
        for (String line : lines) {
            String[] parts = line.split("\\s+");
            System.out.println(Arrays.deepToString(parts) + " " + parts.length);
        }
    }

    public RedisNodeImpl() {
    }

    public RedisNodeImpl(String url) {
        super(url);
    }

    public RedisNodeImpl(HostAndPort hp) {
        super(hp);
    }

    public RedisNodeImpl(String host, int port) {
        super(host, port);
    }

    public RedisNodeImpl(String host, int port, JedisClientConfig config) {
        super(host, port, config);
    }

    public RedisNodeImpl(HostAndPort hostPort, JedisClientConfig config) {
        super(hostPort, config);
    }

    public RedisNodeImpl(String host, int port, boolean ssl) {
        super(host, port, ssl);
    }

    public RedisNodeImpl(String host, int port, boolean ssl, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        super(host, port, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
    }

    public RedisNodeImpl(String host, int port, int timeout) {
        super(host, port, timeout);
    }

    public RedisNodeImpl(String host, int port, int timeout, boolean ssl) {
        super(host, port, timeout, ssl);
    }

    public RedisNodeImpl(String host, int port, int timeout, boolean ssl, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        super(host, port, timeout, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
    }

    public RedisNodeImpl(String host, int port, int connectionTimeout, int soTimeout) {
        super(host, port, connectionTimeout, soTimeout);
    }

    public RedisNodeImpl(String host, int port, int connectionTimeout, int soTimeout, int infiniteSoTimeout) {
        super(host, port, connectionTimeout, soTimeout, infiniteSoTimeout);
    }

    public RedisNodeImpl(String host, int port, int connectionTimeout, int soTimeout, boolean ssl) {
        super(host, port, connectionTimeout, soTimeout, ssl);
    }

    public RedisNodeImpl(String host, int port, int connectionTimeout, int soTimeout, boolean ssl, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        super(host, port, connectionTimeout, soTimeout, ssl, sslSocketFactory, sslParameters, hostnameVerifier);
    }

    public RedisNodeImpl(String host, int port, int connectionTimeout, int soTimeout, int infiniteSoTimeout, boolean ssl, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        super(host, port, connectionTimeout, soTimeout, infiniteSoTimeout, ssl, sslSocketFactory, sslParameters,
                hostnameVerifier);
    }

    public RedisNodeImpl(URI uri) {
        super(uri);
    }

    public RedisNodeImpl(URI uri, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        super(uri, sslSocketFactory, sslParameters, hostnameVerifier);
    }

    public RedisNodeImpl(URI uri, int timeout) {
        super(uri, timeout);
    }

    public RedisNodeImpl(URI uri, int timeout, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        super(uri, timeout, sslSocketFactory, sslParameters, hostnameVerifier);
    }

    public RedisNodeImpl(URI uri, int connectionTimeout, int soTimeout) {
        super(uri, connectionTimeout, soTimeout);
    }

    public RedisNodeImpl(URI uri, int connectionTimeout, int soTimeout, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        super(uri, connectionTimeout, soTimeout, sslSocketFactory, sslParameters, hostnameVerifier);
    }

    public RedisNodeImpl(URI uri, int connectionTimeout, int soTimeout, int infiniteSoTimeout, SSLSocketFactory sslSocketFactory, SSLParameters sslParameters, HostnameVerifier hostnameVerifier) {
        super(uri, connectionTimeout, soTimeout, infiniteSoTimeout, sslSocketFactory, sslParameters, hostnameVerifier);
    }

    public RedisNodeImpl(URI uri, JedisClientConfig config) {
        super(uri, config);
    }

    public RedisNodeImpl(JedisSocketFactory jedisSocketFactory) {
        super(jedisSocketFactory);
    }

    public RedisNodeImpl(JedisSocketFactory jedisSocketFactory, JedisClientConfig clientConfig) {
        super(jedisSocketFactory, clientConfig);
    }

    public RedisNodeImpl(Connection connection) {
        super(connection);
    }
}
