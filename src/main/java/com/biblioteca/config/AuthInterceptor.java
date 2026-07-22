package com.biblioteca.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        Long usuarioId = session != null ? (Long) session.getAttribute("usuarioId") : null;
        String path = request.getRequestURI();

        // Sin sesion -> al login
        if (usuarioId == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Rutas de administracion -> solo Bibliotecario
        if (path.startsWith("/admin")) {
            String tipoUsuario = (String) session.getAttribute("tipoUsuario");
            if (!"BIBLIOTECARIO".equals(tipoUsuario)) {
                response.sendRedirect("/catalogo");
                return false;
            }
        }

        return true;
    }
}
