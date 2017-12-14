package ea.tool.api;

import org.sparx.Collection;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EAFile {
    private String fileName = null;
    private Repository repo = null;

    public boolean open(String fileName) {
        if (fileName == null || fileName.trim().length() <= 0) {
            return false;
        }

        close();
        this.fileName = fileName;

        synchronized ("EAFile") {
            repo = new Repository();
            try {
                repo.OpenFile(fileName);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.printf("Error when open: %s\r\n", fileName);
            }
        }
        return true;
    }

    public boolean isOpen() {
        synchronized ("EAFile") {
            return repo != null;
        }
    }

    public void close() {
        if (!isOpen()) {
            return;
        }

        synchronized ("EAFile") {
            if (repo == null) {
                return;
            }
            repo.CloseFile();
            repo.Exit();
            repo = null;
            fileName = null;
        }
    }

    public List<String[]> getElementList() {
        if (!isOpen()) {
            System.out.println("Please call open() firstly");
            return null;
        }

        // Read file
        List<Package> packageList = new ArrayList<Package>();
        List<Element> elementList = new ArrayList<Element>();
        getElementList(packageList, elementList);

        List<String[]> elements = EAFileUtil.formatElementList(packageList, elementList);
        EAFileUtil.updateInfo(elements, fileName);
        return elements;
    }

    private void getElementList(List<Package> packageList, List<Element> elementList) {
        try {
            getElementList(repo.GetModels(), packageList, elementList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("Error when getElementList: %s\r\n", fileName);
        }
    }

    private void getElementList(Collection<Package> packages, List<Package> packageList, List<Element> elementList) {
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

    private void getElementList(Collection<Element> elements, List<Element> elementList) {
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

    public void updateStoryInfo(List<String[]> elements) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly");
            return;
        }

        if (elements == null || elements.size() <= 0) {
            return;
        }

        List<Package> packageList = new ArrayList<Package>();
        List<Element> elementList = new ArrayList<Element>();
        getElementList(packageList, elementList);
        if (elementList == null || elementList.size() <= 0) {
            return;
        }

        // Check headers firstly
        int elementIndex = 0;
        final String[] headers = elements.get(elementIndex++);
        if (headers == null || headers.length <= 0) {
            return;
        }
        EAHeaderEnum.fillIndex(headers);

        int typeIndex = EAHeaderEnum.Type.getIndex();
        int guidIndex = EAHeaderEnum.GUID.getIndex();
        int statusIndex = EAHeaderEnum.Status.getIndex();
        int storyKeyIndex = EAHeaderEnum.JiraIssueKey.getIndex();

        // Get the GUID map
        Map<String, String[]> guidElementMap = new HashMap<String, String[]>();
        for (String[] element : elements) {
            if (EATypeEnum.isMappedToStory(element[typeIndex])
                    && (EAStatusEnum.isMappedToStory(element[statusIndex])
                    || EAStatusEnum.Approved.getCode().equalsIgnoreCase(element[statusIndex]))) {
                guidElementMap.put(element[guidIndex], element);
            }
        }

        // Update
        for (Element element : elementList) {
            if (EATypeEnum.isMappedToStory(element.GetType())
                    && EAStatusEnum.isMappedToStory(element.GetStatus())
                    && guidElementMap.containsKey(element.GetElementGUID())) {
                // Check values
                String[] values = guidElementMap.get(element.GetElementGUID());
                if (values == null || values.length <= 0) {
                    continue;
                }

                String status = values[statusIndex];
                String storyKey = values[storyKeyIndex];

                boolean changed = false;
                if (status != null && status.trim().length() > 0 && !status.trim().equalsIgnoreCase(element.GetStatus())) {
                    element.SetStatus(status);
                    changed = true;
                }

                if (storyKey != null && storyKey.trim().length() > 0 && !storyKey.trim().equalsIgnoreCase(element.GetStereotype())) {
                    element.SetStereotype(storyKey);
                    changed = true;
                }

                if (changed) {
                    element.Update();
                }
            }
        }
    }

    public void testUpdate(String guid) {
        if (guid == null || guid.trim().length() <= 0) {
            return;
        }

        List<Package> packageList = new ArrayList<Package>();
        List<Element> elementList = new ArrayList<Element>();
        getElementList(packageList, elementList);

        if (elementList == null || elementList.size() <= 0) {
            return;
        }

        guid = guid.trim();
        for (Package pack : packageList) {
            if (guid.equalsIgnoreCase(pack.GetPackageGUID())) {
                System.out.printf("Duplicated GUID with package: %s\r\n", pack.GetName());
                return;
            }
        }

        for (Element element : elementList) {
            if (guid.equalsIgnoreCase(element.GetElementGUID())) {
                System.out.printf("Duplicated GUID with element: %s\r\n", element.GetName());
                return;
            }
        }

        testUpdate(elementList.get(0), guid);
    }

    private void testUpdate(Element element, String guid) {
        if (element == null) {
            return;
        }

        List<String> statusList = new ArrayList<String>() {{
            add("Approved");
            add("已发布");
            add("Proposed");
            add("Mandatory");
            add("Implemented");
            add("Validated");
        }};

        int index = statusList.indexOf(element.GetStatus()) + 1;
        if (index >= statusList.size()) {
            index = 0;
        }
        element.SetStatus("已发布");
        element.SetTag("状态：已发布");
        element.SetStereotype(EAElementUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        element.SetNotes(String.format("%s_%s", element.GetNotes(), guid));
        element.Update();
    }
}
