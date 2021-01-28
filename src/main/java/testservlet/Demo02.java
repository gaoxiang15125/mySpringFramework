package testservlet;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

/**
 * @program: mavenTomcat
 * @description: 使用适配器创建 Servlet
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-28 19:28
 **/
public class Demo02 extends GenericServlet {

    private final String myName = Demo02.class.getName();

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        System.out.println(myName + " demo02 Service 方法被调用");
    }
}
