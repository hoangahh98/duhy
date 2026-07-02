package com.vodich.logging;

import com.vodich.auth.UserRole;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Entity
@Table(name = "app_log")
public class AppLog {
    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private LogLevel level;
    private String category;
    private String action;
    private String method;
    private String path;
    @Column(name = "query_string")
    private String queryString;
    @Column(name = "status_code")
    private Integer statusCode;
    @Column(name = "duration_ms")
    private Long durationMs;
    @Column(name = "user_id")
    private Long userId;
    private String username;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole userRole;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "user_agent")
    private String userAgent;
    @Column(columnDefinition = "TEXT")
    private String details;
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    protected AppLog() {
    }

    public AppLog(LogLevel level, String category, String action) {
        this.createdAt = LocalDateTime.now(ZoneOffset.UTC);
        this.level = level;
        this.category = category;
        this.action = action;
    }

    public Long getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCreatedAtVietnam() {
        return createdAt == null ? null : createdAt.atOffset(ZoneOffset.UTC).atZoneSameInstant(VIETNAM_ZONE).toLocalDateTime();
    }
    public LogLevel getLevel() { return level; }
    public String getCategory() { return category; }
    public String getAction() { return action; }
    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getQueryString() { return queryString; }
    public Integer getStatusCode() { return statusCode; }
    public Long getDurationMs() { return durationMs; }
    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public UserRole getUserRole() { return userRole; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public String getDetails() { return details; }
    public String getErrorMessage() { return errorMessage; }

    public void setRequest(String method, String path, String queryString, Integer statusCode, Long durationMs) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.statusCode = statusCode;
        this.durationMs = durationMs;
    }

    public void setUser(Long userId, String username, UserRole userRole) {
        this.userId = userId;
        this.username = username;
        this.userRole = userRole;
    }

    public void setClient(String ipAddress, String userAgent) {
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
