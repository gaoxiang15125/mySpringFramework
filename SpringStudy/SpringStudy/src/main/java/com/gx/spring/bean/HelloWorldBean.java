package com.gx.spring.bean;

import lombok.Data;

/**
 * @program: mavenTomcat
 * @description: Spring 入门程序，感受Bean 的加载过程
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-02-01 18:24
 **/
@Data
public class HelloWorldBean {
    private String name;

    public HelloWorldBean() {
        System.out.println("怎么了，这样写有什么特殊的嘛");
    }

    public HelloWorldBean(String str) {
        System.out.println("调用了单参数构造方法，入参为: " + str);
    }

    public void hello() {
        System.out.println("你好，" + name);
    }
}
