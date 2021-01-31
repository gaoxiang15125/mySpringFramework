package com.gupaoedu.mvcframework.v2.servlet.utilrealize.handler;

import com.gupaoedu.mvcframework.annotation.GPRequestParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @program: mavenTomcat
 * @description:
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-31 14:27
 **/
public class MethodHandlerTest {

    MethodHandler methodHandler;

    @Test
    public void testOne() {
        TestMember testMember = new TestMember();
        Class<?> clazz = TestMember.class;
        Method[] methods = clazz.getMethods();
        for(int i=0;i<methods.length;i++) {
            methodHandler = new MethodHandler(null,null,methods[i]);
        }
        // 反射获取共有方法、私有方法的方法？？
        // 私有方法： getDeclaredMethods
    }

    @Test
    public void testTwo() {
        List<String> testChangeAble = new ArrayList<>();
        for(int i=0;i<10;i++) {
            testChangeAble.add(i+"");
        }
        for(String testStr:testChangeAble) {
            testChangeAble.add("天之道，轮回也");
            System.out.println(testStr);
        }
    }
}
class TestMember {
    private String name = "聚天之气，行天之道";
    private String interestUrl = "我的路在何方？";

    public void sayYourNameTome(String he, @GPRequestParam String tui) {
        System.out.println(name + " 传入的参数内容: " + he + " tui 内容: " + tui);
    }

    public void sayYourNameTome(@GPRequestParam String loser) {
        System.out.println("重载方法可以区分嘛: " + loser);
    }

    public void doNothingSerialize(@GPRequestParam String sameName) {
        System.out.println("跟真正的大佬比起来，我差的太多了，怎么比得过呀 唉爱爱爱爱爱爱爱" + sameName);
    }
}