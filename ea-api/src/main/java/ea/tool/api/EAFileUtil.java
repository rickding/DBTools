package ea.tool.api;

import org.sparx.Collection;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EAFileUtil {
    public static List<String[]> readFile(String eaFile) {
        if (eaFile == null || eaFile.trim().length() <= 0) {
            return null;
        }

        // Open file
        Repository eaRepo = new Repository();
        List<Package> packageList = new ArrayList<Package>();
        List<Element> elementList = new ArrayList<Element>();
        List<String[]> elements = null;

        try {
            eaRepo.OpenFile(eaFile);
            Collection<Package> packages = eaRepo.GetModels();

            // Read
            getElementList(packages, packageList, elementList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("Error when getElementList: %s\r\n", eaFile);
        } finally {
            // Format
            elements = formatElementList(packageList, elementList);

            // Close
            eaRepo.CloseFile();
            eaRepo.Exit();
        }

        addFileName(elements, eaFile);
        return elements;
    }

    private static void addFileName(List<String[]> elements, String fileName) {
        if (elements == null || elements.size() <= 0 || fileName == null || fileName.trim().length() <= 0) {
            return;
        }

        // Get file name
        File file = new File(fileName);
        fileName = file.getName();
        if (fileName == null || fileName.trim().length() <= 0) {
            return;
        }

        // Headers
        int index = 0;
        EAHeaderEnum.fillIndex(elements.get(index++));
        int fileIndex = EAHeaderEnum.FileName.getIndex();
        if (fileIndex < 0) {
            System.out.println("Error when addFileName(), can't find index");
            return;
        }

        // Set values
        for (; index < elements.size(); index++) {
            elements.get(index)[fileIndex] = fileName;
        }
    }

    private static void getElementList(Collection<Package> packages, List<Package> packageList, List<Element> elementList) {
        if (packages == null || packages.GetCount() <= 0) {
            return;
        }

        for (Package pack : packages) {
            // Package
            if (packageList != null) {
                packageList.add(pack);
            }

            // Elements
            getElementList(pack.GetElements(), elementList);

            // Sub packages
            getElementList(pack.GetPackages(), packageList, elementList);
        }
    }

    private static void getElementList(Collection<Element> elements, List<Element> elementList) {
        if (elements == null || elements.GetCount() <= 0) {
            return;
        }

        for (Element element : elements) {
            if (elementList != null) {
                elementList.add(element);
            }

            // children elements
            getElementList(element.GetElements(), elementList);
        }
    }

    private static List<String[]> formatElementList(List<Package> packageList, List<Element> elementList) {
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
                elements.add(EAElementUtil.getValues(element));
            }
        }
        return elements;
    }
}
