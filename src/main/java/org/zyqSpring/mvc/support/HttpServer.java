package org.zyqSpring.mvc.support;

import org.apache.catalina.*;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.zyqSpring.mvc.servlet.DispatcherServlet;

import java.io.File;

public class HttpServer {

    private static final String DISPATCHER = "dispatcher";

    private static final String ROOT_PATH = "/";

    private static final String BASE_DOC = "src/main/java";


    public void start() throws LifecycleException {
        // 创建Tomcat容器
        Tomcat tomcatServer = new Tomcat();
        // 端口号设置
        tomcatServer.setPort(9090);
        // 读取项目路径 加载静态资源
        StandardContext ctx = (StandardContext) tomcatServer.addWebapp(ROOT_PATH, new File(BASE_DOC).getAbsolutePath());
        // 禁止重新载入
        ctx.setReloadable(false);
        // class文件读取地址
        File additionWebInfClasses = new File("target/classes");
        // 创建WebRoot
        WebResourceRoot resources = new StandardRoot(ctx);
        // tomcat内部读取Class执行
        resources.addPreResources(
                new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClasses.getAbsolutePath(), "/"));
        tomcatServer.start();
        // 异步等待请求执行
        tomcatServer.getServer().await();
    }

    public void start(String hostName, Integer port) throws LifecycleException {


       //通过server.xml的格式
        // 实例一个tomcat
        Tomcat tomcat = new Tomcat();

        // 构建server
        Server server = tomcat.getServer();

        /**
         * 在getServer的时候，就在方法内部执行了
         * Service service = new StandardService();
         * service.setName("Tomcat");
         * server.addService(service);
         */
        // 获取service
        Service service = server.findService("Tomcat");

        // 构建Connector
        Connector connector = new Connector();
        connector.setPort(port);

        // 构建Engine
        Engine engine = new StandardEngine();
        engine.setDefaultHost(hostName);

        // 构建Host
        Host host = new StandardHost();
        host.setName(hostName);

        tomcat.setBaseDir(new File(BASE_DOC).getAbsolutePath());
        // 构建Context
        Context context = (StandardContext) tomcat.addWebapp(ROOT_PATH, new File(BASE_DOC).getAbsolutePath());
        context.addLifecycleListener(new Tomcat.FixContextListener()); // 生命周期监听器

        // 然后按照server.xml，一层层把子节点添加到父节点
        host.addChild(context);
        engine.addChild(host);

        service.setContainer(engine);
        service.addConnector(connector);

        // service在getServer时就被添加到server节点了

        // tomcat是一个servlet,设置路径与映射
        // 定义一个处理器
        tomcat.addServlet(context.getPath(), DISPATCHER, new DispatcherServlet());
        //Servlet映射
        context.addServletMappingDecoded(ROOT_PATH, DISPATCHER);

        try {
            tomcat.start();
            System.out.println("tomcat服务已启动");
            tomcat.getServer().await();     // 接受请求
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

}
