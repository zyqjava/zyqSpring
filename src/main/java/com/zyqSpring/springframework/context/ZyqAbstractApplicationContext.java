package com.zyqSpring.springframework.context;

/**
 * Created by Enzo Cotter on 2021/5/21.
 */
public abstract class ZyqAbstractApplicationContext {
    /**
     * 受保护得，只提供给子类进行重写
     */
    public void refresh() throws Exception{
    }
}
