package db2ea;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 2017/9/23.
 */
public class EAItem {
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

    public void addChild(EAItem child) {
        // Set will remove the duplicated one.
        children.add(child);
    }

    public void setCodeForExcel(boolean codeForExcel) {
        this.codeForExcel = codeForExcel;
    }

    public String getStereotypeCode() {
        return stereotype == null ? "" : (codeForExcel ? stereotype.getCodeForExcel() : stereotype.getCode());
    }

    public int getStereotypeId() {
        return stereotype == null ? 0 : stereotype.getId();
    }


    public void saveToFile(EAWriter writer, boolean codeForExcel) {
        if (writer == null || !writer.isOpen()) {
            return;
        }

        // Save itself
        setCodeForExcel(codeForExcel);
        writer.write(this);

        if (children == null || children.size() <= 0) {
            return;
        }

        // Save children
        for (EAItem child : children) {
            child.saveToFile(writer, codeForExcel);
        }
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

        // Combine the needed values
        String[] values = {
                fullName,
                type == null ? "" : type.getCode(),
                getStereotypeCode(),
                getId(),
                parent == null ? "" : parent.getId()
        };

        StringBuilder sb = new StringBuilder();
        for (String str : values) {
            sb.append(EAWriter.Field_Separator);
            sb.append(str);
        }

        return sb.substring(1);
    }
}
