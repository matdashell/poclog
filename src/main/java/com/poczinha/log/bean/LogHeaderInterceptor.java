package com.poczinha.log.bean;

import com.poczinha.log.service.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class LogHeaderInterceptor implements WebMvcConfigurer {
    @Value("${audit.log.headerIdentifier:X-log-id}")
    private String logHeaderIdentifier;

    @Value("${audit.log.headerRole:X-log-role}")
    private String logHeaderAuthorization;

    @Autowired
    private LogSessionRegisterManager registerManager;

    @Autowired
    private RegisterService registerService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                if (request.getHeader(logHeaderIdentifier) != null) {
                    registerManager.setIdentifier(request.getHeader(logHeaderIdentifier));
                }
                if (request.getHeader(logHeaderAuthorization) != null) {
                    registerManager.setAuthHeaders(request.getHeader(logHeaderAuthorization));
                }
                return true;
            }

            @Override
            public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
                registerService.saveAllRegisters(
                        registerManager.getRegisterEntities().listIterator(),
                        registerManager.getCorrelationEntity()
                );
            }
        });
    }
}
