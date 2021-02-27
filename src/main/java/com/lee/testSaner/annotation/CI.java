package com.lee.testSaner.annotation;

import java.lang.annotation.*;

/**
 * @Author Lee
 * @Date 2021/2/27
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CI {
    Scope[] value() default Scope.DAILY;

    enum Scope {
        MICRO("micro"),
        DAILY("daily");
        private final String description;

        Scope(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}

