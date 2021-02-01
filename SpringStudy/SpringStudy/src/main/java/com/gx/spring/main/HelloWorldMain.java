package com.gx.spring.main;

import com.gx.spring.bean.HelloWorldBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

/**
 * @program: mavenTomcat
 * @description: 为什么 Servlet 没有 Main 函数，到了 Spring 反而出现了 Main 函数呢？
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-02-01 18:53
 **/
public class HelloWorldMain {
    static final String CONFIG_PATH = "helloWorld.xml";

    public static void main(String[] args) {
        // 为什么要经过 Main 函数，Spring 与 Servlet 有什么区别要经过 Main 这一层 ？？
        File file = new File(CONFIG_PATH);
        System.out.println(file.getPath());
        System.out.println(file.getAbsolutePath());
        // 创建 Spring 的 IoC 容器对象
        ApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_PATH);
        // 从 IoC 容器获取 Bean 实例，填写 Bean ID 获取，这个名字应该是可以在 xml 文件里配置的吧
        HelloWorldBean helloWorld = context.getBean(HelloWorldBean.class);
        // 使用 Class 声明可以获得实例对象嘛？ 使用 name 声明可以获得实例对象嘛
        helloWorld.hello();
        helloWorld.setName("偶吼吼");
        helloWorld = (HelloWorldBean) context.getBean("helloWorld");
        helloWorld.hello();
    }
}
