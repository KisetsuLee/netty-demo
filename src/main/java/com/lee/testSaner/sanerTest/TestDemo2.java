package com.lee.testSaner.sanerTest;

import com.lee.testSaner.AbstractTestBase;
import com.lee.testSaner.annotation.CI;

import static com.lee.testSaner.annotation.CI.Scope;

/**
 * @Author Lee
 * @Date 2021/2/27
 */
@CI({Scope.DAILY, Scope.MICRO})
public class TestDemo2 extends AbstractTestBase {
    @Override
    public void beforeTest() {
        System.out.println("before test");
    }

    @Override
    public void init() {
        System.out.println("init test");
    }

    @Override
    public void afterTest() {
        System.out.println("after test");
    }
}
