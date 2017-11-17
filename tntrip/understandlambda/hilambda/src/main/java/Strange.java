import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by libing2 on 2016/1/5.
 */
public class Strange {
    public static class BeReplacement {
        public List<String> listHex = new ArrayList<>();
        public int replacementStart;//Inclusive
        public int replacementEnd;//Exclusive

    }

    public static final String REG = "\\[0x([\\w\\d]{2})\\]";
    public static final Pattern PTN = Pattern.compile(REG);

    public static String makeItReadable(String content) {
        BeReplacement br = parseToGetHexString(content);
        if (br.listHex.isEmpty()) {
            return content;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(content.substring(0, br.replacementStart));
        byte[] bytes = new byte[br.listHex.size()];
        for (int i = 0; i < br.listHex.size(); i++) {
            bytes[i] = hex2byte(br.listHex.get(i));
        }
        try {
            sb.append(new String(bytes, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        sb.append(content.substring(br.replacementEnd));
        return sb.toString();
    }

    private static BeReplacement parseToGetHexString(String content) {
        BeReplacement br = new BeReplacement();
        Matcher m = PTN.matcher(content);

        boolean firstGroup = true;
        while (m.find()) {
            if (firstGroup) {
                br.replacementStart = m.start();
                firstGroup = false;
            }
            br.replacementEnd = m.end();
            br.listHex.add(m.group(1));
        }

        return br;
    }

    private static byte hex2byte(String aHex) {
        int i = Integer.valueOf(aHex, 16);
        return (byte) i;
    }


    public static void main(String[] args) {
        String content = "\"msg\":\"[0xe6][0x95][0xb0][0xe6][0x8d][0xae][0xe5][0xba][0x93][0xe8][0x80][0x97][0xe6][0x97][0xb6]:80.0812\"";

        System.out.println(makeItReadable(content));

    }
}
