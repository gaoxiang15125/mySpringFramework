package testservlet;

import jakarta.servlet.*;

import java.io.IOException;

/**
 * @program: mavenTomcat
 * @description: 最早接触servlet是16年，现在是21年，五年了我却一点长进都没有
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-28 15:37
 **/
public class Demo01 implements Servlet {

    private final String myName = Demo01.class.getName();

    public Demo01() {
        System.out.println(myName + "构造方法。");
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        System.out.println("创建实例化对象:" + myName);
    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        System.out.println(" 服务方法");
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {
        System.out.println(myName + " 销毁方法 destroy 方法");
    }
}
