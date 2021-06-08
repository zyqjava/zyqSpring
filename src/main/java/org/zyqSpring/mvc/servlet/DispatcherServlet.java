package org.zyqSpring.mvc.servlet;

import org.zyqSpring.springframework.annotation.ZyqController;
import org.zyqSpring.springframework.annotation.ZyqRequestMapping;
import org.zyqSpring.springframework.context.support.ZyqAnnotationApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class DispatcherServlet extends HttpServlet {

    /**配置文件地址，从web.xml中获取*/
    private static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

    private ZyqAnnotationApplicationContext applicationContext;

    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new HashMap<>();

    //视图解析器
    private List<ViewResolver> viewResolvers = new ArrayList<>();


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Exception,Details:\r\n"
                    + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]", "")
                    .replaceAll(",\\s", "\r\n"));
            e.printStackTrace();
        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        //1、通过从request中拿到URL，去匹配一个HandlerMapping
        HandlerMapping handler = getHandler(req);
        if (handler == null) {
            //没有找到handler返回404
            processDispatchResult(req, resp, new ModelAndView("404"));
            return;
        }
        //2、准备调用前的参数
        HandlerAdapter handlerAdapter = getHandlerAdapter(handler);

        //3、真正的调用controller的方法
        ModelAndView modelAndView = handlerAdapter.handle(req, resp, handler);

        //4、渲染页面输出
        processDispatchResult(req, resp, modelAndView);
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) {
            return null;
        }

        HandlerAdapter handlerAdapter = this.handlerAdapters.get(handler);
        return handlerAdapter;
    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ModelAndView modelAndView) throws Exception{
        if (null == modelAndView) {
            return;
        }

        if (this.viewResolvers.isEmpty()) {
            return;
        }

        for (ViewResolver viewResolver : this.viewResolvers) {
            //根据模板名拿到View
            View view = viewResolver.resolveViewName(modelAndView.getViewName(), null);
            //开始渲染
            view.render(modelAndView.getModel(), req, resp);
            return;
        }
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");

        for (HandlerMapping handler : this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            //如果没有匹配上继续下一个匹配
            if (!matcher.matches()) {
                continue;
            }
            return handler;
        }

        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //1、初始化spring容器
        if (config.getServletContext().getAttribute(ZyqAnnotationApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) instanceof ZyqAnnotationApplicationContext) {
            applicationContext = (ZyqAnnotationApplicationContext)
                    config.getServletContext().getAttribute(ZyqAnnotationApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
        } else {
            System.out.println("获取spring容器失败!!!");
            throw new ServletException("获取spring容器失败!!!");
        }
        //2、初始化spring mvc九大组件
        initStrategies(applicationContext);
    }

    private void initStrategies(ZyqAnnotationApplicationContext applicationContext) {
        //多文件上传的组件

        //初始化本地语言环境

        //初始化模板处理器

        //handlerMapping，必须实现
        initHandlerMappings(applicationContext);

        //初始化参数适配器，必须实现
        initHandlerAdapters(applicationContext);

        //初始化异常拦截器

        //初始化视图预处理器

        //初始化视图转换器，必须实现
        initViewResolvers(applicationContext);

        //参数缓存器

    }

    private void initViewResolvers(ZyqAnnotationApplicationContext applicationContext) {
        //配置文件中拿到模板的存放目录
        String templateRoot = applicationContext.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        String[] templates = templateRootDir.list();
        for (int i = 0; i < templates.length; i ++) {
            this.viewResolvers.add(new ViewResolver(templateRoot));
        }
    }

    /**
     * 遍历容器中的Bean，找到被@Controller注解的
     * 遍历Controller的所有方法，找到被@RequestMapping注解的
     * 获取URL表达式，编译成正则
     * 将HandlerMapping添加到集合中保存起来
     * @param applicationContext
     */
    private void initHandlerMappings(ZyqAnnotationApplicationContext applicationContext) {
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        try {
            for (String beanName : beanNames) {
                Object bean = applicationContext.getBean(beanName);
                Class<?> clazz = bean.getClass();
                if (!clazz.isAnnotationPresent(ZyqController.class)) {
                    continue;
                }

                String baseUrl = "";
                //获取Controller的url配置
                if (clazz.isAnnotationPresent(ZyqRequestMapping.class)) {
                    ZyqRequestMapping requestMapping = clazz.getAnnotation(ZyqRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //获取Method的url配置
                Method[] methods = clazz.getMethods();
                for (Method method : methods) {
                    //没有加@RequestMapping注解直接忽略
                    if (!method.isAnnotationPresent(ZyqRequestMapping.class)) {
                        continue;
                    }

                    //映射URL
                    ZyqRequestMapping requestMapping = method.getAnnotation(ZyqRequestMapping.class);
                    String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                    Pattern pattern = Pattern.compile(regex);

                    this.handlerMappings.add(new HandlerMapping(bean, method, pattern));
                    System.out.println("Mapped " + regex + "," + method);
                }
            }
        } catch (Exception e) {
            System.out.println("映射URL失败");
        }
    }

    private void initHandlerAdapters(ZyqAnnotationApplicationContext applicationContext) {
        //一个HandlerMapping对应一个HandlerAdapter
        for (HandlerMapping handlerMapping : this.handlerMappings) {
            this.handlerAdapters.put(handlerMapping, new HandlerAdapter());
        }
    }
}
