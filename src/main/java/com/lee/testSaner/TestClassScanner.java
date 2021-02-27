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
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Stream;

import static com.lee.testSaner.annotation.CI.Scope;

/**
 * @Author Lee
 * @Date 2021/2/27
 */
public class TestClassScanner {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        TestClassScanner saner = new TestClassScanner("");
        saner.printTestCase(Scope.DAILY);
//        System.out.println(Arrays.toString(getClasses(sanerClass.getPackage().getName())));
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

    public void printTestCase(Scope... scope) throws IOException, ClassNotFoundException {
//        Stream.of(getClasses())
        Stream.of(RecursiveGetClasses())
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
                System.out.println(currentFilePath);
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

    private Class<?>[] RecursiveGetClasses() throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = this.packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, this.packageName));
        }
        return classes.toArray(new Class[0]);
    }

    private List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, "".equals(packageName) ? file.getName() :
                        packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
