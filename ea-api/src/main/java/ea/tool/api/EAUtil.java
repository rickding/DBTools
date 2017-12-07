package ea.tool.api;

import org.sparx.Collection;
import org.sparx.Element;
import org.sparx.Package;
import org.sparx.Repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EAUtil {
    private static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    private static String format(Date date, String format) {
        if (date == null) {
            return "";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            System.out.printf("%s, %s\r\n", e.getMessage(), format);
        }
        return "";
    }

    public static String[] getHeaders() {
        return new String[] {
                "Name", "Type", "GUID", "Notes",
                "Phase", "Version", "Priority",
                "Stereotype", "Language", "Author", "Scope", "Alias", "Status",
                "Complexity", "Keywords", "Is Abstract", "Is Leaf", "Is Root",
                "Is Specification", "Created Date", "Modified Date",
                "Requirement Difficulty", "Requirement Priority", "GenFile",
                "Profile Metatype", "CSV_KEY", "CSV_PARENT_KEY",
        };
    }

    private static String[] formatElement(Element pack) {
        if (pack == null) {
            return null;
        }
        return new String[]{
                pack.GetName(), pack.GetType(), pack.GetElementGUID(), pack.GetNotes(),
                pack.GetPhase(), pack.GetVersion(), pack.GetPriority(),
                pack.GetStereotypeEx(), null, pack.GetAuthor(), null, pack.GetAlias(), pack.GetStatus(),
                null, null, null, null, null, // Keywords
                null, format(pack.GetCreated()), format(pack.GetModified()),
                null, null, null,
                null, String.valueOf(pack.GetPackageID()), pack.GetParentID() <= 0 ? null : String.valueOf(pack.GetParentID())
        };
    }

    private static String[] formatPackage(Package pack) {
        if (pack == null) {
            return null;
        }
        return new String[]{
                pack.GetName(), "package", pack.GetPackageGUID(), pack.GetNotes(),
                null, pack.GetVersion(), null,
                pack.GetStereotypeEx(), null, pack.GetOwner(), null, pack.GetAlias(), null,
                null, null, null, null, null,
                null, format(pack.GetCreated()), format(pack.GetModified()),
                null, null, null,
                null, String.valueOf(pack.GetPackageID()), pack.GetParentID() <= 0 ? null : String.valueOf(pack.GetParentID())
        };
    }

    private static List<String[]> formatElementList(List<Package> packageList, List<Element> elementList) {
        List<String[]> elements = new ArrayList<String[]>() {{
            add(getHeaders());
        }};

        // packages
        if (packageList != null && packageList.size() > 0) {
            for (Package pack : packageList) {
                elements.add(formatPackage(pack));
            }
        }

        // Elements
        if (elementList != null && elementList.size() > 0) {
            for (Element element : elementList) {
                elements.add(formatElement(element));
            }
        }
        return elements;
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
            Collection<Element> elements = pack.GetElements();
            for (Element element : elements) {
                if (elementList != null) {
                    elementList.add(element);
                }
            }

            // Sub packages
            getElementList(pack.GetPackages(), packageList, elementList);
        }
    }

    public static List<String[]> getElementList(String eaFile) {
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
        return elements;
    }
}
