package com.gupaoedu.mvcframework.v2.servlet;

import com.gupaoedu.mvcframework.v2.servlet.utilrealize.FactoryBeanRealize;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @program: mavenTomcat
 * @description: 第二版本，使用工厂模式实现的 Spring; 缺少参数解析，且IoC实现逻辑不好，对象与方法是分开检索的
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-29 16:51
 **/
@Slf4j
public class GxDispatcherServlet extends HttpServlet {

    FactoryBeanRealize factoryBeanRealize = new FactoryBeanRealize();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        // 加载配置文件
        factoryBeanRealize.doLoadConfig(servletConfig.getInitParameter("contextConfigLocation"));
        // 扫描相关类文件，加载其实现
        factoryBeanRealize.doScanner(factoryBeanRealize.getContextConfig().getProperty("scanPackage"));
        // 初始化类对象
        factoryBeanRealize.doInstance();
        // 进行依赖注入
        factoryBeanRealize.doAutowired();
        // 初始化 HandlerMapping();
        factoryBeanRealize.initHandlerMapping();
        log.info("第二代容器初始化完成");
    }

    @Override
    public void doGet(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.doPost(servletRequest, servletResponse);
    }

    @Override
    public void doPost(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        this.doDisPatch(servletRequest, servletResponse);
    }

    private void doDisPatch(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

    }


}
