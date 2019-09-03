package name.yumao.ffxiv.chn.util.nlpcn;

import org.nlpcn.commons.lang.tire.GetWord;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;
import org.nlpcn.commons.lang.util.StringUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class JianFan {

    public static final char CJK_UNIFIED_IDEOGRAPHS_START = '\u4E00';
    public static final char CJK_UNIFIED_IDEOGRAPHS_END = '\u9FA5';
    public static final String TRADITIONAL_MAPPING_FILE = "resource" + File.separator + "nlpcn" + File.separator + "trad.txt";
    public static final String TRADITIONAL_LEXEMIC_MAPPING_FILE = "resource" + File.separator + "nlpcn" + File.separator + "traditional.txt";

    public static final String EMPTY = "";
    public static final String SHARP = "#";
    public static final String EQUAL = "=";

    private char[] chars = null;

    private Forest dict = null;

    private int maxLen = 2;

    private static JianFan jianFan;
    public static synchronized JianFan getInstance(){
        try {
            if(jianFan == null){
                jianFan = new JianFan();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jianFan;
    }

    JianFan() {
        loadCharMapping();
        loadLexemicMapping();
    }

    public void loadCharMapping() {

        String mappingFile = TRADITIONAL_MAPPING_FILE;

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(mappingFile), StandardCharsets.UTF_8));

            CharArrayWriter out = new CharArrayWriter();
            String line = null;
            while (null != (line = in.readLine())) {
                // line = line.trim();
                out.write(line);
            }
            chars = out.toCharArray();
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLexemicMapping() {

        String mappingFile = TRADITIONAL_LEXEMIC_MAPPING_FILE;

        dict = new Forest();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(mappingFile), StandardCharsets.UTF_8));

            String line = null;
            while (null != (line = in.readLine())) {
                // line = line.trim();
                if (line.length() == 0 || line.startsWith(SHARP)) {
                    continue;
                }
                String[] pair = line.split(EQUAL);

                if (pair.length < 2) {
                    continue;
                }
                maxLen = maxLen < pair[0].length() ? pair[0].length() : maxLen;

                Library.insertWord(dict, new Value(pair[0], pair[1]));
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public char convert(char ch) {
        if (ch >= CJK_UNIFIED_IDEOGRAPHS_START && ch <= CJK_UNIFIED_IDEOGRAPHS_END) {
            return chars[ch - CJK_UNIFIED_IDEOGRAPHS_START];
        } else {
            return ch;
        }
    }

    private void strConvert(String str, StringBuilder sb) {
        if (StringUtil.isBlank(str)) {
            return;
        }
        for (int i = 0; i < str.length(); i++) {
            sb.append(convert(str.charAt(i)));
        }
    }


    public String j2f(String str) {
        if (StringUtil.isBlank(str)) {
            return str;
        }

        GetWord word = dict.getWord(str);

        StringBuilder sb = new StringBuilder(str.length());

        String temp = null;
        int beginOffe = 0;
        while ((temp = word.getFrontWords()) != null) {
            strConvert(str.substring(beginOffe, word.offe), sb);
            sb.append(word.getParam(0));
            beginOffe = word.offe + temp.length();
        }

        if (beginOffe < str.length()) {
            strConvert(str.substring(beginOffe, str.length()), sb);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(JianFan.getInstance().j2f("简体中文"));
    }

}
