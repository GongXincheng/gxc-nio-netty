package com.gxc.netty.protocoltcp;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 协议包。
 *
 * @author GongXincheng
 * @date 2020-02-23 05:15
 */
@Data
@Builder
public class MessageProtocol {

    /**
     * 长度（关键）
     */
    private int len;
    private byte[] content;

}
