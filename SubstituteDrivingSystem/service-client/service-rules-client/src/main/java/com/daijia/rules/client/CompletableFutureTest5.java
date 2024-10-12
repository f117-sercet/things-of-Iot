package com.daijia.rules.client;

import lombok.SneakyThrows;

import java.util.concurrent.*;

/**
 * Description： TODO
 *
 * @author: 段世超
 * @aate: Created in 2024/10/12 9:54
 */
public class CompletableFutureTest5 {

    @SneakyThrows
    public static void main(String[] args) {
        // 服务器核数
        int processors = Runtime.getRuntime().availableProcessors();

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                processors+1, // 核心线程个数 io:2n ,cpu: n+1  n:内核数据
                processors+1,
                0,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
        CompletableFuture<String> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("123");
            return "123";
        }, executor);
        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("456");
            return "456";
        }, executor);
        CompletableFuture<String> future03 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务3";
        }, executor);

        // 串联起若干个线程任务, 没有返回值
        CompletableFuture<Void> all = CompletableFuture.allOf(future01, future02, future03);

        // 等待所有线程完成
        // .join()和.get()都会阻塞并获取线程的执行情况
        // .join()会抛出未经检查的异常，不会强制开发者处理异常 .get()会抛出检查异常，需要开发者处理
        all.join();
        all.get();
    }
}
