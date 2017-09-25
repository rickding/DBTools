package db2ea;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 2017/9/23.
 */
public class EAItem {
    private String rootProject;
    private Set<String> projectSet = new HashSet<String>();
    private String name;
    private String comment;

    private EAType type;
    private EAStereotype stereotype;

    private EAItem parent;
    private Set<EAItem> children = new HashSet<EAItem>();

    private boolean codeForExcel = false;

    public EAItem(String name, EAType type, EAStereotype stereotype, EAItem parent) {
        this.name = name;
        this.type = type;
        this.stereotype = stereotype;

        setParent(parent);
    }

    public String getProject() {
        return rootProject;
    }

    public void setProject(String project) {
        this.rootProject = project;

        if (children == null || children.size() <= 0) {
            return;
        }

        // Save children
        for (EAItem child : children) {
            child.setProject(project);
        }
    }

    private void addProject(String project) {
        projectSet.add(project);
    }

    public void checkAndMarkProject(EAItem item) {
        if (item == null || StrUtils.isEmpty(name) || !name.equalsIgnoreCase(item.getName())) {
            return;
        }

        addProject(item.getProject());
        item.addProject(getProject());

        // Check the children
        for (EAItem child1 : children) {
            for (EAItem child2 : item.children) {
                child1.checkAndMarkProject(child2);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public EAStereotype getStereotype() {
        return stereotype;
    }

    public void setParent(EAItem parent) {
        this.parent = parent;
        if (null != parent) {
            parent.addChild(this);
        }
    }

    private void addChild(EAItem child) {
        // Set will remove the duplicated one.
        children.add(child);
    }

    public void saveToFile(EAWriter writer, boolean codeForExcel) {
        if (writer == null || !writer.isOpen()) {
            return;
        }

        // Save itself
        this.codeForExcel = codeForExcel;
        writer.write(this);

        if (children == null || children.size() <= 0) {
            return;
        }

        // Save children
        for (EAItem child : children) {
            child.saveToFile(writer, codeForExcel);
        }
    }

    private String getStereotypeCode() {
        return stereotype == null ? "" : (codeForExcel ? stereotype.getCodeForExcel() : stereotype.getCode());
    }

    private int getStereotypeId() {
        return stereotype == null ? 0 : stereotype.getId();
    }

    public String getId() {
        String id = StrUtils.isEmpty(name) ? "" : String.format("%d_%s", getStereotypeId(), name);

        if (parent != null) {
            String pid = parent.getId();

            // format with parent id
            if (!StrUtils.isEmpty(pid)) {
                if (StrUtils.isEmpty(id)) {
                    id = pid;
                } else {
                    id = String.format("%s_%s", parent.getId(), id);
                }
            }
        }
        return id;
    }

    @Override
    public String toString() {
        // Combine name and comment
        String fullName = StrUtils.isEmpty(name) ? "" : name;
        if (!StrUtils.isEmpty(comment)) {
            String tmp = comment.replace(EAWriter.Field_Separator, EAWriter.Field_Separator_Replace);
            fullName = String.format("%s%s", fullName, tmp);
        }

        StringBuilder sb = new StringBuilder();
        if (!StrUtils.isEmpty(rootProject)) {
            sb.append(", ");
            sb.append(rootProject);
        }
        for (String project : projectSet) {
            sb.append(", ");
            sb.append(project);
        }
        String project = sb.length() > 0 ? sb.substring(2) : "";

        // Combine the needed values
        String[] values = {
                StrUtils.isEmpty(project) ? "" : String.format("\"%s\"", project),
                fullName,
                type == null ? "" : type.getCode(),
                getStereotypeCode(),
                getId(),
                parent == null ? "" : parent.getId()
        };

        sb = new StringBuilder();
        for (String str : values) {
            sb.append(EAWriter.Field_Separator);
            sb.append(str);
        }

        return sb.substring(1);
    }
}
