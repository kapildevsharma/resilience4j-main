package com.dailycodebuffer.ServiceA.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/*Resilience4j Tutorial with Spring Boot
 * Circuit Breaker, Retry, Rate Limiter : 
 * URL : https://www.youtube.com/watch?v=9AXAUlp3DBw
*/
@RestController
@RequestMapping("/a")
public class ServiceAController {

	@Autowired
	private RestTemplate restTemplate;

    @Autowired
    private WebClient webClient;

	private static final String BASE_URL = "http://localhost:8081/";
    private static final String RETRY_DATA = "retryService";
    private static final String CIRCUIT_BREAKER = "circuitBreakerService";
    private static final String RATE_LIMITER = "rateLimiterService";
    private static final String BULKHEAD = "bulkheadService";
    private static final String THREAD_POOL_BULKHEAD = "threadPoolBulkheadService";
	private static final String SERVICE_A = "serviceA";

    AtomicInteger count = new AtomicInteger(1);

	@GetMapping("/retryBreaker")
	@Retry(name = RETRY_DATA, fallbackMethod = "serviceAFallback")
	public String serviceA() {
		String url = BASE_URL + "b";
		System.out.println("Retry method called " + count.getAndIncrement() + " times at " + new Date());
		return restTemplate.getForObject(url, String.class);
	}

	@GetMapping("/breaker")
	@CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "serviceAFallback")
	public String serviceACircuitBreaker() {
		String url = BASE_URL + "b";
		System.out.println("CircuitBreaker method called times at " + new Date());
		return restTemplate.getForObject(url, String.class);
	}

	@GetMapping("/limiter")
	@RateLimiter(name = RATE_LIMITER, fallbackMethod = "serviceAFallback")
	public String serviceALimiter() {

		String url = BASE_URL + "b";
		System.out.println("RateLimiter method called " + " times at " + new Date());
		return restTemplate.getForObject(url, String.class);
	}


// Bulkhead → RateLimiter → CircuitBreaker → Retry → Service Call
    // Bulkhead limits threads first
    //RateLimiter controls traffic
    //CircuitBreaker monitors failures
    //Retry handles transient failures
    @GetMapping("/resilience")
  //  @Bulkhead(name = THREAD_POOL_BULKHEAD, type = Bulkhead.Type.THREADPOOL)
    @RateLimiter(name = RATE_LIMITER, fallbackMethod = "serviceAFallback")
    @CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "serviceAFallback")
    @Retry(name = RETRY_DATA, fallbackMethod = "serviceAFallback")
    public String getResilience() {
        String url = BASE_URL + "b";
        System.out.println("resilience method called times at " + new Date());
        // Traditional blocking call
        String response = restTemplate.getForObject(url, String.class);

        // Reactive non-blocking example using WebClient
        Mono<String> userMono = webClient.get().uri(url).retrieve().bodyToMono(String.class);
        // If you need to block and get the value (not recommended for reactive apps), use block():
        response = userMono.block();
        // Return the blocking RestTemplate response by default to keep existing behavior
        return response;

    }


    @GetMapping("/breakerRetry")
    @Retry(name = RETRY_DATA, fallbackMethod = "serviceAFallback")
    @CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "serviceAFallback")
    public String getBreakerRetry() {
        String url = BASE_URL + "b";
        System.out.println("breakerRetry method called times at " + new Date());
        return restTemplate.getForObject(url, String.class);
    }


    @GetMapping("/bulkHead")
    @Bulkhead(name = THREAD_POOL_BULKHEAD, type = Bulkhead.Type.THREADPOOL) // MUST use CompletableFuture
    public CompletableFuture<String> getBulkHead() {

        return CompletableFuture.supplyAsync(() -> {
            String url = BASE_URL + "b";
            System.out.println("BulkHead method called at " + new Date());
            return restTemplate.getForObject(url, String.class);
        });
    }
	public String serviceAFallback(Exception e) {
		return "This is a fallback method for Service A";
	}
}
