package com.atguigu.distributed.lock.service;

import com.atguigu.distributed.lock.factory.DistributedLockClient;
import com.atguigu.distributed.lock.factory.ZKClient;
import com.atguigu.distributed.lock.mapper.StockMapper;
import com.atguigu.distributed.lock.pojo.Stock;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class StockService_SQL {

    private Stock stock = new Stock();

    private ReentrantLock lock = new ReentrantLock();
    private DistributedLockClient distributedLockClient;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private ZKClient client;

    @Autowired
    private CuratorFramework curatorFramework;

    //curator 可重入锁InterProcessMutex
    public void checkAndLock() {
        InterProcessMutex mutex = new InterProcessMutex(curatorFramework, "/curator/lock");
        try {
            // 加锁
            mutex.acquire();

            // 先查询库存是否充足
            Stock stock = this.stockMapper.selectById(1L);
            // 再减库存
            if (stock != null && stock.getCount() > 0){
                stock.setCount(stock.getCount() - 1);
                this.stockMapper.updateById(stock);
            }

            // this.testSub(mutex);

            // 释放锁
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSub(InterProcessMutex mutex) {

        try {
            mutex.acquire();
            System.out.println("测试可重入锁。。。。");
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //curator 读写锁
    public void testZkReadLock() {
        try {
            InterProcessReadWriteLock rwlock = new InterProcessReadWriteLock(curatorFramework, "/curator/rwlock");
            rwlock.readLock().acquire(10, TimeUnit.SECONDS);
            // TODO：一顿读的操作。。。。
            //rwlock.readLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testZkWriteLock() {
        try {
            InterProcessReadWriteLock rwlock = new InterProcessReadWriteLock(curatorFramework, "/curator/rwlock");
            rwlock.writeLock().acquire(10, TimeUnit.SECONDS);
            // TODO：一顿写的操作。。。。
            //rwlock.writeLock().unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //测试手动写zk分布式锁
//    public void checkAndLock() {
//        // 加锁，获取锁失败重试
//        ZKDistributedLock lock = this.client.getZKDistributedLock("lock");
//        lock.lock();
//
//        // 先查询库存是否充足
//        Stock stock = this.stockMapper.selectById(1L);
//        // 再减库存
//        if (stock != null && stock.getCount() > 0){
//            stock.setCount(stock.getCount() - 1);
//            this.stockMapper.updateById(stock);
//        }
//
//        // 释放锁
//        lock.unlock();
//    }


//    /**
//     * 乐观锁（ Optimistic Locking ） 相对悲观锁而言，乐观锁假设认为数据一般情况下不会造成冲突，所以在数据进行提交更新的时候，
//     * 才会正式对数据的冲突与否进行检测，如果发现冲突了，则重试。那么我们如何实现乐观锁呢
//     * 使用数据版本（Version）记录机制实现，这是乐观锁最常用的实现 方式。一般是通过为数据库表增加一个数字类型的 “version” 字段来实现。
//     * 当读取数据时，将version字段的值一同读出，数据每更新一次，
//     * 对此version值加一。当我们提交更新的时候，判断数据库表对应记录 的当前版本信息与第一次取出来的version值进行比对，
//     * 如果数据库表当前版本号与第一次取出来的version值相等，则予以更新。
//     */
//    public void checkAndLock() {
//
//        // 先查询库存是否充足
//        Stock stock = this.stockMapper.selectById(1L);
//
//        // 再减库存
//        if (stock != null && stock.getCount() > 0){
//            // 获取版本号
//            Long version = stock.getVersion();
//
//            stock.setCount(stock.getCount() - 1);
//            // 每次更新 版本号 + 1
//            stock.setVersion(stock.getVersion() + 1);
//            // 更新之前先判断是否是之前查询的那个版本，如果不是重试
//            if (this.stockMapper.update(stock, new UpdateWrapper<Stock>().eq("id", stock.getId()).eq("version", version)) == 0) {
//                checkAndLock();
//            }
//        }
//    }

    /**
     * 悲观锁：在读取数据时锁住那几行，其他对这几行的更新需要等到悲观锁结束时才能继续 。
     *     SELECT … FOR UPDATE （悲观锁）
     */
//    public void checkAndLock() {
//        this.stockMapper.selectStockForUpdate(1L);
//    }





}
