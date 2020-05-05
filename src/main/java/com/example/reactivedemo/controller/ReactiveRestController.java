package com.example.reactivedemo.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
@RestController
public class ReactiveRestController {

    @GetMapping(path = "/non-blocking-5s")
    public Mono<String> nonBlocking5s(ServerHttpRequest serverHttpRequest) {
        log.info("Endpoint finished: {}, thread: {}",
                serverHttpRequest.getURI(), Thread.currentThread().getName());
        return Mono.delay(Duration.ofSeconds(5))
                .flatMap(v -> Mono.just("done"))
                .doOnSuccess(v -> log.info("Endpoint finished: {}, thread: {}",
                        serverHttpRequest.getURI(), Thread.currentThread().getName()));
    }

    private String blockingCode(String v) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return v;
    }

    @GetMapping(path = "/blocking-5s")
    public Mono<String> blocking5s(ServerHttpRequest serverHttpRequest) {
        log.info("Endpoint finished: {}, thread: {}",
                serverHttpRequest.getURI(), Thread.currentThread().getName());
        return Mono.just("done")
                .map(this::blockingCode)
                .doOnSuccess(v -> log.info("Endpoint finished: {}, thread: {}",
                        serverHttpRequest.getURI(), Thread.currentThread().getName()));
    }

    @GetMapping(path = "/blocking-5s-wrapper")
    public Mono<String> blocking5sWrapper(ServerHttpRequest serverHttpRequest) {
        log.info("Endpoint finished: {}, thread: {}",
                serverHttpRequest.getURI(), Thread.currentThread().getName());
        return Mono.just("done")
                .flatMap(v -> Mono.fromCallable(() -> blockingCode(v))
                        .subscribeOn(Schedulers.boundedElastic())
                )
                .doOnSuccess(v -> log.info("Endpoint finished: {}, thread: {}",
                        serverHttpRequest.getURI(), Thread.currentThread().getName()));
    }

    ExecutorService executorService = Executors.newFixedThreadPool(100);

    @GetMapping(path = "/blocking-5s-scheduler")
    public Mono<String> blocking5sOnScheduler(ServerHttpRequest serverHttpRequest) {
        log.info("Endpoint finished: {}, thread: {}",
                serverHttpRequest.getURI(), Thread.currentThread().getName());
        return Mono.just("done")
//                .publishOn(Schedulers.fromExecutorService(executorService))
                .publishOn(Schedulers.boundedElastic())
                .map(this::blockingCode)
                .doOnSuccess(v -> log.info("Endpoint finished: {}, thread: {}",
                        serverHttpRequest.getURI(), Thread.currentThread().getName()));
    }
}
