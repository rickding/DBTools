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
    private String phase;

    private EAItem parent;
    private Set<EAItem> children = new HashSet<EAItem>();

    private boolean codeForExcel = false;

    public EAItem(String name, EAType type, EAStereotype stereotype, EAItem parent) {
        this.name = name;
        this.type = type;
        this.stereotype = stereotype;

        setParent(parent);
        markPhase();
    }

    private void markPhase() {
        phase = null;

        if (!StrUtils.isEmpty(name)) {
            String tmp = name.toLowerCase();
            if (tmp.equals("id") || tmp.endsWith("_id")) {
                phase = name;
            }
        }
    }

    public void setPhase(String phase) {
        this.phase = phase;
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

    private boolean isGarbage() {
        // Check if it's garbage
        if (!StrUtils.isEmpty(name)) {
            for (String garbage : SqlParser.Name_Garbage_List) {
                if (name.endsWith(garbage)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void checkAndMarkProject(EAItem item) {
        if (item == null || StrUtils.isEmpty(name) || !name.equalsIgnoreCase(item.getName()) || isGarbage()) {
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
        if (writer == null || !writer.isOpen() || isGarbage()) {
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
            String tmp = comment.replace(EAWriter.Field_Marker, EAWriter.Field_Marker_Replace);
            fullName = String.format("%s%s", fullName, tmp);
        }

        StringBuilder sb = new StringBuilder();
        if (!StrUtils.isEmpty(rootProject)) {
            sb.append(",");
            sb.append(rootProject);
        }
        for (String project : projectSet) {
            sb.append(",");
            sb.append(project);
        }
        String project = sb.length() > 0 ? sb.substring(1) : "";

        // Combine the needed values
        String[] values = {
                StrUtils.isEmpty(project) ? "" : String.format("%s%s%s", EAWriter.Field_Marker, project, EAWriter.Field_Marker),
                StrUtils.isEmpty(fullName) ? "" : String.format("%s%s%s", EAWriter.Field_Marker, fullName, EAWriter.Field_Marker),
                type == null ? "" : type.getCode(),
                getStereotypeCode(),
                StrUtils.isEmpty(phase) ? "" : phase,
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
