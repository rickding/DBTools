package ea.tool.api;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Start...");

        String file = "C:\\Work\\dbtools\\dbtools\\sample\\sample.EAP";
        List<String[]> elements = EAFileUtil.readFile(file);
        System.out.printf("%s, elements: %d, %s\r\n", file, elements.size(), elements);

        for (String[] values : elements) {
            System.out.println(Arrays.asList(values));
        }
    }
}
