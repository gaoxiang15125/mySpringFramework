package com.gupaoedu.util;

/**
 * @program: mavenTomcat
 * @description: 字符串操作工具类
 * @author: gaoxiang
 * @email: 630268696@qq.com
 * @create: 2021-01-31 13:11
 **/
public class StringUtils {

    static final int DISTANCE_UPPER_LETTER = 'a' - 'A';

    public static boolean isEmpty(String str) {
        if(null == str || str.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 将目标字符串首字母小写
     * @param simpleName
     * @return
     */
    public static String toLowerFirstCase(String simpleName) {
        char[] charArray = simpleName.toCharArray();
        charArray[0] += DISTANCE_UPPER_LETTER;
        return String.valueOf(charArray);
    }
}
