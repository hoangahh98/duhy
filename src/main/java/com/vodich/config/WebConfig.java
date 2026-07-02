package com.vodich.config;

import com.vodich.auth.AuthSession;
import com.vodich.auth.CurrentUser;
import com.vodich.logging.AppLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfig implements WebMvcConfigurer {
    private static final String START_TIME = "requestStartTime";
    private final AuthSession authSession;
    private final AppLogService appLogService;

    public WebConfig(AuthSession authSession, AppLogService appLogService) {
        this.authSession = authSession;
        this.appLogService = appLogService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String path = request.getRequestURI();
                request.setAttribute(START_TIME, System.currentTimeMillis());
                if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/ws")
                    || path.startsWith("/icons/") || path.startsWith("/uploads/")
                    || path.equals("/login") || path.startsWith("/external-register")) {
                    return true;
                }
                if (!authSession.isLoggedIn(request.getSession())) {
                    response.sendRedirect("/login");
                    return false;
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                String path = request.getRequestURI();
                if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/icons/") || path.startsWith("/uploads/") || path.startsWith("/ws")) {
                    return;
                }
                long start = request.getAttribute(START_TIME) instanceof Long value ? value : System.currentTimeMillis();
                CurrentUser user = authSession.current(request.getSession(false)).orElse(null);
                appLogService.recordHttp(request, response, user, ex, System.currentTimeMillis() - start);
            }
        });
    }
}
