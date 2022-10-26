package com.yamu.data.sample.service.resources.common.annotations;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EscapeChars {
    String[] value() default {};
}