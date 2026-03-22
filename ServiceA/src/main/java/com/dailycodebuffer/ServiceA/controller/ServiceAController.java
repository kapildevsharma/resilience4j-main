package com.dailycodebuffer.ServiceA.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
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

    private static final Logger log = LoggerFactory.getLogger(ServiceAController.class);

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    public ServiceAController(RestTemplate restTemplate, WebClient webClient) {
        this.restTemplate = restTemplate;
        this.webClient = webClient;
    }

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
		log.info("Retry method called {} times at {}", count.getAndIncrement(), new Date());
		return restTemplate.getForObject(url, String.class);
	}

	@GetMapping("/breaker")
	@CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "serviceAFallback")
	public String serviceACircuitBreaker() {
		String url = BASE_URL + "b";
		log.info("CircuitBreaker method called at {}", new Date());
		return restTemplate.getForObject(url, String.class);
	}

	@GetMapping("/limiter")
	@RateLimiter(name = RATE_LIMITER, fallbackMethod = "serviceAFallback")
	public String serviceALimiter() {

		String url = BASE_URL + "b";
		log.info("RateLimiter method called at {}", new Date());
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
        log.info("resilience method called at {}", new Date());

        // Prefer non-blocking WebClient with timeout and graceful fallback, but preserve API contract (String return)
        Mono<String> userMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .doOnSubscribe(s -> log.debug("WebClient call started to {}", url))
                .doOnSuccess(s -> log.debug("WebClient call succeeded"))
                .doOnError(e -> log.warn("WebClient call failed: {}", e.toString()));

        // Block with a timeout and fall back to RestTemplate or a simple message on error
        try {
            return userMono.block();
        } catch (Exception ex) {
            log.warn("Reactive call failed, falling back to RestTemplate: {}", ex.toString());
            try {
                return restTemplate.getForObject(url, String.class);
            } catch (Exception rex) {
                log.error("RestTemplate fallback failed: {}", rex.toString());
                throw ex; // let Resilience4j handle fallback
            }
        }

    }


    @GetMapping("/breakerRetry")
    @Retry(name = RETRY_DATA, fallbackMethod = "fluxFallback")
    @CircuitBreaker(name = CIRCUIT_BREAKER, fallbackMethod = "fluxFallback")
    public Flux<String> getBreakerRetry() {
        String url = BASE_URL + "b";
        log.info("breakerRetry method called at {}", new Date());
      //  return restTemplate.getForObject(url, String.class);

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.warn("Flux call failed: {}", e.toString()));
    }


    @GetMapping("/bulkHead")
    @Bulkhead(name = THREAD_POOL_BULKHEAD, type = Bulkhead.Type.THREADPOOL) // MUST use CompletableFuture
    public CompletableFuture<String> getBulkHead() {

       /* return CompletableFuture.supplyAsync(() -> {
            String url = BASE_URL + "b";
            System.out.println("BulkHead method called at " + new Date());
            return restTemplate.getForObject(url, String.class);
        });*/

        // return a future from the reactive pipeline with timeout and fallback
        return webClient.get()
                .uri(BASE_URL + "b")
                .retrieve()
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(e -> log.warn("BulkHead reactive call failed: {}", e.toString()))
                .onErrorResume(e -> Mono.just("Bulkhead fallback response"))
                .toFuture();
    }

	public String serviceAFallback(Throwable e) {
		log.warn("serviceAFallback triggered: {}", e.toString());
		return "This is a fallback method for Service A";
	}

    public Flux<String> fluxFallback(Throwable e) {
        log.warn("fluxFallback triggered: {}", e.toString());
        return Flux.just("This is a fluxFallback for Service A: " + e.getMessage());
    }
}
