//package tomecatstudy;
//
//import org.apache.catalina.servlets.DefaultServlet;
//import org.apache.catalina.startup.Tomcat;
//import org.apache.tomcat.util.buf.ByteChunk;
//
//import java.io.File;
//
///**
// * @program: mavenTomcat
// * @description: 使用 Tomcat7 jar 启动 tomcat 实例，并访问
// * @author: gaoxiang
// * @email: 630268696@qq.com
// * @create: 2021-01-28 16:20
// **/
//public class TomeCatCodeStarter extends DefaultServlet {
//
//    // 使用单例的目的，应该是避免重复创建导致端口占用错误吧
//    public static final Tomcat tomcat = new Tomcat();
//
//    public static void main(String[] args) {
//        File appDir = new File(getBuildDirectory(), "webapps/examples");
//        tomcat.addWebapp(null, "/examples", appDir.getAbsolutePath());
//        tomcat.start();
//        ByteChunk res = getUrl("http://localhost:" + getPort() +
//                "/examples/servlets/servlet/HelloWorldExample");
//    }
//}

// 估计还是要用 TomCat 7 的 example 才能成功