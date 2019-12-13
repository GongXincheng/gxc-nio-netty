package com.gxc.spring.component.util;

import com.gxc.spring.component.model.HttpRequestAttribute;
import com.gxc.spring.model.constant.StringConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 解析请求消息头和请求消息体
 *
 * @author GongXincheng
 * @date 2019-12-13 11:52
 */
@Slf4j
public class HttpParseUtil {

    /**
     * 解析请求消息头和请求消息体
     *
     * @param message 请求内容字符串
     * @return HttpRequestAttribute
     */
    public static HttpRequestAttribute parse(String message) {
        System.out.println("//////////////");
        System.out.println(message);
        System.out.println("//////////////");

        log.info("|-----------> start parse http body");
        HttpRequestAttribute request = new HttpRequestAttribute();

        // 先根据换行符进行分割
        String[] afterSeparator = message.split(StringConstant.SEPARATOR_R_N);

        Map<String, String> headers = new HashMap<>(16);
        int nullLine = 0;
        for (int i = 0; i < afterSeparator.length; i++) {
            // 获取请求方法、请求uri等
            if (i == 0) {
                String[] arr = afterSeparator[i].split(" ");
                request.setMethod(arr[0]);
                request.setUri(arr[1]);
                request.setProtocol(arr[2]);
                continue;
            }

            // 获取请求头
            String[] split = afterSeparator[i].split(": ");
            // 如果是空行，则说明是请求头和请求体的分隔符
            if (split.length == 1 && StringUtils.isBlank(split[0])) {
                nullLine = i;
                break;
            } else {
                headers.put(split[0], split[1]);
            }
        }

        // GET请求的时候如果请求体为空，则nullLine = 0
        if (nullLine != 0) {
            String[] bodyArr = Arrays.copyOfRange(afterSeparator, nullLine + 1, afterSeparator.length);
            String body = String.join(StringConstant.SEPARATOR, bodyArr);
            request.setBody(body);
        }
        request.setHeaders(headers);

        log.info("|----------- finish parse http body success !");
        return request;
    }

}
