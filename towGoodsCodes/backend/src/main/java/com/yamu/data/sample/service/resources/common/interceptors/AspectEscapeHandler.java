package com.yamu.data.sample.service.resources.common.interceptors;

import cn.hutool.core.bean.BeanUtil;
import com.yamu.data.sample.service.common.util.ReportUtils;
import com.yamu.data.sample.service.resources.common.annotations.EscapeChars;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;
import java.util.concurrent.ConcurrentHashMap;

public class AspectEscapeHandler {

    private static Logger logger = LoggerFactory.getLogger(AspectEscapeHandler.class);

    private static final ConcurrentHashMap<String, Boolean> marker = new ConcurrentHashMap<>();

    public static void escape(JoinPoint joinPoint) throws NoSuchMethodException {
        String signature = joinPoint.getSignature().toString();
        try {
            if (marker.get(signature) == Boolean.FALSE) {
                return;
            }
            Object[] args = joinPoint.getArgs();
            if (args == null || args.length == 0) {
                return;
            }
            Class[] cls = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                cls[i] = args[i].getClass();
            }
            String methodName = joinPoint.getSignature().getName();
            Parameter[] parameters = joinPoint.getSignature().getDeclaringType().getMethod(methodName, cls).getParameters();
            boolean needEscape = doEscape(args, parameters);
            marker.put(signature, needEscape);
        } catch (NoSuchMethodException e) {
            logger.warn("controller parameter Nonstandard： {}", signature);
            marker.put(signature, false);
        } catch (Throwable e) {
            logger.warn("error: {}", e);
            marker.put(signature, false);
        }
    }

    private static boolean doEscape(Object[] args, Parameter[] parameters) {
        if (args == null || parameters == null || args.length != parameters.length) {
            return false;
        }
        boolean needEscape = false;
        for (int i = 0; i < args.length; i++) {
            EscapeChars[] escapeChars = parameters[i].getAnnotationsByType(EscapeChars.class);
            if (escapeChars.length > 0) {
                String[] properties = escapeChars[0].value();
                for (String property : properties) {
                    Object field = BeanUtil.getFieldValue(args[i], property);
                    if (field instanceof String) {
                        String escaped = ReportUtils.escapeChar((String) field);
                        BeanUtil.setFieldValue(args[i], property, escaped);
                        needEscape = true;
                    }
                }
            }
        }
        return needEscape;
    }
}
