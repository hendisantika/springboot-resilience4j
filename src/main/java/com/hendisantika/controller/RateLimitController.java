package com.hendisantika.controller;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by IntelliJ IDEA.
 * Project : springboot-resilence4j-demo
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 26/09/21
 * Time: 07.40
 */
@RestController
public class RateLimitController {

    Logger logger = LoggerFactory.getLogger(RateLimitController.class);

    @GetMapping("/getMessage")
    @RateLimiter(name = "getMessageRateLimit", fallbackMethod = "getMessageFallBack")
    public ResponseEntity<String> getMessage(@RequestParam(value = "name", defaultValue = "Hello") String name) {

        return ResponseEntity.ok().body("Message from getMessage() :" + name);
    }

    public ResponseEntity<String> getMessageFallBack(RequestNotPermitted exception) {

        logger.info("Rate limit has applied, So no further calls are getting accepted");

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Too many requests : No further request will be accepted. Please try after sometime");
    }
}
