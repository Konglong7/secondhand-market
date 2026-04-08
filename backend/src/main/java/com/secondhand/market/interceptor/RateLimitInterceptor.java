package com.secondhand.market.interceptor;

import com.secondhand.market.annotation.RateLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 接口限流拦截器
 * 基于Redis实现滑动窗口限流
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    private static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimit rateLimit = handlerMethod.getMethodAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return true;
        }

        String clientId = getClientId(request);
        String key = RATE_LIMIT_KEY_PREFIX + clientId + ":" + handlerMethod.getMethod().getName();
        int count = rateLimit.count();
        int window = rateLimit.window();

        try {
            Long currentCount = redisTemplate.opsForValue().increment(key);

            if (currentCount == null) {
                return true;
            }

            // 首次请求，设置过期时间
            if (currentCount == 1) {
                redisTemplate.expire(key, window, TimeUnit.SECONDS);
            }

            if (currentCount > count) {
                log.warn("接口限流触发: clientId={}, method={}, count={}, limit={}",
                        clientId, handlerMethod.getMethod().getName(), currentCount, count);
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(429);
                response.getWriter().write("{\"code\":429,\"message\":\"" + rateLimit.message() + "\"}");
                return false;
            }

            // 设置响应头，告知客户端剩余请求次数
            response.setHeader("X-RateLimit-Limit", String.valueOf(count));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, count - currentCount)));

            return true;
        } catch (Exception e) {
            // Redis异常时，不阻止请求，但记录错误
            log.error("限流检查异常: key={}", key, e);
            return true;
        }
    }

    /**
     * 获取客户端标识
     * 优先使用用户ID（已登录），其次使用IP地址
     */
    private String getClientId(HttpServletRequest request) {
        // 尝试从请求头获取用户ID
        String userId = request.getHeader("X-User-Id");
        if (userId != null && !userId.isEmpty()) {
            return "user:" + userId;
        }

        // 使用IP地址
        String ip = getClientIp(request);
        return "ip:" + ip;
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 多个代理的情况下，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}