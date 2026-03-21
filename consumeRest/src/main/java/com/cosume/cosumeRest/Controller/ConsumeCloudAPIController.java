package com.cosume.cosumeRest.Controller;

import java.util.Date;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;


@RestController
@RequestMapping("/consume")
public class ConsumeCloudAPIController {

	@Autowired
	private RestTemplate restTemplate;

	private static final String RETRTY_DATA = "retryServcie";
	private static final String CIRCUIT_BREAKER = "circuitBreakerService";
	private static final String RATE_LIMITER = "rateLimiterService";
    private static final String THREAD_POOL_BULKHEAD = "threadPoolBulkheadService";
	// to check circuit breaker, Retry, RateLmiter 
	//So first start service b project as simple project then run it to access API of service b project
	
	// project Service B
	private static final String BASE_URL = "http://localhost:8081/";

	
	@GetMapping("/rateLimiterService")
	@RateLimiter(name = RATE_LIMITER, fallbackMethod = "fallbackMethod")
	public String getRateLimiter() {
		String url = BASE_URL + "b";
		System.out.println("RATE_LIMITER method called " + new Date());
		return restTemplate.getForObject(url, String.class);	}

	@GetMapping("/retryService")
	@Retry(name = RETRTY_DATA)
	public String getRetryServcie() {
		String url = BASE_URL + "b";
		System.out.println("RateLimiter method called " + " times at " + new Date());
		return restTemplate.getForObject(url, String.class);
	}

	@GetMapping("/circuitBreakerService")
	@CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "fallbackMethod")
    @Retry(name = RETRTY_DATA)
    @RateLimiter(name = RATE_LIMITER)
    @Bulkhead(name = THREAD_POOL_BULKHEAD, type = Bulkhead.Type.THREADPOOL)
    public String getCircuitBreaker() {
		String url = BASE_URL + "b";
		System.out.println("CircuitBreaker method called " + " times at " + new Date());
		return restTemplate.getForObject(url, String.class);
	}

	// Fallback method in case of retries or circuit breaker
	public String fallbackMethod(Exception ex) {
        return "This is a fallback method for Service A";
	}

}
