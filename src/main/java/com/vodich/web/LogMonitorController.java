package com.vodich.web;

import com.vodich.auth.AuthSession;
import com.vodich.auth.CurrentUser;
import com.vodich.auth.PermissionService;
import com.vodich.logging.AppLogRepository;
import com.vodich.logging.LogLevel;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LogMonitorController {
    private final AppLogRepository logs;
    private final AuthSession authSession;
    private final PermissionService permissions;

    public LogMonitorController(AppLogRepository logs, AuthSession authSession, PermissionService permissions) {
        this.logs = logs;
        this.authSession = authSession;
        this.permissions = permissions;
    }

    @GetMapping("/logs")
    public String index(HttpSession session, @RequestParam(defaultValue = "ERROR") String level, Model model) {
        CurrentUser user = authSession.current(session).orElseThrow();
        if (!permissions.isRoot(user)) {
            throw new IllegalStateException("Chỉ admin gốc được xem log hệ thống");
        }
        LogLevel filter = parseLevel(level);
        model.addAttribute("logs", filter == null ? logs.findTop200ByOrderByCreatedAtDesc() : logs.findTop200ByLevelOrderByCreatedAtDesc(filter));
        model.addAttribute("level", filter == null ? "ALL" : filter.name());
        model.addAttribute("levels", new String[] {"ERROR", "WARN", "INFO", "ALL"});
        return "logs/index";
    }

    private LogLevel parseLevel(String value) {
        if ("ALL".equalsIgnoreCase(value)) {
            return null;
        }
        try {
            return LogLevel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return LogLevel.ERROR;
        }
    }
}
