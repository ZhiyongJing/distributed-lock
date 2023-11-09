package com.atguigu.distributed.lock.controller;

import com.atguigu.distributed.lock.service.StockService_SQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController_SQL {

    @Autowired
    private StockService_SQL stockService_sql;

    @GetMapping("check/lock")
    public String checkAndLock(){

        this.stockService_sql.checkAndLock();

        return "验库存并锁库存成功！";
    }

    @GetMapping("test/read")
    public String testRead(){
        stockService_sql.testZkReadLock();

        return "测试读";
    }

    @GetMapping("test/write")
    public String testWrite(){
        stockService_sql.testZkWriteLock();
        return "测试写";
    }

}
