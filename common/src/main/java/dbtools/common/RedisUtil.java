package dbtools.common;

import redis.clients.jedis.Jedis;

public class RedisUtil {
    private static Jedis redis = null;

    public static boolean open() {
        return open("192.168.20.163", 6379);
    }

    /**
     * @param host
     * @param port
     * @return
     */
    public static boolean open(String host, int port) {
        synchronized ("redis init") {
            if (redis == null) {
                redis = new Jedis(host, port);
//                redis.auth("admin");
            } else {
                System.out.println("Please call close() firstly!");
                return false;
            }
        }
        return redis.isConnected();
    }

    public static boolean isOpen() {
        return redis != null && redis.isConnected();
    }

    public static void close() {
        synchronized ("redis close") {
            if (redis != null) {
                redis.close();
                redis = null;
            }
        }
    }

    public static Jedis inst() {
        if (!isOpen()) {
            System.out.println("Please call open() firstly!");
            return null;
        }
        return redis;
    }

    public static boolean exists(String key) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly!");
            return false;
        }
        Boolean ret = redis.exists(key);
        return ret == null ? false : ret;
    }

    public static long del(String key) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly!");
            return 0;
        }
        Long ret = redis.del(key);
        return ret == null ? 0 : ret;
    }

    public static String set(String key, String value) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly!");
            return null;
        }
        return redis.set(key, value);
    }

    public static String get(String key) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly!");
            return null;
        }
        return redis.get(key);
    }

    public static long hset(String key, String field, String value) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly!");
            return 0;
        }
        Long ret = redis.hset(key, field, value);
        return ret == null ? 0 : ret;
    }

    public static String hget(String key, String field) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly!");
            return null;
        }
        return redis.hget(key, field);
    }
}
