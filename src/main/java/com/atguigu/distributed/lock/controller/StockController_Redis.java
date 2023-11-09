package com.atguigu.distributed.lock.controller;

import com.atguigu.distributed.lock.service.StockService_Redis;
import com.atguigu.distributed.lock.service.StockService_SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController_Redis {

    @Autowired
    private StockService_Redis stockService_redis;



    @GetMapping("stock/deduct")
    public String deduct(){
        this.stockService_redis.deduct();
        return "hello stock deduct！！";
    }

    @GetMapping("stock/checkandLock")
    public String checkAndLock(){
        this.stockService_redis.checkAndLock();
        return "hello stock deduct！！";
    }

    @GetMapping("test/semaphore")
    public String testSemaphore(){
        this.stockService_redis.testSemaphore();

        return "测试信号量";
    }

    @GetMapping("test/latch")
    public String testLatch(){
        this.stockService_redis.testLatch();

        return "班长锁门。。。";
    }

    @GetMapping("test/countdown")
    public String testCountDown(){
        this.stockService_redis.testCountDown();

        return "出来了一位同学";
    }



    @GetMapping("test/read")
    public String testRead(){
        String msg = stockService_redis.testRead();

        return "测试读";
    }

    @GetMapping("test/write")
    public String testWrite(){
        String msg = stockService_redis.testWrite();

        return "测试写";
    }


}
