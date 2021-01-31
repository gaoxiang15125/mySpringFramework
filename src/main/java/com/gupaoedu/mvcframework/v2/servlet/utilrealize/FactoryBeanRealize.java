package com.gupaoedu.mvcframework.v2.servlet.utilrealize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONWriter;
import com.gupaoedu.mvcframework.annotation.GPAutowired;
import com.gupaoedu.mvcframework.annotation.GPController;
import com.gupaoedu.mvcframework.annotation.GPRequestMapping;
import com.gupaoedu.mvcframework.annotation.GPService;
import com.gupaoedu.mvcframework.v2.servlet.utilrealize.handler.MethodHandler;
import com.gupaoedu.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: mavenTomcat
 * @description: 自己编写的 Spring Bean 装载实现类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-29 16:58
 **/
@Data
@Slf4j
public class FactoryBeanRealize {
    // 不想把初始化控件的方法放到 Servlet 内，单独拿出来吧，至少好看点
    /**
     *  整个流程包括:
     *      加载配置文件 根据 serviceConfig 指定地路径解析 配置文件
     *      根据配置文件 扫描相关类 扫描 Java 文件，将 (类名，null）放入 IoC 容器中 ！！！Spring 使用 List 实现 IoC 容器，为什么不是 Map ？？
     *      初始化扫描类，将其放入 IoC 容器中
     *      完成依赖注入
     *      初始化 HandlerMapping
     */
    // 配置文件存储对象
    private Properties contextConfig = new Properties();
    //TODO 保存扫描的所有的类名 保存类名的目的是什么 ？？ 还真没注意到
    // 单纯是觉得使用 Map-null 保存不好吧，因此使用 List 保存，更符合情景
    private List<String> classNames = new ArrayList<>();
    // IoC 容器，当前版本不考虑 ConcurrentHashMap 主要关注 设计思想和原理
    private Map<String,Object> ioc = new HashMap<>();
    // 保存 url 与 method 的对应关系 将来扩充为 utl - 对象&方法
    //TODO 将 Map 修改为 List 会不会存在效率问题
    private List<MethodHandler> handlerMapping = new ArrayList<>();

