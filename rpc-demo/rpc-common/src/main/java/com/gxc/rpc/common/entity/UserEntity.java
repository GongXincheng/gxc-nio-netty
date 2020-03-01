package com.gxc.rpc.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author GongXincheng
 * @date 2020-03-01 02:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements Serializable {

    private Integer id;
    private String username;
    private LocalDateTime birthday;

}
