package com.gxc.test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

class TestFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        TestFuture testFuture = new TestFuture();
        Future<String> future = testFuture.queryUserInfo("10001"); //返回future
        String userInfo = future.get();
        System.out.println("查询用户信息：" + userInfo);
    }

    private Future<String> queryUserInfo(String userId) {
        FutureTask<String> future = new FutureTask<>(() -> {
            try {
                Thread.sleep(1000);
                return "微信公众号：bugstack虫洞栈 | 用户ID：" + userId;
            } catch (InterruptedException ignored) {}
            return "error";
        });
        new Thread(future).start();
        return future;
    }

}