    /**
     * 加载配置信息
     * @param contextConfigLocation
     */
    public void doLoadConfig(String contextConfigLocation) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            contextConfig.load(inputStream);
        } catch (Exception e) {
            log.error("加载配置过程中出现错误", e);
        } finally {
            if(null != inputStream) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    log.error("关闭输入流过程出现错误 ", e);
                }
            }
        }
    }

    /**
     * 进行类扫描
     * @param scanPackage
     */
    public void doScanner(String scanPackage) {
        // 转换文件路径，将 . 替换为 /
        // 本质上是 解析类文件，创建 类名称-null 枚举表
        URL url = this.getClass().getClassLoader().getResource(SPILT_TAG + scanPackage.replaceAll(PACKAGE_TAG, SPILT_TAG));
        File classPath = new File(url.getFile());
        for(File file:classPath.listFiles()) {
            if(file.isDirectory()) {
                // 递归解析 文件 TODO 为什么不直接将 . 转化为 / 反而进行额外的替换
                doScanner(scanPackage + "." + file.getName());
            } else {
                if(!file.getName().endsWith(CLASS_FILE_END)) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(CLASS_FILE_END, ""));
                // 获取 类名称, 并存储到 List 中
                classNames.add(className);
            }
        }
    }

    /**
     * 对 类文件 进行实例化
     */
    public void doInstance() {
        if(classNames.isEmpty()) {
            return;
        }
        try {
            // 根据注解创建类的实例对象，并存储到 IoC 容器中
            // 感觉这里用 class 文件而不是文件名是不是好一些，这里也说明了为什么文件名要与类名一致
            for(String className:classNames) {
                Class<?> clazz = Class.forName(className);
                // 判断其注解，对于需要初始化给 IoC 管理的类，我们进行初始化操作
                if(clazz.isAnnotationPresent(GPController.class)) {
                    Object instance = clazz.newInstance();
                    String beanName = StringUtils.toLowerFirstCase(className);
                    // 需不需要考虑自定义注解内容，获取也存储到 map 中?
                    ioc.put(beanName, instance);
                } else if(clazz.isAnnotationPresent(GPService.class)) {
                    // 对于包含 Service 注解的类，一是把其 Bean 对象放入 Ioc,二是把其所有继承的接口放入 ioc
                    //TODO 一个接口有多个实现的时候怎么办？ 默认第一个作为其实现嘛？？
                    GPService service = clazz.getAnnotation(GPService.class);
                    String beanName = service.value();
                    if(StringUtils.isEmpty(beanName)) {
                        beanName = StringUtils.toLowerFirstCase(clazz.getSimpleName());
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);
                    // 将其父类赋值为当前 Bean
                    for(Class<?> i:clazz.getInterfaces()) {
                        if(ioc.containsKey(i.getName())) {
                            // 小写的目的就是与这个时候的大写做区分嘛？？ 有点离谱
                            if(ioc.containsKey(i.getName())) {
                                throw new Exception("The bean named " + i.getName() + " is exist!");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("使用 Autowire 加载类过程中出现错误: ", e);
        }
    }

    /**
     * 进行依赖注入，对 Autowire 声明的属性赋值
     */
    public void doAutowired() {
        if(ioc.isEmpty()) {
            return;
        }
        for(Map.Entry<String, Object> entry:ioc.entrySet()) {
            // 相较与上一个版本，该版本既对 Controller 进行了注入，也对 Service 进行了注入
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for(Field field:fields) {
                if(!field.isAnnotationPresent(GPAutowired.class)) {
                    continue;
                }
                // 获取 BeanName 根据 IoC 容器内容进行注入操作
                GPAutowired autowired = field.getAnnotation(GPAutowired.class);
                String beanName = autowired.value().trim(); // 如果设置了默认bean 获取其默认bean
                if(StringUtils.isEmpty(beanName)) {
                    // 未设置默认 Bean，使用类名获取对应Bean
                    beanName = field.getType().getName();
                }
                // private 属性想要赋值，必须设置为可达
                field.setAccessible(true);
                try {
                    // 使用反射对 Autowire 属性进行赋值
                    field.set(entry.getValue(),ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    log.error("使用 Autowire 对 Controller 进行依赖注入过程出错 ", e);
                }
            }
        }
    }

    /**
     * 初始化 method 与 url 的 一对一关系
     */
    public void initHandlerMapping() {
        if(ioc.isEmpty()) {
            return;
        }
        // 不为空，按照 Controller -> Method 的顺序进行遍历，将解析结果存入 Map 中
        // 遍历我们构造的 IoC 容器，对符合条件的 对象进行赋值
        for(Map.Entry<String, Object> entry:ioc.entrySet()) {
            Class<?> clazz = entry.getValue().getClass();
            if(!clazz.isAnnotationPresent(GPController.class)) {
                continue;
            }
            // 根据对应的层级关系，保存 Url - method 映射表
            //TODO 对于在 App 配置文件里声明的 contentPath，我们是如何处理、保存的呢？这是个问题
            String baseUrl = "";
            if(clazz.isAnnotationPresent(GPRequestMapping.class)) {
                GPRequestMapping requestMapping = clazz.getAnnotation(GPRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            // 获取所有 public 类型方法，将其解析后存储 handlerMap 中
            Method[] methods = clazz.getMethods();
            for(Method method:methods) {
                // 对于声明为对外接口的方法，将其构造为 MethodHandler 存储到 List 中
                if(!method.isAnnotationPresent(GPRequestMapping.class)) {
                    continue;
                }
                GPRequestMapping requestMapping = method.getAnnotation(GPRequestMapping.class);
                String regex = (baseUrl + requestMapping.value());
                regex = regex.replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                handlerMapping.add(new MethodHandler(pattern, entry.getValue(), method));
                log.info("添加 Url - Method 关联: " + regex + " : " + method.getName());
            }
        }
    }

    public void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        MethodHandler handler = getHandler(request);
        if(handler == null) {
            response.getWriter().write("404 url not Found!");
            response.setStatus(404);
            return;
        }
        // 获取 url 对应的方法，对方法参数进行填充
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        Object[] paramValue = new Object[paramTypes.length];
        Map<String, String[]> params = request.getParameterMap();
        // 去除 入参中的特殊字符,并将其作为入参传递给 目标 method
        for(Map.Entry<String, String[]> entry:params.entrySet()) {
            // 看起来是去除 [ ] \s 空白字符
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "")
                    // 我认为空白替换为 ， 不合理，这里还是让他空白
                    .replaceAll("\\s"," ");
            // 如果是我们需要的入参，进行类型转化后，存入结果集，否则舍去
            //TODO 这里转化失败报错没有怎么处理
            if(!handler.getParamIndexMapping().containsKey(entry.getKey())) {
                continue;
            }
            int index = handler.getParamIndexMapping().get(entry.getKey());
            paramValue[index] = convert(paramTypes[index], value);
        }
        // 填充 response 与 request
        if(handler.getParamIndexMapping().containsKey(HttpServletRequest.class.getName())) {
            int index = handler.getParamIndexMapping().get(HttpServletRequest.class.getName());
            paramValue[index] = request;
        }

        if(handler.getParamIndexMapping().containsKey(HttpServletResponse.class.getName())) {
            int index = handler.getParamIndexMapping().get(HttpServletResponse.class.getName());
            paramValue[index] = response;
        }

        Object returnValue = handler.getMethod().invoke(handler.getController(), paramValue);
        if(null == returnValue || returnValue instanceof Void) {
            return;
        }
        response.getWriter().write(JSON.toJSONString(returnValue));
    }

    //为什么不使用枚举，反而使用 List 实现呢？
    private MethodHandler getHandler(HttpServletRequest request) throws Exception {
        if(handlerMapping.isEmpty()) {
            return null;
        }
        String url = request.getRequestURI();
        String contextPath = request.getContextPath();
        url = url.replace(contextPath, "");
        url = url.replaceAll("/+", "/");
        // 遍历 Method List，找到 Url 指定的 method
        for(MethodHandler handler : handlerMapping) {
            // 使用正则表达式对 Url 进行匹配，应该是因为入参可能存在差别吧
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()) {
                continue;
            }
            return handler;
        }
        return null;
    }

    /**
     * 对方法的入参进行转换，因为 http 基于字符串的协议，默认参数类型为 http
     * 需要我们把 http 转换为其他类型
     * @param type
     * @param value
     * @return
     */
    public Object convert(Class<?> type, String value) {
        // 基本的数据类型需要手动解析
        // 应该可以使用 工厂类进行优化
        // 现在简单的处理下几个常用的 类型就可以了
        if(null == type) {
            return null;
        } else if(Integer.class == type) {
            return Integer.valueOf(value);
        } else if(Double.class == type) {
            return Double.valueOf(value);
        } else if(String.class == type) {
            return value;
        }
        return JSON.parseObject(value, type);
        // 按道理用 fastJson 基本就可以处理了，不过还是比较好奇，fastJson 是如何实现的 估计要反射构造了吧
    }

    static String CLASS_FILE_END = ".class";
    static String SPILT_TAG = "/";
    static String PACKAGE_TAG = "\\.";
}
