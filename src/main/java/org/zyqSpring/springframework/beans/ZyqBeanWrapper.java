package org.zyqSpring.springframework.beans;

/**
 * 当BeanDefinition的Bean配置信息被读取
 * 并实例化成一个实例后，这个实例封装在BeanWrapper中
 */
public class ZyqBeanWrapper {

    /*Bean的实例化对象*/
    private Object wrappedObject;

    public ZyqBeanWrapper(Object wrappedObject) {
        this.wrappedObject = wrappedObject;
    }

    public Object getWrappedInstance() {
        return this.wrappedObject;
    }

    public Class<?> getWrappedClass() {
        return getWrappedInstance().getClass();
    }
}
