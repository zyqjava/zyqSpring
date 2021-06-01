package com.zyqSpring.springframework.context.support;

import com.zyqSpring.springframework.annotation.ZyqAutowired;
import com.zyqSpring.springframework.aop.config.AopConfig;
import com.zyqSpring.springframework.aop.framework.AopProxy;
import com.zyqSpring.springframework.aop.framework.CglibAopProxy;
import com.zyqSpring.springframework.aop.framework.JdkDynamicAopProxy;
import com.zyqSpring.springframework.aop.support.AdvisedSupport;
import com.zyqSpring.springframework.beans.ZyqBeanWrapper;
import com.zyqSpring.springframework.beans.config.ZyqBeanDefinition;
import com.zyqSpring.springframework.beans.config.ZyqBeanPostProcessor;
import com.zyqSpring.springframework.beans.support.ZyqBeanDefinitionReader;
import com.zyqSpring.springframework.context.ApplicationContext;
import com.zyqSpring.springframework.core.factory.ZyqDefaultListableBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public class ZyqApplicationContext extends ZyqDefaultListableBeanFactory implements ApplicationContext {

    //配置文件的路径
    private String[] configLocations;
    private ZyqBeanDefinitionReader reader;


    /**保存了真正实例化的对象*/
    private Map<String, ZyqBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();
    //单例的IOC容器缓存
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    public ZyqApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            System.out.println("XML方式启动容器失败");
        }
    }


    public void refresh() throws Exception {
        //step1:定位，定位配置文件
        reader = new ZyqBeanDefinitionReader(this.configLocations);
        //step2:加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<ZyqBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //step3:注册，把配置信息放到容器里面（ioc容器）
        doRegisterBeanDefinition(beanDefinitions);
        //把不是延时加载的类，提前初始化
        doAutowired();
    }

    private void doAutowired() {
        for (Map.Entry<String, ZyqBeanDefinition> beanDefinitionEntry : beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
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

        ZyqBeanPostProcessor postProcessor = new ZyqBeanPostProcessor();
        postProcessor.postProcessBeforeInitialization(instance, beanName);

        //1.调用反射初始化bean
        instance = instantiateBean(beanName, zyqBeanDefinition);

        //2.封装对象到BeanWrapper
        ZyqBeanWrapper zyqBeanWrapper = new ZyqBeanWrapper(instance);

        //3.把BeanWrapper保存到IOC容器中去
        //注册一个类名（首字母小写，如helloService）
        this.factoryBeanInstanceCache.put(beanName, zyqBeanWrapper);
        //注册一个全类名（com.zyqSpring.helloService）
        this.factoryBeanInstanceCache.put(zyqBeanDefinition.getBeanClassName(), zyqBeanWrapper);

        postProcessor.postProcessAfterInitialization(instance, beanName);
        //4.注入
        populateBean(beanName, new ZyqBeanDefinition(), zyqBeanWrapper);

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
            if (this.factoryBeanObjectCache.containsKey(beanClassName)) {
                instance = factoryBeanObjectCache.get(beanClassName);
            } else {
                Class<?> clazz = Class.forName(beanClassName);
                instance = clazz.newInstance();
                this.factoryBeanObjectCache.put(beanClassName, instance);
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
        if (targetClass.getInterfaces().length > 0) {
            return new JdkDynamicAopProxy(aopConfig);
        }
        return new CglibAopProxy(aopConfig);
    }

    private Object getSingleton(String beanName) {
        ZyqBeanWrapper beanWrapper = factoryBeanInstanceCache.get(beanName);
        return beanWrapper == null ? null : beanWrapper.getWrappedInstance();
    }


    @Override
    public <T> T getBean(Class<T> requiredType) throws Exception {
        return (T) getBean(requiredType.getName());
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }


    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
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
