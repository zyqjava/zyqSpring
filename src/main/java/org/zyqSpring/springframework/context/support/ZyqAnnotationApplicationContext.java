package org.zyqSpring.springframework.context.support;

import org.zyqSpring.boot.annotation.ZyqComponentScan;
import org.zyqSpring.boot.annotation.ZyqSpringApplication;
import org.zyqSpring.springframework.annotation.ZyqAutowired;
import org.zyqSpring.springframework.aop.config.AopConfig;
import org.zyqSpring.springframework.aop.framework.AopProxy;
import org.zyqSpring.springframework.aop.framework.ProxyFactory;
import org.zyqSpring.springframework.aop.support.AdvisedSupport;
import org.zyqSpring.springframework.beans.InitializingBean;
import org.zyqSpring.springframework.beans.ZyqBeanWrapper;
import org.zyqSpring.springframework.beans.config.BeanPostProcessor;
import org.zyqSpring.springframework.beans.config.ZyqBeanDefinition;
import org.zyqSpring.springframework.beans.support.ZyqAnnotationBeanDefinitionReader;
import org.zyqSpring.springframework.context.ApplicationContext;
import org.zyqSpring.springframework.context.ZyqBeanNameAware;
import org.zyqSpring.springframework.core.factory.ZyqDefaultListableBeanFactory;
import org.zyqSpring.springframework.utils.PropertiesUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class ZyqAnnotationApplicationContext extends ZyqDefaultListableBeanFactory implements ApplicationContext {

    //配置文件的路径
    private ZyqAnnotationBeanDefinitionReader reader;

    private List<String> scanPackages;

    /**保存了真正实例化的对象*/
    private Map<String, ZyqBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();
    //单例的IOC容器缓存
    private Map<String, Object> singletonBeanObjectCache = new ConcurrentHashMap<>();

    private List<BeanPostProcessor> beanPostProcessorsList = new ArrayList<>();

    public static final String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = "applicationContext";

    public ZyqAnnotationApplicationContext() {
    }

    public ZyqAnnotationApplicationContext(Class<?>... annotatedClasses) {
        try {
            //注册配置类
            register(annotatedClasses);
            //注入IOC容器
            refresh();
        } catch (Exception e) {
            System.out.println("容器启动失败");
            e.printStackTrace();
        }
    }

    private void register(Class<?>[] annotatedClasses) {
        List<String> packagesTemp = new ArrayList<>();
        //处理配置类
        List<String> packageNames = null;
        for (Class<?> annotatedClass : annotatedClasses) {
            packageNames = new ArrayList<>();

            if (annotatedClass.isAnnotationPresent(ZyqSpringApplication.class)) {
                packageNames.add(annotatedClass.getPackage().getName());
            }

            //是否有配置扫描包
            if (annotatedClass.isAnnotationPresent(ZyqComponentScan.class)) {
                ZyqComponentScan annotation = annotatedClass.getAnnotation(ZyqComponentScan.class);
                String[] packages = annotation.value();
                if (packages.length > 0) {
                    for (String aPackage : packages) {
                        packagesTemp.add(aPackage);
                    }
                } else {
                    packageNames.add(annotatedClass.getPackage().getName());
                }
            }
        }
        if (packageNames == null) {
            System.out.println("加载项目路径失败！！");
            throw new RuntimeException("加载项目路径失败！！");
        }

        reader = new ZyqAnnotationBeanDefinitionReader(packageNames.toArray(new String[packageNames.size()]));
        List<String> packages = reader.loadConfigurationBeanDefinitions();

        for (Class<?> annotatedClass : annotatedClasses) {
            java.net.URL uri = annotatedClass.getClass().getResource("/");
            File files = new File(uri.getPath());
            doReaderFilesProperties(files.listFiles(), reader);
        }

        for (String temp : packagesTemp) {
            if (scanPackages.contains(temp)) {
                continue;
            } else {
                scanPackages.add(temp);
            }
        }

        for (String temp : packages) {
            if (scanPackages.contains(temp)) {
                continue;
            } else {
                scanPackages.add(temp);
            }
        }

        if (packages.size() == 0) {
            scanPackages = packageNames;
        }
    }

    private void doReaderFilesProperties(File[] files, ZyqAnnotationBeanDefinitionReader reader) {
        for (File file : files) {
            if (!file.isDirectory()) {
                if(file.getName().endsWith(".properties")) {
                    PropertiesUtils.onLoadProperties(file.getName(), reader.getConfig(), reader.getClass().getClassLoader());
                }
            } else {
                doReaderFilesProperties(file.listFiles(), reader);
            }
        }
    }


    public void refresh() throws Exception {
        //step1:定位，定位配置文件
        scanCustomerPack();
        //step2:加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<ZyqBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //注册beanProcessors
        loadBeanProcessors(beanDefinitions);
        //step3:注册，把配置信息放到容器里面（ioc容器）
        doRegisterBeanDefinition(beanDefinitions);
        //把不是延时加载的类，提前初始化
        doAutowired();
    }

    private void scanCustomerPack() {
        if (scanPackages != null) {
            for (String scanPackage : scanPackages) {
                reader.doScanner(scanPackage);
            }
        }
    }

    private void loadBeanProcessors(List<ZyqBeanDefinition> beanDefinitions) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (ZyqBeanDefinition beanDefinition : beanDefinitions) {
            Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
            //接口无法被实例化、所以无需封装
            if (beanClass.isInterface()) {
                continue;
            }

            if (BeanPostProcessor.class.isAssignableFrom(beanClass)) {
                BeanPostProcessor beanPostProcessor = (BeanPostProcessor) beanClass.getDeclaredConstructor().newInstance();
                beanPostProcessorsList.add(beanPostProcessor);
            }
        }
    }

    private void doAutowired() {
        for (Map.Entry<String, ZyqBeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    if (beanDefinitionEntry.getValue().getScope().equals("singleton")) {
                        getBean(beanName);
                    } else {
                        //创建bean对象
                        Object bean = createBean(beanDefinitionEntry.getValue());
                        singletonBeanObjectCache.put(beanName, bean);
                    }
                } catch (Exception e) {
                    System.out.println("获取bean失败!!!");
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<ZyqBeanDefinition> beanDefinitions) throws Exception{
        //将BeanDefinition保存为以factoryBeanName为Key的Map
        for (ZyqBeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("该" + beanDefinition.getFactoryBeanName() + "已存在");
            }
            beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    private Object createBean(ZyqBeanDefinition beanDefinition) throws Exception {
        Class aClass = beanDefinition.getClass();
        Object instance = aClass.getDeclaredConstructor().newInstance();

        for (Field declaredField : aClass.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(ZyqAutowired.class)) {
                Object bean = getBean(declaredField.getName());
                declaredField.setAccessible(true);
                declaredField.set(instance, bean);
            }
        }
        return instance;
    }

    /**
     * 如果已经实例化了，则直接获取实例化后的对象返回即可。如果没有实例化则走后面的逻辑
     * 拿到该Bean的BeanDefinition信息，通过反射实例化
     * 将实例化后的对象封装到BeanWrapper中
     * 将封装好的BeanWrapper保存到IOC容器（实际就是一个Map）中
     * 依赖注入实例化的Bean
     * 返回最终实例
     *
     * //装饰器模式：
     *     //1、保留原来的OOP关系
     *     //2、我需要对它进行扩展，增强（为了以后AOP打基础）
     * @param beanName
     * @return
     * @throws Exception
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        //如果是单例，那么在上一次调用getBean获取该bean时，已经初始化过了
        //拿到不为空的实例直接返回即可
        Object instance  = getSingleton(beanName);
        if (instance != null) {
            return instance;
        }

        ZyqBeanDefinition zyqBeanDefinition = this.beanDefinitionMap.get(beanName);

        for (BeanPostProcessor beanPostProcessor : beanPostProcessorsList) {
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
        }


        //1.调用反射初始化bean
        instance = instantiateBean(beanName, zyqBeanDefinition);


        if (instance != null) {
            if (instance instanceof ZyqBeanNameAware) {
                ((ZyqBeanNameAware) instance).setZyqBeanName(beanName);
            }
        }

        if (instance != null) {
            if (instance instanceof InitializingBean) {
                ((InitializingBean) instance).afterPropertiesSet();
            }
        }

        //2.封装对象到BeanWrapper
        ZyqBeanWrapper zyqBeanWrapper = new ZyqBeanWrapper(instance);

        //3.把BeanWrapper保存到IOC容器中去
        //注册一个类名（首字母小写，如helloService）
        this.factoryBeanInstanceCache.put(beanName, zyqBeanWrapper);
        //注册一个全类名（org.zyqSpring.helloService）
        this.factoryBeanInstanceCache.put(zyqBeanDefinition.getBeanClassName(), zyqBeanWrapper);

        for (BeanPostProcessor beanPostProcessor : beanPostProcessorsList) {
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
        }
        //4.注入
        populateBean(beanName, new ZyqBeanDefinition(), zyqBeanWrapper);

        //5.bean

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }

    /**
     * 上一步中Bean只是实例化了，但是Bean中被@Autowired注解的变量还没有注入，
     * 如果这个时候去使用就会报空指针异常。下面是注入的逻辑：
     * 拿到Bean中的所有成员变量开始遍历
     * 过滤掉没有被@Autowired注解标注的变量
     * 拿到被注解变量的类名，并从IOC容器中找到该类的实例（上一步已经初始化放在容器了）
     * 将变量的实例通过反射赋值到变量中
     * // 可能会涉及到循环依赖？  这里不做处理
     * @param beanName
     * @param zyqBeanDefinition
     * @param zyqBeanWrapper
     */
    private void populateBean(String beanName, ZyqBeanDefinition zyqBeanDefinition, ZyqBeanWrapper zyqBeanWrapper) {
        Class<?> clazz = zyqBeanWrapper.getWrappedClass();

        //获取所有的成员变量
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            //如果没有被@ZyqAutowired注解的成员变量则直接跳过
            if (!field.isAnnotationPresent(ZyqAutowired.class)) {
                continue;
            }

            ZyqAutowired autowired = field.getAnnotation(ZyqAutowired.class);
            //拿到需要注入的类名
            String autowiredBeanName  = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }

            //强制访问成员变量
            field.setAccessible(true);

            try {
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
                //将容器中的实例注入到成员变量中
                field.set(zyqBeanWrapper.getWrappedInstance(), this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (Exception e) {
                System.out.println("注入失败, 失败的bean" + beanName);
            }
        }
    }

    private Object instantiateBean(String beanName, ZyqBeanDefinition zyqBeanDefinition) {
        //1.拿到需要实例化的类名
        String beanClassName = zyqBeanDefinition.getBeanClassName();

        //2.反射实例化，得到一个对象
        Object instance = null;
        try {
            if (this.singletonBeanObjectCache.containsKey(beanClassName)) {
                instance = singletonBeanObjectCache.get(beanClassName);
            } else {
                Class<?> clazz = Class.forName(beanClassName);
                instance = clazz.newInstance();
                this.singletonBeanObjectCache.put(beanClassName, instance);
                //############填充如下代码###############
                //获取AOP配置
                AdvisedSupport aopConfig = getAopConfig();
                aopConfig.setTargetClass(clazz);
                aopConfig.setTarget(instance);
                //符合PointCut的规则的话，将创建代理对象
                if(aopConfig.pointCutMatch()) {
                    //创建代理
                    instance = createProxy(aopConfig).getProxy();
                }
                //#############填充完毕##############
            }
        } catch (Exception e) {
            System.out.println("实例化对象失败 类名--" + beanClassName);
        }
        return instance;
    }

    private AopProxy createProxy(AdvisedSupport aopConfig) {
        Class<?> targetClass = aopConfig.getTargetClass();
        //如果是接口，默认使用JDK
        return ProxyFactory.getProxy(aopConfig, targetClass);
    }

    private Object getSingleton(String beanName) {
        ZyqBeanWrapper beanWrapper = factoryBeanInstanceCache.get(beanName);
        return beanWrapper == null ? null : beanWrapper.getWrappedInstance();
    }


    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }


    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }


    @Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        return (T) getBean(requiredType.getName());
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }

    private AdvisedSupport getAopConfig() {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
    }
}
