package com.vodich.logging;

import com.vodich.auth.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppLogService {
    private final AppLogRepository logs;

    public AppLogService(AppLogRepository logs) {
        this.logs = logs;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordHttp(HttpServletRequest request, HttpServletResponse response, CurrentUser user, Throwable error, long durationMs) {
        try {
            int status = response.getStatus();
            LogLevel level = error != null || status >= 500 ? LogLevel.ERROR : status >= 400 ? LogLevel.WARN : LogLevel.INFO;
            AppLog log = new AppLog(level, "HTTP", request.getMethod() + " " + request.getRequestURI());
            log.setRequest(request.getMethod(), request.getRequestURI(), request.getQueryString(), status, durationMs);
            if (user != null) {
                log.setUser(user.id(), user.email(), user.role());
            }
            log.setClient(clientIp(request), trim(request.getHeader("User-Agent"), 500));
            log.setDetails(params(request));
            if (error != null) {
                log.setErrorMessage(trim(error.getClass().getSimpleName() + ": " + error.getMessage(), 2000));
            }
            logs.save(log);
        } catch (RuntimeException ignored) {
            // Logging must never break the user request.
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSystem(LogLevel level, String category, String action, String details, Throwable error) {
        try {
            AppLog log = new AppLog(level, category, action);
            log.setDetails(trim(details, 2000));
            if (error != null) {
                log.setErrorMessage(trim(error.getClass().getSimpleName() + ": " + error.getMessage(), 2000));
            }
            logs.save(log);
        } catch (RuntimeException ignored) {
            // Logging must never break business flow.
        }
    }

    private String params(HttpServletRequest request) {
        if (request.getParameterMap().isEmpty()) {
            return "";
        }
        return request.getParameterMap().entrySet().stream()
            .filter(entry -> !entry.getKey().toLowerCase().contains("password"))
            .map(this::paramEntry)
            .collect(Collectors.joining("&"));
    }

    private String paramEntry(Map.Entry<String, String[]> entry) {
        return entry.getKey() + "=" + trim(String.join(",", entry.getValue()), 300);
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String trim(String value, int max) {
        if (value == null) {
            return null;
        }
        return value.length() <= max ? value : value.substring(0, max);
    }
}
