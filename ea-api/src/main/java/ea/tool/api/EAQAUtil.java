package ea.tool.api;

public class EAQAUtil {
    private static String[] EA_QA_Flag_Array = new String[] {
            "测试负责人",
            "QA负责人", "QA 负责人",
            "测试leader", "测试 leader",
            "QALeader", "QA Leader",
            "测试工程师", "测试 工程师",
            "QA", "测试",
    };

    private static String[] EA_QA_Marker_Array = new String[] {
            ",", ":", ";",
            "，", "：", "；", "、",
            " ", " ", "  ", "\t",
    };

    private static String[] EA_QA_Separator_Array = new String[] {
            ", ", "， ", "、 ", "; ", "； ", ": ", "： "
    };

    public static String[] parseQAStr(String value) {
        if (value == null || value.trim().length() <= 0) {
            return null;
        }

        String separator = EA_QA_Separator_Array[0];
        return value.trim().split(separator);
    }

    /**
     * Format the QA names with separator
     * @param value
     * @return
     */
    public static String formatQAStr(String value) {
        if (value == null || value.trim().length() <= 0) {
            return null;
        }

        String marker = EA_QA_Marker_Array[0];
        value = value.trim().replace(EA_QA_Separator_Array[0], marker).replace(" ", "");
        for (String sep : EA_QA_Marker_Array) {
            value = value.replace(sep, marker);
        }

        value = value.replace(marker, EA_QA_Separator_Array[0]);
        return value;
    }

    /**
     * Parse QA from the notes
     * @param value
     * @return
     */
    public static String[] getQAArray(String value) {
        return parseQAStr(getQAStr(value));
    }

    public static String getQAStr(String value) {
        if (value == null || value.trim().length() <= 0) {
            return null;
        }

        // Find with the flag
        value = value.trim().toLowerCase();
        for (String flag : EA_QA_Flag_Array) {
            for (String sep : EA_QA_Marker_Array) {
                String tmp = String.format("%s%s", flag, sep);
                int index = value.indexOf(tmp.toLowerCase());

                if (index >= 0 && value.length() > tmp.length()) {
                    // Get the possible value
                    tmp = value.substring(index + tmp.length());

                    if (tmp != null && tmp.trim().length() > 0) {
                        // Replace separators with marker
                        tmp = tmp.trim();
                        String marker = EA_QA_Marker_Array[0];
                        for (String oldSep : EA_QA_Separator_Array) {
                            tmp = tmp.replace(oldSep, marker);
                        }

                        // Remove html markers
                        tmp = removeHtml(tmp);

                        // Split to get the value
                        String[] arr = tmp.split("\\s+");
                        if (arr != null && arr.length > 0) {
                            tmp = arr[0].trim();
                            if (tmp != null && tmp.length() > 0) {
                                tmp = formatQAStr(tmp);
                                return tmp;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String removeHtml(String str) {
        if (str == null || str.trim().length() <= 0) {
            return null;
        }

        char start = '<', end = '>';
        str = str.trim();

        while (str.length() > 0 && str.indexOf(start) >= 0) {
            String tmp = str.substring(0, str.indexOf(start)).trim();

            int endIndex = str.indexOf(end);
            if (endIndex >= 0) {
                str = String.format("%s%s", tmp, str.substring(endIndex + 1, str.length()).trim());
            } else {
                str = tmp;
            }
        }

        while (str.length() > 0 && str.indexOf(end) >= 0) {
            str = str.substring(str.indexOf(end) + 1, str.length()).trim();
        }

        return str;
    }
}
