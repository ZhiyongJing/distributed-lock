package com.atguigu.distributed.lock.mapper;

import com.atguigu.distributed.lock.pojo.Stock;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface StockMapper extends BaseMapper<Stock> {

    public Stock selectStockForUpdate(Long id);
}
