package com.gxc.rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author GongXincheng
 * @date 2020-03-02 00:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReq implements Serializable {

    private Integer id;
    private String name;
    private BigDecimal price;
    private LocalDateTime createTime;

}
