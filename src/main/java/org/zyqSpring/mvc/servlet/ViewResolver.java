package org.zyqSpring.mvc.servlet;

import java.io.File;
import java.util.Locale;

/**
 * Created by Enzo Cotter on 2021/5/27.
 */
public class ViewResolver {

    private final String DEFAULT_TEMPLATE_SUFFIX = ".html";

    /**模板根目录*/
    private File templateRootDir;

    public ViewResolver(String templateRoot) {
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        templateRootDir = new File(templateRootPath);
    }

    public View resolveViewName(String viewName, Locale locale) throws Exception{
        if (null == viewName || "".equals(viewName.trim())){
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFIX) ? viewName : (viewName + DEFAULT_TEMPLATE_SUFFIX);
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new View(templateFile);
    }
}
