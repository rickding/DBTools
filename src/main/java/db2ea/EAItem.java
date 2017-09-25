package db2ea;

/**
 * Created by user on 2017/9/23.
 */
public class EAItem {
    private String name;
    private String comment;

    private EAType type;
    private EAStereotype stereotype;

    private EAItem parent;
    private boolean codeForExcel = false;

    public EAItem(String name, EAType type, EAStereotype stereotype, EAItem parent) {
        this.name = name;
        this.type = type;
        this.stereotype = stereotype;
        this.parent = parent;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public EAStereotype getStereotype() {
        return stereotype;
    }

    public void setParent(EAItem parent) {
        this.parent = parent;
    }

    public void setCodeForExcel(boolean codeForExcel) {
        this.codeForExcel = codeForExcel;
    }

    public String getId() {
        String id = StrUtils.isEmpty(name) ? "" : name;

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
                stereotype == null ? "" : (codeForExcel ? stereotype.getCodeForExcel() : stereotype.getCode()),
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
