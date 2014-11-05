package com.example.DataBaseGenerator.tools;

import java.io.*;

public class Utils {
    public static String DES_decrypt(String passwordToken, String password) {
        return SecurityUtil.DES_decrypt(passwordToken, password);
    }

    public static String DES_encrypt(String securityToken, String passwd) {
        return SecurityUtil.DES_encrypt(securityToken, passwd);
    }

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        if (srcFile != null && srcFile.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(srcFile);
                result = copyToFile(in, destFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                        in = null;
                    } catch (Exception ex) {
                    }
                }
            }
        }
        return result;
    }

    /**
     * Copy data from a source stream to destFile. Return true if succeed,
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        boolean result = false;
        OutputStream out = null;
        try {
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdirs();
            } else if (destFile.exists()) {
                destFile.delete();
            }
            out = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) >= 0) {
                out.write(buffer, 0, bytesRead);
            }

            result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                } catch (Exception e) {
                    result = false;
                }
                try {
                    out.close();
                } catch (Exception e) {
                    result = false;
                }
            }
        }
        if (!result) {
            destFile.delete();
        }
        return result;
    }
}