package com.reliaquest.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.exception.CustomCreateException;
import com.reliaquest.api.exception.CustomDeleteException;
import com.reliaquest.api.exception.CustomNoDataFoundException;
import com.reliaquest.api.exception.EmpInternalServiceException;
import com.reliaquest.api.request.EmployeeRequest;
import com.reliaquest.api.response.EmployeeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HelperUtils {

    static RetryBackoffSpec retrySpecs = getRetrySpecs();

    @Value("${employee.service.base.url}")
    private static String BASE_URL;

    public static RetryBackoffSpec getRetrySpecs() {
        return Retry.fixedDelay(5, Duration.ofSeconds(3))
                .doBeforeRetry(beforeRetry -> {
                    log.error("Error while connecting to service, message: {}", beforeRetry.failure().getMessage());
                })
                .filter(throwable -> throwable instanceof WebClientResponseException.TooManyRequests)
                .onRetryExhaustedThrow((retrySpecs, retrySignal) -> handleCustomException(new EmpInternalServiceException(retrySignal.failure().getMessage()), "Service is Temporary Unavailable!"));
    }

    public static RuntimeException handleCustomException(Exception e, String operation) {
        log.error("Exception during '{}': {}", operation, e.getMessage());
        if (e instanceof CustomCreateException) return (CustomCreateException) e;
        if (e instanceof CustomDeleteException) return (CustomDeleteException) e;
        if (e instanceof CustomNoDataFoundException) return (CustomNoDataFoundException) e;
        return new EmpInternalServiceException(operation + " : " + e.getMessage());
    }

    public static <T> T processResponse(String response, Class<T> tempClass) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, tempClass);
    }

    public static void deleteEmployeeData(WebClient webClient, String name) {
        log.info("Deleting data using url: {} ", BASE_URL);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", name);
        webClient.method(HttpMethod.DELETE)
                .uri(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(requestBody), HashMap.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new CustomDeleteException("Failed to delete employee")))
                .toBodilessEntity()
                .retryWhen(retrySpecs)
                .block();
    }

    public static String getEmployeeData(WebClient webClient, String url) {
        log.info("Fetching data using url: {} ", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> Mono.error(new EmpInternalServiceException("Failed to fetch employees data.")))
                .bodyToMono(String.class)
                .retryWhen(retrySpecs)
                .block();
    }

    public static EmployeeResponse postEmployeeData(WebClient webClient, String url, EmployeeRequest employee) {
        log.info("Getting data from url: {} ", url);
        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(employee), EmployeeRequest.class)
                .retrieve()
                .onStatus(HttpStatusCode::isError, response -> Mono.error(new CustomCreateException("Failed to create employee")))
                .bodyToMono(EmployeeResponse.class)
                .retryWhen(retrySpecs)
                .block();
    }
}
