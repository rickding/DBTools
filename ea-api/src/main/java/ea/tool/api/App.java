package ea.tool.api;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Start...");

        String guid = null;
        for (String file : new String[] {"C:\\Work\\dbtools\\dbtools\\sample\\sample.EAP"}) {
            List<String[]> elements = EAFileUtil.readFile(file);
            System.out.printf("%s, elements: %d, %s\r\n", file, elements.size(), elements);
            for (String[] values : elements) {
                System.out.println(Arrays.asList(values));
            }
            guid = elements.get(elements.size() - 1)[EAHeaderEnum.GUID.getIndex()];
        }

        EAFile eaFile = new EAFile();
        for (String file : new String[] {"C:\\Work\\dbtools\\dbtools\\sample\\test.EAP"}) {
            eaFile.open(file);
            List<String[]> elements = eaFile.getElementList();

            if (guid != null) {
                eaFile.testUpdate(guid);
            }

            eaFile.close();

            System.out.printf("%s, elements: %d, %s\r\n", file, elements.size(), elements);
            for (String[] values : elements) {
                System.out.println(Arrays.asList(values));
            }
        }
    }
}
