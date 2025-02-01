package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.RequestInputDto;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";
    private final ConcurrentHashMap<String, ResponseEntity<Object>> requestCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> cacheTimePerRequest = new ConcurrentHashMap<>();
    private static final long CACHE_TIME = TimeUnit.MINUTES.toMillis(5);

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(RequestInputDto requestInputDto, Long userId) {
       return post("", userId, requestInputDto);
    }

    public ResponseEntity<Object> getRequests(Long userId) {
        String requestCacheChannel = "one";

        String key = String.valueOf(Objects.hash(requestCacheChannel,userId));

        if (isCacheValid(key)) {
            return requestCache.get(key);
        }

        ResponseEntity<Object> response = get("", userId);
        requestCache.put(key, response);
        cacheTimePerRequest.put(key, System.currentTimeMillis());
        return response;
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        String requestCacheChannel = "two";

        String key = String.valueOf(Objects.hash(requestCacheChannel,userId));

        if (isCacheValid(key)) {
            return requestCache.get(key);
        }

        ResponseEntity<Object> response = get("/all", userId);
        requestCache.put(key, response);
        cacheTimePerRequest.put(key, System.currentTimeMillis());
        return response;
    }

    public ResponseEntity<Object> getRequest(Long requestId, Long userId) {
        String requestCacheChannel = "three";

        String key = String.valueOf(Objects.hash(requestCacheChannel,requestId,userId));

        log.warn(key);

        if (isCacheValid(key)) {
            return requestCache.get(key);
        }

        ResponseEntity<Object> response = get("/" + requestId, userId);
        requestCache.put(key, response);
        cacheTimePerRequest.put(key, System.currentTimeMillis());
        return response;
    }

    private boolean isCacheValid(String key) {
        Long timestamp = cacheTimePerRequest.get(key);
        return timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_TIME;
    }
}
