package com.lee.testSaner;

import com.google.common.reflect.ClassPath;
import com.lee.testSaner.annotation.CI;

import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * @Author Lee
 * @Date 2021/2/27
 */
@SuppressWarnings("UnstableApiUsage")
public class TestClassScannerWithGuava {
    public static void main(String[] args) throws IOException {
        ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        classPath.getAllClasses().stream()
                .filter(c -> c.getName().startsWith("com.lee"))
                .map(ClassPath.ClassInfo::load)
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .forEach(x -> System.out.println(x.getName()));
    }

}
