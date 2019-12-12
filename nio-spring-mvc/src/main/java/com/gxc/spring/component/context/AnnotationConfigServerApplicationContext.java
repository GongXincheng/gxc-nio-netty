package com.gxc.spring.component.context;

import com.gxc.spring.component.server.NioServerApplicationContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author GongXincheng
 * @date 2019-12-12 14:51
 */
@Slf4j
public class AnnotationConfigServerApplicationContext extends NioServerApplicationContext {

    public AnnotationConfigServerApplicationContext(Class<?> config) {
        super(config);
    }

}
