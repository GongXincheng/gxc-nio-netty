package com.gxc.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * RPC协议.
 *
 * @author GongXincheng
 * @date 2020-03-01 02:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageProtocol {

    /**
     * content的总长度
     */
    private int length;

    /**
     * 对象 -> 字节数组
     */
    private byte[] content;

}
