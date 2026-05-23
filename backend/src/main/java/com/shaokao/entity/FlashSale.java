package com.shaokao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("flash_sale")
public class FlashSale {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private BigDecimal price;
    private Integer count;
    private LocalDateTime saleTime;
    private LocalDateTime createTime;

    // 关联字段，非数据库列
    private transient String productName;
    private transient String productImage;
    private transient BigDecimal originalPrice;
}
