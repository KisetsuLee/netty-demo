package com.lee.testSaner;

import com.lee.testSaner.annotation.CI;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author Lee
 * @Date 2021/2/27
 */
@CI
public abstract class AbstractTestBase {
    @Getter
    @Setter
    private String testName;

    public abstract void beforeTest();

    public abstract void init();

    public abstract void afterTest();

}
