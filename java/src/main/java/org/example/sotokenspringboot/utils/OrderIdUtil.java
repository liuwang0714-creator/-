package org.example.sotokenspringboot.utils;

public class OrderIdUtil {

    // ===================== 配置区域 =====================
    private final long twepoch = 1672531200000L; // 起始时间戳 (可以自定义，单位毫秒)
    private final long workerIdBits = 5L;        // 机器ID位数
    private final long datacenterIdBits = 5L;    // 数据中心ID位数
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits); // 最大机器ID
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits); // 最大数据中心ID
    private final long sequenceBits = 12L;       // 序列号位数

    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    // ===================== 变量区域 =====================
    private long workerId;        // 机器ID
    private long datacenterId;    // 数据中心ID
    private long sequence = 0L;   // 序列号
    private long lastTimestamp = -1L; // 上次生成ID时间戳

    // ===================== 构造函数 =====================
    public OrderIdUtil(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId 不能大于 %d 或小于 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenterId 不能大于 %d 或小于 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // ===================== 核心方法 =====================
    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("时钟回退了 %d 毫秒，拒绝生成ID", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 序列号用完，等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    // ===================== 工具方法 =====================
    /**
     * 获取订单号字符串（可直接用作订单号）
     */
    public String getOrderId() {
        return String.valueOf(nextId());
    }
}