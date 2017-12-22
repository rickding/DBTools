package dbtools.common;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello common!");

        System.out.println(HttpClientUtil.sendHttpGet("http://www.baidu.com", null));
    }
}
