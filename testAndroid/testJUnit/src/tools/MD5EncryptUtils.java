package tools;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.util.Locale;

public class MD5EncryptUtils {
    //private static final String TAG = "MD5EncryptUtils";
    private static String mSalt = null;
    private static final String DES_KEY = Utils.DES_decrypt("603d5c3d2bc1246c34a4a15abfd1a453", "jtest*confxy@1");

    /**
     * MD5加密
     * 
     * @param 要加密的字符串
     * @return md5值
     */
    public static String encrypt(String data) {
        if (data != null) {
            data = data.toLowerCase(Locale.US);
        }
        if (mSalt == null) {
        	mSalt = fixedLengthHexSalt(DES_KEY);
        }
        data = md5Hex(data + mSalt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = data.charAt(i / 3 * 2);
            char c = mSalt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = data.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * MD5加密
     * 
     * @param 要加密的字符串
     * @param key
     * @return md5值
     */
    public static String encrypt(String data, String s) {
        if (data != null) {
            data = data.toLowerCase(Locale.US);
        }
        String salt = fixedLengthHexSalt(s);
        data = md5Hex(data + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = data.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = data.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }
    
    /**
     * 
     * @param data
     *            原始字符串
     * @param md5
     *            md5字符串
     * @return 比较的结果
     */

    public static boolean verify(String data, String md5) {
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        return md5Hex(data + salt).equals(new String(cs1));
    }

    private static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }

    private static String fixedLengthHexSalt(String salt) {
        String hex = new String(new Hex().encode(salt.getBytes()));
        if (hex.length() >= 16) {
            return hex.substring(0, 16);
        } else {
            StringBuilder sb = new StringBuilder(16);
            sb.append(hex);
            int len = sb.length();
            if (len < 16) {
                for (int i = 0; i < 16 - len; i++) {
                    sb.append("0");
                }
            }
            return sb.toString();
        }
    }
    
}