package com.tntrip.mob.askq.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


//stringutil工具类
public class TnStringUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TnStringUtils.class);

    private static final String DEFAULT_CHARSET = "UTF-8";

    public static void concat(StringBuilder sb, List<String> list,
                              String normal, String lastSpecial) {
        concat(sb, list.toArray(new String[0]), normal, lastSpecial);
    }

    public static String concat(List<String> list, String normal,
                                String lastSpecial) {
        StringBuilder sb = new StringBuilder("");
        concat(sb, list.toArray(new String[0]), normal, lastSpecial);
        return sb.toString();
    }

    public static void concat(StringBuilder sb, String[] array, String normal,
                              String lastSpecial) {
        int length = array.length;
        for (int i = 0; i < length; i++) {
            sb.append(array[i]);
            if (i == length - 1) {
                sb.append(lastSpecial);
            } else {
                sb.append(normal);
            }
        }
    }

    public static boolean allDigit(String str) {
        if (str == null) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * str.toString() will return "null" when (str == null), this method returns "" if obj is empty,
     * <p>
     * however this would still return "null" is <code>obj#toString()</code> returns "null"
     */
    public static String anything2str(Object obj) {
        if (obj == null) {
            return "";
        }
        return obj.toString();
    }

    //判断是否为空
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    //判断是否为非空
    public static boolean isNotEmpty(String str) {
        return !TnStringUtils.isEmpty(str);
    }

    //判断是否为空
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    public static boolean blankOrLiteralNull(String str) {
        return isBlank(str) || ("null".equalsIgnoreCase(str.trim()));
    }

    //判断是否为非空
    public static boolean isNotBlank(String str) {
        return !TnStringUtils.isBlank(str);
    }

    public static String trim2Empty(String str) {
        return str == null ? "" : str.trim();
    }

    public static String reserveLeft(String orgn, int len) {
        if (TnStringUtils.isEmpty(orgn)) {
            return "";
        }
        len = len < 0 ? 0 : len;
        if (orgn.length() > len) {
            return orgn.substring(0, len);
        }
        return orgn;
    }

    //判断某个字符串的索引
    public static int indexOf(String str, char searchChar) {
        if (isEmpty(str)) {
            return -1;
        }
        return str.indexOf(searchChar);
    }

    //判断是否存在某个字符
    public static boolean contains(String str, char searchChar) {
        if (isEmpty(str)) {
            return false;
        }
        return str.indexOf(searchChar) >= 0;
    }

    //判断是否为数字
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String byte2utf8str(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> sliceList(List<T> orgn, int startIndex, int count) {
        if (orgn == null || orgn.isEmpty()) {
            return Collections.emptyList();
        }

        List<T> rtn = new ArrayList<>(count);
        int loop = 0;
        for (int i = startIndex; (i < orgn.size() && loop < count); i++) {
            rtn.add(orgn.get(i));
            loop++;
        }
        return rtn;
    }

    public static String retainDigital(String str, int maxRetainCount) {
        if (isBlank(str)) {
            return "";
        }
        char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; (i < str.length() && sb.length() < maxRetainCount); i++) {
            char c = str.charAt(i);
            if (inArray(chars, c)) {
                sb.append(c);
            }
        }
        return sb.length() == 0 ? "" : sb.toString();
    }

    public static long str2long(String str, long dftValue) {
        if (isBlank(str)) {
            return dftValue;
        }
        try {
            return Long.valueOf(str);
        } catch (NumberFormatException e) {
            return dftValue;
        }
    }

    public static int str2int(String str, int dftValue) {
        if (isBlank(str)) {
            return dftValue;
        }
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return dftValue;
        }
    }

    public static boolean str2boolean(String str) {
        String[] array = {"true", "yes", "y"};
        for (String aVal : array) {
            if (aVal.equalsIgnoreCase(TnStringUtils.trim2Empty(str))) {
                return true;
            }
        }
        return false;
    }

    private static boolean inArray(char[] array, char c) {
        for (char c1 : array) {
            if (c1 == c) {
                return true;
            }
        }
        return false;
    }

    public static boolean inArray(int[] array, int i) {
        if (array == null || array.length == 0) {
            return false;
        }
        for (int val : array) {
            if (val == i) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean inArray(T[] array, T t) {
        if (t == null) {
            return false;
        }
        if (array == null || array.length == 0) {
            return false;
        }
        for (T val : array) {
            if (Objects.equals(val, t)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean isEmpty(T[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(int[] array) {
        if (array == null || array.length == 0) {
            return true;
        }
        return false;
    }

    /**
     * Formats a string using {@link MessageFormat#format(String, Object[])}.
     * <p>
     * This is a convenience method.
     * </p>
     *
     * @param pattern
     * @param argument2
     * @return the formated string
     */
    public static String format(final String pattern, final Object... argument2) {
        return MessageFormat.format(pattern, argument2);
    }

    public static List<String> split(String original, String delimiter) {
        List<String> list = new ArrayList<>();
        if (original == null || original.trim().length() == 0) {
            return list;
        }
        int delimiterL = delimiter.length();
        int orgnL = original.length();
        int lastEnd = 0;// delimiter's (endIndex+1)
        while (lastEnd <= (orgnL - 1)) {
            int currentStart = original.indexOf(delimiter, lastEnd);
            if (currentStart == -1) {
                list.add(original.substring(lastEnd));
                break;
            }
            list.add(original.substring(lastEnd, currentStart));
            lastEnd = currentStart + delimiterL;
        }
        if (original.endsWith(delimiter)) {
            list.add("");
        }
        return list;
    }

    public static String literallyReplace(String orgn, String literal, String replacement) {
        if (orgn == null || literal == null || replacement == null) {
            return null;
        }
        if (literal.length() == 0) {
            return orgn;
        }
        // 正则表达式里面的 Literal-text span
        // 这样， literal 中的字符都不会被当做是 特殊字符
        return orgn.replaceAll("\\Q" + literal + "\\E", replacement);
    }


    public static String human(long value) {
        String str = value + "";
        StringBuilder sb = new StringBuilder();
        int cnt = 0;
        for (int i = str.length() - 1; i >= 0; i--) {
            cnt++;
            if ((cnt == 3) && (i != 0)) {
                cnt = 0;
                sb.insert(0, "," + str.charAt(i));
            } else {
                sb.insert(0, str.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String stripPrefx(String str, String prefix) {
        if (str == null || prefix == null) {
            return str;
        }
        if (!str.startsWith(prefix)) {
            throw new RuntimeException("str must start with prefix");
        }
        return str.substring(prefix.length());
    }
    /**
     * 将 <code>target</code> 中的增补字符替换成 <code>replacement</code>。
     * 注意：是整个增补字符替换为 <code>replacement</code>
     * <p>
     * 在java中表示时，一般的字符 用一个char表示，但是，增补字符总是用两个char来表示
     * <p>
     * 第一个字符称为：高位代理(highSurrogate)，第二个字符称为：低位代理(lowSurrogate)
     * <p>
     * <p>
     * 返回值：总是一个长度为2的字符串数组。index0是替换完成后得到的结果，index1是 被剔除掉的增补字符
     *
     * @param target
     * @param replacement
     * @return
     */
    public static String[] replaceSurrogateChar(String target, String replacement) {
        if (target == null || target.equals("")) {
            return new String[]{target, null};
        }

        int len = target.length();
        StringBuilder sbNormal = new StringBuilder(len);
        StringBuilder sbSurrogate = new StringBuilder(10);

        boolean lastCharIsSurrogate = false;
        for (int i = 0; i < len; i++) {
            char curC = target.charAt(i);
            // Current char is surrogate
            if (Character.isSurrogate(curC)) {
                sbSurrogate.append(curC);
                if (lastCharIsSurrogate) {
                    // curC is lowSurrogate.
                    // We've already add replacement when last time we encounter highSurrogate
                    // So, sbNormal need NOT add the replacement
                } else {
                    // curC is highSurrogate.
                    sbNormal.append(replacement);
                }
                lastCharIsSurrogate = true;
            } else {
                sbNormal.append(curC);
                lastCharIsSurrogate = false;
            }
        }
        return new String[]{sbNormal.toString(), sbSurrogate.toString()};
    }


    /**
     * 有些调用只允许公司内部调用，而ip地址如果是私有地址，则必定是公司内部的调用
     * <p>
     * A类： 里面的10.x.x.x是private address
     * B类：128.x.x.x  ----  191.x.x.x（里面的172.16.x.x -- 172.31.x.x是private address）
     * C类：192.x.x.x  ----  223.x.x.x（里面的192.168.x.x是private address）
     *
     * @param ipv4
     * @return
     */
    public static boolean isPrivateIPv4(String ipv4) {
        if (TnStringUtils.isBlank(ipv4)) {
            return false;
        }
        List<String> listLeft2Right = TnStringUtils.split(ipv4.trim(), ".");
        if (CollectionUtils.isEmpty(listLeft2Right) || listLeft2Right.size() != 4) {
            return false;
        }
        int left0 = TnStringUtils.str2int(listLeft2Right.get(0).trim(), -1);
        int left1 = TnStringUtils.str2int(listLeft2Right.get(1).trim(), -1);
        if (left0 == -1 || left1 == -1) {
            return false;
        }
        // 10.x.x.x是private address
        if (left0 == 10) {
            return true;
        }
        // 172.16.x.x -- 172.31.x.x是private address
        if (left0 == 172 && (left1 >= 16 && left1 <= 31)) {
            return true;
        }
        // 192.168.x.x是private address
        if (left0 == 192 && left1 == 168) {
            return true;
        }
        return false;
    }

    private static volatile String LOCAL_HOST_ADDRESS = "unknown";

    public static String getLocalHostAddress() {
        if (LOCAL_HOST_ADDRESS.equals("unknown")) {
            try {
                LOCAL_HOST_ADDRESS = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e) {
                LOCAL_HOST_ADDRESS = "unknown";
            }
        }
        return LOCAL_HOST_ADDRESS;
    }

    public static <R extends Number, T extends Enum<T>> Map<R, T> num2EnumMap(T[] array, Function<T, R> func) {
        Map<R, T> map = new HashMap<>(array.length);
        for (T e : array) {
            map.put(func.apply(e), e);
        }
        return map;
    }

    public static class MergeResult<F, S> {
        public List<F> needInsert_F = new ArrayList<>();
        public List<S> needDeleted_S = new ArrayList<>();
        public List<F> inCommon_F = new ArrayList<>();
        public List<S> inCommon_S = new ArrayList<>();

    }

    public static <F, S> MergeResult<F, S> mergeBasedOnFirst(Collection<F> first,
                                                             Collection<S> second,
                                                             BiFunction<F, S, Boolean> funcIsEqual) {
        MergeResult<F, S> mr = new MergeResult<>();
        if (CollectionUtils.isEmpty(first)) {
            mr.needInsert_F.clear();
            mr.needDeleted_S.addAll(second == null ? Collections.emptyList() : second);
            mr.inCommon_F.clear();
            mr.inCommon_S.clear();
        }

        if (CollectionUtils.isEmpty(second)) {
            mr.needInsert_F.addAll(first == null ? Collections.emptyList() : first);
            mr.needDeleted_S.clear();
            mr.inCommon_F.clear();
            mr.inCommon_S.clear();
            return mr;
        }

        for (F f : first) {
            boolean fAlreadyExist = false;
            for (S s : second) {
                // second中，有一个元素是和 f 一致的
                if (funcIsEqual.apply(f, s)) {
                    mr.inCommon_F.add(f);
                    mr.inCommon_S.add(s);
                    fAlreadyExist = true;
                    break;
                }
            }

            if (fAlreadyExist) {
                // second中，有一个元素是和 f 一致的
                // 什么都不用作
            }
            // f 根本就不存在于 second 中
            else {
                mr.needInsert_F.add(f);
            }
        }

        for (S s : second) {
            if (!mr.inCommon_S.contains(s)) {
                mr.needDeleted_S.add(s);
            }
        }
        return mr;
    }

    /**
     * @param data 参数值
     * @return 编码后的字符串
     * @title 采用utf-8对url参数进行编码
     * @author fanguotao
     * @date 2015-09-10 15:10
     */
    public static String encodeUrl(String data) {
        return encodeUrl(data, DEFAULT_CHARSET);
    }

    /**
     * @param data   参数值
     * @param encode 编码格式
     * @return 编码后的字符串
     * @title 对url参数进行编码
     * @author fanguotao
     * @date 2015-09-10 15:10
     */
    public static String encodeUrl(String data, String encode) {
        try {
            return URLEncoder.encode(data, encode);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("encode url error", e);
            return "";
        }
    }

    /**
     * 设置客户头像
     *
     * @param orgnUrl                    头像url
     * @param suffix                     _w180_h180_c1_t0
     * @param dftReturnUrlIfOrgnUrlEmpty
     * @return 返回url
     */
    // http://m.tntripcdn.com/filebroker/cdn/prd/45/ec/45ec011e824c847ec6e0547daf9734f0.jpg -->
    // http://m.tntripcdn.com/filebroker/cdn/prd/45/ec/45ec011e824c847ec6e0547daf9734f0_w70_h70_c1_t0.jpg
    public static String appendSubfixIfNecessary(String orgnUrl, String suffix, String dftReturnUrlIfOrgnUrlEmpty) {
        if (TnStringUtils.blankOrLiteralNull(orgnUrl)) {
            return dftReturnUrlIfOrgnUrlEmpty;
        }
        // already contains this suffix. do nothing
        if (TnStringUtils.isBlank(suffix) || orgnUrl.contains(suffix)) {
            return orgnUrl;
        }
        int lastDot = orgnUrl.lastIndexOf(".");
        if (lastDot == -1) {
            return orgnUrl;
        }
        return orgnUrl.substring(0, lastDot) + suffix + orgnUrl.substring(lastDot);
    }

    public static List<String> strImages2List(String strImages, String delimiter, String suffix) {
        if (TnStringUtils.blankOrLiteralNull(strImages)) {
            return Collections.emptyList();
        }
        List<String> eachOrgnImage = TnStringUtils.split(strImages, delimiter).
                stream().map(TnStringUtils::trim2Empty).filter(e -> !TnStringUtils.isBlank(e)).collect(Collectors.toList());
        List<String> images = new ArrayList<>(eachOrgnImage.size());
        for (String orgn : eachOrgnImage) {
            String finalImg = appendSubfixIfNecessary(orgn, suffix, "");
            if (!TnStringUtils.isEmpty(finalImg)) {
                images.add(finalImg);
            }
        }
        return images;
    }

    public static String listImages2String(List<String> listImages, String delimiter) {
        if (CollectionUtils.isEmpty(listImages)) {
            return "";
        }
        String strImages = "";
        for (String image : listImages) {
            strImages += image + delimiter;
        }
        strImages = strImages.substring(0, strImages.length() - delimiter.length());
        return strImages;
    }

    public static String slice(String str, int startInclusive, int length) {
        if (str == null) {
            return "";
        }
        int minLength = startInclusive + length;
        if (str.length() <= minLength) {
            return str.substring(startInclusive);
        } else {
            return str.substring(startInclusive, minLength);
        }
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     */
    public static String md5_16(String str) {
        if (TnStringUtils.isBlank(str)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] messageDigest = md.digest(str.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++) {
                String tmp = Integer.toHexString(0x0F & messageDigest[i]);
                hexString.append(tmp);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        long begin0 = System.currentTimeMillis();
        long begin = System.currentTimeMillis();
        int round = 0;
        int minIdExclusive = -1;
        int count = 500;
        System.out.println(md5_16("公交卡拉大数据的刚加瓦儿歌更惧怕围观"));
    }
}

