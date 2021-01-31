package com.gupaoedu.mvcframework.v2.servlet.utilrealize.handler;

import com.gupaoedu.mvcframework.annotation.GPRequestParam;
import com.gupaoedu.util.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @program: mavenTomcat
 * @description: IoC 存储方法的引用实例对象
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-30 18:11
 **/
// 为什么都是 Protected 这不离谱嘛
@Data
public class MethodHandler {
    protected Object controller; // 保存方法对应的实例
    protected Method method; // 反射存储的方法的引用
    protected Pattern pattern; // 正则表达式存储的匹配规则
    protected Map<String, Integer> paramIndexMapping; // 存储方法的入参类型、入参顺序

    public MethodHandler(Pattern pattern, Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
        paramIndexMapping = new HashMap<>();
        // 通过反射 解析方法的入参，并将其结构存储到 Mapping  中
        putParamIndexMapping(method);
    }

    private void putParamIndexMapping(Method method) {
        // 提取方法中添加了注解的参数，用来对参数进行填充
        Annotation[][] annotations = method.getParameterAnnotations();
        for(int i=0;i<annotations.length;i++) {
            for(Annotation a:annotations[i]) {
                // 为什么不判断是不是依赖于 注解，而是判断继承关系呢 ？？
                //TODO 为什么 Annotation 是二维数组 ？？？
                if(a instanceof GPRequestParam) {
                    String paramName = ((GPRequestParam) a).value();
                    if(!StringUtils.isEmpty(paramName)) {
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }
        // 提取方法中 Request 与 Response 参数 位置
        Class<?>[] paramsTypes = method.getParameterTypes();
        for(int i=0;i<paramsTypes.length;i++) {
            Class<?> type = paramsTypes[i];
            if(type == HttpServletRequest.class || type == HttpServletResponse.class) {
                paramIndexMapping.put(type.getName(), i);
            }
        }
    }
}
