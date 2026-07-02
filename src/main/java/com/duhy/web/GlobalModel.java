package com.duhy.web;

import com.duhy.auth.AuthSession;
import com.duhy.auth.AppFeature;
import com.duhy.auth.PermissionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Set;

@ControllerAdvice
public class GlobalModel {
    private final AuthSession authSession;
    private final PermissionService permissions;

    public GlobalModel(AuthSession authSession, PermissionService permissions) {
        this.authSession = authSession;
        this.permissions = permissions;
    }

    @ModelAttribute("currentUser")
    public Object currentUser(HttpSession session) {
        return authSession.current(session).orElse(null);
    }

    @ModelAttribute("featureSet")
    public Set<AppFeature> featureSet(HttpSession session) {
        return authSession.current(session).map(permissions::featuresFor).orElse(null);
    }

    @ModelAttribute("permissionService")
    public PermissionService permissionService() {
        return permissions;
    }
}
