package com.duhy.web;

import com.duhy.auth.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final AuthSession authSession;
    private final PermissionService permissions;

    public HomeController(AuthSession authSession, PermissionService permissions) {
        this.authSession = authSession;
        this.permissions = permissions;
    }

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        CurrentUser user = authSession.current(session).orElseThrow();
        model.addAttribute("features", permissions.featuresFor(user));
        return "home";
    }
}
