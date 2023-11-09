package com.atguigu.distributed.lock.service;

import com.atguigu.distributed.lock.factory.DistributedLockClient;
import com.atguigu.distributed.lock.mapper.StockMapper;
import com.atguigu.distributed.lock.pojo.Stock;
import com.atguigu.distributed.lock.lock.DistributedRedisLock;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StockService_Redis {

    private Stock stock = new Stock();

    private ReentrantLock lock = new ReentrantLock();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private DistributedLockClient distributedLockClient;
    @Autowired
    private StockMapper stockMapper;

    //测试手动写分布式锁
    public void deduct() {
        DistributedRedisLock redisLock = this.distributedLockClient.getRedisLock("lock");
        redisLock.lock();

        try {
            // 1. 查询库存信息
            String stock = redisTemplate.opsForValue().get("stock").toString();

            // 2. 判断库存是否充足
            if (stock != null && stock.length() != 0) {
                Integer st = Integer.valueOf(stock);
                if (st > 0) {
                    // 3.扣减库存
                    redisTemplate.opsForValue().set("stock", String.valueOf(--st));
                }
            }
        } finally {
            redisLock.unlock();
        }
    }
    //测试Redisson 可重入锁（Reentrant Lock）
    public void checkAndLock() {
        // 加锁，获取锁失败重试
        RLock lock = this.redissonClient.getLock("lock");
        lock.lock();

        // 先查询库存是否充足
        Stock stock = this.stockMapper.selectById(1L);
        // 再减库存
        if (stock != null && stock.getCount() > 0){
            stock.setCount(stock.getCount() - 1);
            this.stockMapper.updateById(stock);
        }

        // 释放锁
        lock.unlock();
    }

    //基于Redis的Redisson的分布式信号量
    public void testSemaphore() {
        RSemaphore semaphore = this.redissonClient.getSemaphore("semaphore");
        semaphore.trySetPermits(3);
        try {
            semaphore.acquire();

            TimeUnit.SECONDS.sleep(5);
            System.out.println(System.currentTimeMillis());

            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //    基于Redisson的Redisson分布式闭锁
    public void testLatch() {
        RCountDownLatch latch = this.redissonClient.getCountDownLatch("latch");
        latch.trySetCount(6);

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void testCountDown() {
        RCountDownLatch latch = this.redissonClient.getCountDownLatch("latch");
        latch.trySetCount(6);

        latch.countDown();
    }

    //基于Redis的Redisson分布式可重入读写锁
    public String testRead() {
        RReadWriteLock rwLock = this.redissonClient.getReadWriteLock("rwLock");
        rwLock.readLock().lock(10, TimeUnit.SECONDS);

        System.out.println("测试读锁。。。。");
        // rwLock.readLock().unlock();

        return null;
    }
    public String testWrite() {
        RReadWriteLock rwLock = this.redissonClient.getReadWriteLock("rwLock");
        rwLock.writeLock().lock(10, TimeUnit.SECONDS);

        System.out.println("测试写锁。。。。");
        // rwLock.writeLock().unlock();

        return null;
    }


}
