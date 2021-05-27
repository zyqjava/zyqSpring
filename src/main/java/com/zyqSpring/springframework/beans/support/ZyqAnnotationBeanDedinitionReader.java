package com.zyqSpring.springframework.beans.support;

import com.zyqSpring.boot.annotation.ZyqComponentScan;
import com.zyqSpring.springframework.annotation.ZyqComponent;
import com.zyqSpring.springframework.beans.config.ZyqBeanDefinition;

import java.io.File;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class ZyqAnnotationBeanDedinitionReader {

    //配置文件
    private Properties config = new Properties();

    /**保存了所有Bean的className*/
    private List<String> registerBeanClasses = new ArrayList<>();

    /**
     * 完成BeanDefinitionReader中的构造方法，流程分为三步走：
     * 将我们传入的配置文件路径解析为文件流
     * 将文件流保存为Properties，方便我们通过Key-Value的形式来读取配置文件信息
     * 根据配置文件中配置好的扫描路径，开始扫描该路径下的所有class文件并保存到集合中
     * @param annotatedClasses
     */
    public ZyqAnnotationBeanDedinitionReader(Class<?>... annotatedClasses) {
        try {
            for (Class<?> annotatedClass : annotatedClasses) {
                //扫描,扫描资源文件.class，并保存到集合中
                if (!annotatedClass.isAnnotationPresent(ZyqComponentScan.class)) {
                    continue;
                }
                ZyqComponentScan annotation = annotatedClass.getAnnotation(ZyqComponentScan.class);
                String[] packages = annotation.value();
                if (packages.length > 0) {
                    for (String basePackage : packages) {
                        doScanner(basePackage);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("读取文件失败" + e.getMessage());
        }

    }

    /**
     * doScanner()是递归方法，当它发现当前扫描的文件是目录时要发生递归，
     * 直到找到一个class文件，然后把它的全类名添加到集合中
     * @param scanPackage
     */
    public void doScanner(String scanPackage) {
        URL url = this.getClass().getClassLoader().getResource(scanPackage.replaceAll("\\.", "/"));
        File files = new File(url.getFile());
        for (File file : files.listFiles()) {
            if (file.isDirectory()) {
                //如果是目录则递归调用，直到找到class
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    //如果不是.class文件，则忽略
                    continue;
                }
                String className = scanPackage + "." + file.getName().replace(".class", "");
                //className保存到集合
                registerBeanClasses.add(className);
            }
        }
    }

    /**
     * 把配置文件中扫描到的所有的配置信息转换为BeanDefinition对象
     * 逻辑是：扫描class集合，如果是被@Component注解的class则需要封装成BeanDefinition，表示着它将来可以被IOC进行管理。
     * BeanDefinition主要保存两个参数，factoryBeanName和beanClassName，
     * 一个是保存实现类的类名（首字母小写）或其接口全类名，
     * 另一个是保存实现类的全类名，如下图所示。通过保存这两个参数我们可以实现用类名或接口类型来依赖注入。
     */
    public List<ZyqBeanDefinition> loadBeanDefinitions() {
        List<ZyqBeanDefinition> beanDefinitionList = new ArrayList<>();
        try {
            for (String className : registerBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                //接口无法被实例化、所以无需封装
                if (beanClass.isInterface()) {
                    continue;
                }

                Annotation[] annotations = beanClass.getAnnotations();
                if (annotations.length == 0) {
                    continue;
                }

                for (Annotation annotation : annotations) {
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    //只考虑被@Component注解的class
                    if (annotationType.isAnnotationPresent(ZyqComponent.class)) {
                        //beanName有三种情况:
                        //1、默认是类名首字母小写
                        //2、自定义名字（这里暂不考虑）
                        //3、接口注入
                        beanDefinitionList.add(doCreateZyqBeanDefinition(beanClass.getName(), toLowerFirstCase(beanClass.getSimpleName())));

                        Class<?>[] interfaces = beanClass.getInterfaces();
                        for (Class<?> anInterface : interfaces) {
                            //接口和实现类之间的关系也需要封装
                            beanDefinitionList.add(doCreateZyqBeanDefinition(beanClass.getName(), toLowerFirstCase(anInterface.getSimpleName())));
                        }
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("封装beanDefinition出错");
        }
        return beanDefinitionList;
    }

    private ZyqBeanDefinition doCreateZyqBeanDefinition(String beanClassName, String factoryBeanName) {
        ZyqBeanDefinition beanDefinition = new ZyqBeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    /**
     * 将单词首字母变为小写
     */
    private String toLowerFirstCase(String simpleName) {
        char [] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return config;
    }
}
