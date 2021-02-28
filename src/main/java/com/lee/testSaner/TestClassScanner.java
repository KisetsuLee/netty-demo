package com.lee.testSaner;

import com.lee.testSaner.annotation.CI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.lee.testSaner.annotation.CI.Scope;

/**
 * @Author Lee
 * @Date 2021/2/27
 */
public class TestClassScanner {
    public static void main(String[] args) throws IOException {
        String pName = System.getProperty("package", null);
        TestClassScanner saner = new TestClassScanner(pName);
        saner.printTestCase(Scope.DAILY);
    }

    private final String packageName;
    private final Path path;

    public TestClassScanner(String packageName) throws FileNotFoundException {
        Class<? extends TestClassScanner> sanerClass = getClass();
        if (packageName == null) {
            this.packageName = sanerClass.getPackage().getName();
        } else {
            this.packageName = packageName;
        }
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        URL resource;
        if (this.packageName.equals("")) {
            resource = contextClassLoader.getResource("");
        } else {
            resource = contextClassLoader.getResource(this.packageName.replace(".", "/"));
        }
        if (resource == null) throw new FileNotFoundException(this.packageName + " not found in classpath");
        this.path = new File(resource.getFile()).toPath();
    }

    public void printTestCase(Scope... scope) throws IOException {
        Stream.of(getClasses())
                .filter(c -> {
                    if (Modifier.isAbstract(c.getModifiers())) return false;
                    CI annotation = c.getAnnotation(CI.class);
                    if (annotation == null) return false;
                    Scope[] scopes = annotation.value();
                    return Arrays.asList(scopes).containsAll(Arrays.asList(scope));
                }).forEach(c -> System.out.println(c.getName()));
    }

    private Class<?>[] getClasses() throws IOException {
        List<String> fileNames = new ArrayList<>();
        // use stack convert package to path info: com.lee -> com/lee/
        List<String> pathList = "".equals(packageName) ? new ArrayList<>() :
                new ArrayList<>(Arrays.asList(this.packageName.split("\\.")));
        Files.walkFileTree(this.path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (!dir.getFileName().equals(path.getFileName())) {
                    pathList.add(dir.getFileName().toString());
                }
                return super.preVisitDirectory(dir, attrs);
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (!dir.getFileName().equals(path.getFileName())) {
                    pathList.remove(pathList.size() - 1);
                }
                return super.postVisitDirectory(dir, exc);
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String currentFilePath = String.join("/", pathList) + "/" + file.getFileName().toString();
                fileNames.add(currentFilePath);
                return super.visitFile(file, attrs);
            }
        });

        return fileNames.stream().filter(fn -> fn.endsWith(".class")).map(this::getClazz).toArray(Class[]::new);
    }


    public Class<?> getClazz(String classPath) {
        try {
            return Class.forName(classPath.replace("/", ".").substring(0, classPath.length() - 6));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
