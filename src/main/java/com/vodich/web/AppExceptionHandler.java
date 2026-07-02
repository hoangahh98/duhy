package com.vodich.web;

import com.vodich.logging.AppLogService;
import com.vodich.logging.LogLevel;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AppExceptionHandler {
    private final AppLogService logs;

    public AppExceptionHandler(AppLogService logs) {
        this.logs = logs;
    }

    @ExceptionHandler(IllegalStateException.class)
    public String forbidden(IllegalStateException ex, Model model) {
        logs.recordSystem(LogLevel.WARN, "EXCEPTION", "Không thể thực hiện thao tác", "", ex);
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    public String runtime(RuntimeException ex, Model model) {
        logs.recordSystem(LogLevel.ERROR, "EXCEPTION", "Lỗi hệ thống", "", ex);
        model.addAttribute("message", "Lỗi hệ thống: " + ex.getMessage());
        return "error";
    }
}
