package com.intuit.userprofile.helper.impl;

import com.intuit.userprofile.helper.IApiRequestHelper;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@Component
public class ApiRequestHelper implements IApiRequestHelper {

    private final RestTemplate restTemplate;

    @Autowired
    public ApiRequestHelper( final RestTemplate restTemplate ) {
        this.restTemplate = restTemplate;
    }

    /**
     * helps in executing http request
     *
     * @param url the request url
     * @param header header to the passed
     * @param body body of the request
     * @param response expected response class
     *
     * @return the api response after executing the http request
     * @param <Req> request type
     * @param <Res> response type
     */
    @Override
    public <Req, Res> Res post( final String url, final Map<String, String> header, final Req body, final Class<Res> response ) {
        log.info("executing https request with data: url{}, header:{}, body:{}", url, header, body);
        HttpHeaders headers = new HttpHeaders();
        header.forEach(headers::set);
        HttpEntity<Req> entity = new HttpEntity<>(body, headers);

        RetryConfig config = RetryConfig.custom().maxAttempts(2).waitDuration(Duration.ofMillis(100)).build();
        RetryRegistry registry = RetryRegistry.of(config);
        Retry retry = registry.retry("retry");
        Supplier<Res> resSupplier = Retry.decorateSupplier(retry, () -> {
            System.out.println("retry");
            ResponseEntity<Res> resResponseEntity = restTemplate.postForEntity(url, entity, response);
            return resResponseEntity.getBody();
        });
        return resSupplier.get();
    }
}
