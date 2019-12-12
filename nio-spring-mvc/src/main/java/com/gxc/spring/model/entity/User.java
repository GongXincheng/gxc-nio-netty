package com.gxc.spring.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author GongXincheng
 * @date 2019-12-12 10:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Integer id;

    private String username;

    private Integer age;

    private String email;

    private String weChet;

}
