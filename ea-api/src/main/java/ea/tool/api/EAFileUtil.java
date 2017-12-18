package ea.tool.api;

import org.sparx.Element;
import org.sparx.Package;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EAFileUtil {
    private static JiraUserInterface jiraUserInterface;

    public static JiraUserInterface getJiraUserInterface() {
        return jiraUserInterface;
    }

    public static List<String[]> readFile(String file) {
        return readFile(file, null);
    }

    public static List<String[]> readFile(String file, JiraUserInterface userImpl) {
        if (file == null || file.trim().length() <= 0) {
            return null;
        }
        jiraUserInterface = userImpl;

        EAFile eaFile = new EAFile();
        eaFile.open(file);
        List<String[]> elements = eaFile.getElementList();
        eaFile.close();

        return elements;
    }

    public static void updateInfo(List<String[]> elements, String fileName) {
        if (elements == null || elements.size() <= 0) {
            return;
        }

        // Get file name
        if (fileName != null && fileName.trim().length() > 0) {
            File file = new File(fileName);
            fileName = file.getName();
            if (fileName == null || fileName.trim().length() <= 0) {
                fileName = null;
            }
        } else {
            fileName = null;
        }

        // Headers
        int index = 0;
        EAHeaderEnum.fillIndex(elements.get(index++));
        int fileIndex = EAHeaderEnum.FileName.getIndex();
        int parentPathIndex = EAHeaderEnum.ParentPath.getIndex();
        int typeIndex = EAHeaderEnum.Type.getIndex();

        if (fileIndex < 0 || parentPathIndex < 0 || typeIndex < 0) {
            System.out.printf("Error when addFileName(), can't find index: fileIndex: %d, parentPathIndex: %d\r\n", fileIndex, parentPathIndex);
        }

        Map<String, String[]> keyElementMap = EAElementUtil.getKeyElementMap(elements);

        // Set values
        for (; index < elements.size(); index++) {
            String[] element = elements.get(index);
            if (!EATypeEnum.isSavedType(element[typeIndex])) {
                continue;
            }

            if (fileIndex >= 0 && fileName != null) {
                element[fileIndex] = fileName;
            }

            if (parentPathIndex >= 0 && keyElementMap != null && keyElementMap.size() > 0) {
                element[parentPathIndex] = EAElementUtil.getParentPath(element, keyElementMap);
            }
        }
    }

    public static List<String[]> formatElementList(List<Package> packageList, List<Element> elementList) {
        List<String[]> elements = new ArrayList<String[]>() {{
            add(EAHeaderEnum.getHeaders());
        }};

        // packages
        if (packageList != null && packageList.size() > 0) {
            for (Package pack : packageList) {
                elements.add(EAElementUtil.getValues(pack));
            }
        }

        // Elements
        if (elementList != null && elementList.size() > 0) {
            for (Element element : elementList) {
                if (EATypeEnum.isSavedType(element.GetType())) {
                    elements.add(EAElementUtil.getValues(element));
                }
            }
        }
        return elements;
    }
}
