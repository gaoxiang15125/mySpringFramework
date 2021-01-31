package com.gupaoedu.mvcframework.v2.servlet.utilrealize;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @program: mavenTomcat
 * @description:
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-31 20:35
 **/
public class FactoryBeanRealizeTest {

    @Test
    public void convert() {
        FactoryBeanRealize factoryBeanRealize = new FactoryBeanRealize();
        // 测试 fastJson 能不能支持日常的格式转换工作
        Object object = null;
//        object = factoryBeanRealize.convert(String.class, "抓紧时间");
//        System.out.println(object);
        TestClassGx testClassGx = new TestClassGx("天之道,轮回也", 250);
        String jsonBuffer = JSON.toJSONString(testClassGx);
        object = factoryBeanRealize.convert(testClassGx.getClass(), jsonBuffer);
        System.out.println(object);
        System.out.println(JSON.toJSONString(object));
    }

}

@Data
@NoArgsConstructor
@AllArgsConstructor
class TestClassGx {
    private String okba;
    private Integer ouNo;
}