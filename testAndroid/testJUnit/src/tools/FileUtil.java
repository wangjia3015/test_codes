package tools;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 */
public class FileUtil {

    public static List<String> getFileLines(String filePath) {

        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        List<String> lines = new ArrayList<String>();

        BufferedReader bufReader = null;

        try {
            bufReader = new BufferedReader(new FileReader(file), 1024);
            String line;
            while ((line = bufReader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufReader != null)
                    bufReader.close();
            } catch (Exception e) {
            }
        }
        return lines;
    }

    public static void writeFile(String destFilePath, List<String> lines) {
        BufferedWriter bufWriter = null;
        try {
            bufWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFilePath)));
            for (String line : lines) {
                bufWriter.write(line + "\n");
            }
            bufWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufWriter != null)
                    bufWriter.close();
            } catch (Exception e) {
            }
        }
    }

    // 复制文件
    public static void copyFile(File sourceFile, File targetFile) throws IOException {
        if (targetFile.exists()) {
            return;
        }

        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    }

    // 复制文件夹
    public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile = file[i];
                // 目标文件
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1 = sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2 = targetDir + "/" + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }

    /**
     * 
     * @param filepath
     * @throws java.io.IOException
     */
    public static void del(String filepath) throws IOException {
        File f = new File(filepath);// 定义文件路径
        if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
            if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                f.delete();
            } else {// 若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;
                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                    }
                    delFile[j].delete();// 删除文件
                }
            }
        }
    }

    private static final boolean DEBUG = false;
    private static final String TAG = null;

    /**
     *
     * @param srcFileName
     * @param destFileName
     * @param srcCoding
     * @param destCoding
     * @throws java.io.IOException
     */
    public static void copyFile(File srcFileName, File destFileName, String srcCoding, String destCoding)
            throws IOException {// 把文件转换为GBK文件
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(srcFileName), srcCoding));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destFileName), destCoding));
            char[] cbuf = new char[1024 * 5];
            int len = cbuf.length;
            int off = 0;
            int ret = 0;
            while ((ret = br.read(cbuf, off, len)) > 0) {
                off += ret;
                len -= ret;
            }
            bw.write(cbuf, 0, off);
            bw.flush();
        } finally {
            if (br != null)
                br.close();
            if (bw != null)
                bw.close();
        }
    }

    /**
     * 解析一个Asset配置文件，将每一行输出到一个list中。
     * */
//    public static List<String> parseAssetFile(Context context, String filename) {
//        InputStream is;
//        try {
//            is = context.getAssets().open(filename);
//        } catch (IOException e1) {
//            if (DEBUG) {
//                Log.w(TAG, "user config file not exists: " + filename);
//            }
//            return null;
//        }
//        return parseConfigFile(new InputStreamReader(is));
//    }

    public static boolean writeFile(File file, String content) throws IOException {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bufWriter = null;
        boolean flag = false;
        try {
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos);
            bufWriter = new BufferedWriter(osw);
            bufWriter.write(content);
            bufWriter.flush();
            flag = true;
        } finally {
            if (bufWriter != null) {
                try {
                    bufWriter.close();
                } catch (Exception e) {
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (Exception e) {
                }
            }
        }
        return flag;
    }

//    public static void copyAssetsFile(Context context, String assetsFileName, File destFile) throws IOException {
//        copyFile(context.getAssets().open(assetsFileName), destFile);
//    }
//
//    public static void copyDataFilesFile(Context context, String dataFilesFileName, File destFile) throws IOException {
//        copyFile(context.openFileInput(dataFilesFileName), destFile);
//    }

    public static void copyFile(InputStream is, File destFile) throws IOException {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(destFile);
            byte arrayByte[] = new byte[1024];
            int i = 0;
            while ((i = is.read(arrayByte)) != -1) {
                fos.write(arrayByte, 0, i);
            }
            fos.flush();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** 读配置文件，获取配置列表，可能为null */
    public static List<String> parseConfigFile(Reader in) {
        List<String> configList = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(in, 1024);
            String line;
            while (!TextUtils.isEmpty(line = br.readLine())) {
                if (!line.startsWith("#")) {
                    // # 开头的行为注释，跳过
                    configList.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            configList = null;
            return null;
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                // ignore
            }
        }

        if (configList.size() > 0)
            return configList;
        else
            return null;
    }
}
