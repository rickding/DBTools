package db2ea.enums;

/**
 * Created by user on 2017/9/30.
 */
public enum ProjectEnum {
    HHPLUS_DEV("prod", "hhplus_dev", "海航erp", " C:\\Work\\prod1.0\\db\\dump_hhplus_dev"),
    PROD_DEV("prod", "prod_dev", "产品化", "C:\\Work\\prod1.0\\db\\dump_prod_dev"),

    PROD_TEST("prod", "prod_test", "产品化主干", "C:\\Work\\prod1.0\\db\\dump_prod_test"),
    SAAS2_TEST("prod", "saas2.0_test", "SaaS2.0", "C:\\Work\\prod1.0\\db\\dump_saas2.0_test"),

    WMS_DEV("wms", "wms_dev", "OWMS", "C:\\Work\\prod1.0\\db\\dump_owms_dev"),
    STB_DEV("stb", "stb_dev", "史泰博", "C:\\Work\\prod1.0\\db\\dump_stb_dev"),
    RUN_HE("xcd", "xcd", "润和", "C:\\Work\\db2ea\\xcd-oms-db.sql");

    private static ProjectEnum[] list = {
            HHPLUS_DEV, PROD_DEV,
            PROD_TEST, SAAS2_TEST,
            WMS_DEV, STB_DEV, RUN_HE
    };

    public static ProjectEnum[] getList() {
        return list;
    }

    public static boolean isSameGroup(ProjectEnum pro1, ProjectEnum pro2) {
        return pro1 != null && pro2 != null && pro1.group.equalsIgnoreCase(pro1.group);
    }

    private String group;
    private String code;
    private String name;
    private String path;

    ProjectEnum(String group, String code, String name, String path) {
        this.group = group;
        this.code = code;
        this.name = name;
        this.path = path;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
