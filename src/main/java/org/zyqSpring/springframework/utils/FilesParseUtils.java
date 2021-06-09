package org.zyqSpring.springframework.utils;

import org.zyqSpring.boot.annotation.ZyqSpringApplication;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Enzo Cotter on 2021/6/9.
 */
public class FilesParseUtils {


    public Class<?> getApplicationClass(String root) throws ClassNotFoundException {
        List<String> filePaths = new ArrayList<>();
        List<String> packages = getAllPackages(root, filePaths);
        String scanPackage = null;
        for (String aPackage : packages) {
            URL url = this.getClass().getClassLoader().getResource(aPackage.replaceAll("\\.", "/"));
            File files = new File(url.getFile());
            for (File file : files.listFiles()) {
                if (file.isDirectory()) {
                    continue;
                } else {
                    if (file.getName().endsWith(".class") || file.getName().endsWith(".java")) {
                        String realClass = file.getName().substring(0, file.getName().lastIndexOf("."));
                        scanPackage = aPackage + "." + realClass;
                        Class<?> aClass = Class.forName(scanPackage);
                        if (aClass.isAnnotationPresent(ZyqSpringApplication.class)) {
                            return aClass;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static List<String> getAllPackages(String root, List<String> filePaths) {
        getAllPaths(root, filePaths);
        List<String> packages = new ArrayList<String>();
        for (String filePath : filePaths) {
            root = root.replaceAll("/", "\\\\");
            filePath = filePath.replace(root, "");
            filePath = filePath.replaceAll("\\\\", ".").substring(1, filePath.length());
            if (filePath.contains(".")) {
                packages.add(filePath);
            }
        }
        return packages;
    }


    private static List<String> getAllPaths(String root, List<String> filePaths) {
        File files = new File(root);
        for (File file : files.listFiles()) {
            if (file.getName().equals("org")) {
                break;
            }
            if (file.isDirectory()) {
                String packageName = file.getPath();
                filePaths.add(packageName);
                getAllPaths(file.getPath(), filePaths);
            }
        }
        return filePaths;
    }

    public static void main(String[] args) throws ClassNotFoundException {
        Class<?> allPackages = new FilesParseUtils().getApplicationClass("src/main/java");
        System.out.println(allPackages);
    }
}
