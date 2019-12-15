## NIO & Netty 学习过程

#### 1：使用NIO完成简易聊天室
```
nio-demo-1
    |-- com.gxc.nio.demo2
        |-- MyClient 客户端启动
        |-- MyServer 服务端启动
```

#### 1.1：使用NIO完成聊天室（升级版）
```
nio-demo-1
    |-- com.gxc.nio
        |-- GroupChatServer 服务端
        |-- GroupChatClient 客户端
```

#### 2：使用BIO完成简单RPC框架(整合Spring)
```
gxc-rpc-sample
    |-- rpc-service
        |-- com.gxc.rpc.server.RpcServerTestWithSpring 服务端单元测试
    |-- rpc-client
        |-- com.gxc.rpc.client.RpcClientTestWithSpring 客户端单元测试
```
#### 3：实现简易的SpringMVC（自带服务器NIO方式）
``` 
nio-spring-mvc
    |-- com.gxc.spring
        |- Main 启动类
```