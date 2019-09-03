package name.yumao.ffxiv.chn.model;

public enum Language {
    CHS("简体中文", "CHS"),
    CHT("正體中文", "CHT"),
    JA("日语", "JA"),
    EN("英文", "EN"),
    DE("德语", "DE"),
    FR("法语", "FR");

    private String name;
    private String lang;

    Language(String name, String lang){
        this.name = name;
        this.lang = lang;
    }

    public static String toLang(String name){
        for (Language lang: Language.values()) {
            if (lang.name.equals(name)) {
                return lang.lang;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Language.toLang("正體中文"));
    }

}
