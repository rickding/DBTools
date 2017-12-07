package ea.tool.api;

public class EAQAUtil {
    private static String[] EA_QA_Flag_Array = new String[] {
            "测试负责人",
            "QA负责人", "QA 负责人",
            "测试leader", "测试 leader",
            "QALeader", "QA Leader",
            "测试工程师", "测试 工程师",
            "QA",
    };

    private static String[] EA_QA_Separator_Array = new String[] {
            ",", ":", ";",
            "，", "：", "；",
            " ", " ", "  ",
    };

    public static String getQA(String value) {
        if (value == null || value.trim().length() <= 0) {
            return null;
        }

        // Find with the flag
        value = value.trim().toLowerCase();
        for (String flag : EA_QA_Flag_Array) {
            for (String sep : EA_QA_Separator_Array) {
                String tmp = String.format("%s%s", flag, sep);
                int index = value.indexOf(tmp.toLowerCase());
                if (index >= 0 && value.length() > tmp.length()) {
                    // Get the possible value
                    tmp = value.substring(index + tmp.length());
                    if (tmp != null && tmp.trim().length() > 0) {
                        // Split to get the value
                        String[] arr = tmp.trim().split("\\s+");
                        if (arr != null && arr.length > 0) {
                            tmp = arr[0];
                            if (tmp != null && tmp.trim().length() > 0 && tmp.length() < 7) {
                                return tmp;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
