package tools;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
    private static final String TAG = "ZipUtil";
    public static final boolean bDebug = true;
    private static final int BUFF_SIZE = 1024 * 32; // 32k Byte

    public enum SizeLimitZipResult {
        SizeLimitZipResult_OK, // 正常压缩
        SizeLimitZipResult_TooBig, // 有超出体检的压缩文件
        SizeLimitZipResult_NotFound // 目录是空的
    };

    /**
     * 用标准zip压缩一个目录
     * 
     * @param dirTobeZip
     *            待压缩目录
     * @param newZipFile
     *            压缩后的文件
     * @throws java.io.IOException
     */
    public static void ZipDir(File dirTobeZip, File newZipFile) throws IOException {

        InputStream input = null;

        ZipOutputStream zipOut = null;

        zipOut = new ZipOutputStream(new FileOutputStream(newZipFile));
        if (dirTobeZip.isDirectory()) {
            // 判断是否是目录
            File lists[] = dirTobeZip.listFiles();
            // 列出全部文件
            for (int i = 0; i < lists.length; i++) {
                // 设置文件输入流
                // 每一个被压缩的文件都用ZipEntry表示，需要为每一个压缩后的文件设置
                // 名称
                File currentFile = lists[i];
                input = new FileInputStream(currentFile);
                zipOut.putNextEntry(new ZipEntry(dirTobeZip.getName() + File.separator + currentFile.getName()));
                int temp = 0;
                // 接收输入的数据
                while ((temp = input.read()) != -1) { // 读取内容
                    zipOut.write(temp);// 压缩输出内容
                }
                input.close();
            }
        }
        zipOut.close();
    }

    /**
     * 使用gzip格式压缩一个文件
     *
     * @param srcFile
     *            待压缩文件
     * @param zipFile
     *            压缩后的文件
     * @throws java.io.IOException
     */
    public static void GzipOneFile(File srcFile, File zipFile) throws IOException {
        if (srcFile.exists()) {
            GZIPOutputStream zipout = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile),
                    BUFF_SIZE));
            _gzipFile(srcFile, zipout);
            zipout.close();
        }
    }

    /**
     * 用标准zip 把一个目录压缩成一个文件，再用GZip再压缩一下 注意 newGZipFile 不要放在 dirTobeZip 目录下
     *
     * @param dirTobeZip
     *            代压缩的目录
     * @param newGZipFile
     *            压缩后的文件
     * @throws java.io.IOException
     */
    public static SizeLimitZipResult ZipDirGzip(File dirTobeZip, File newGZipFile, int fileSizeLimit, int totalSizeLimit)
            throws IOException {
        String tempFileName = newGZipFile.getAbsolutePath() + ".tmp";
        File tempZipFile = new File(tempFileName);
        boolean dret1 = tempZipFile.delete();//避免出现无法解压缩的情况
        SizeLimitZipResult ret = zipDirWithSizeLimit(dirTobeZip, tempZipFile, fileSizeLimit, totalSizeLimit);

        if (ret == SizeLimitZipResult.SizeLimitZipResult_NotFound) {
            return ret;
        }

        GzipOneFile(tempZipFile, newGZipFile);
        @SuppressWarnings("unused")
        boolean dret = tempZipFile.delete();

        return ret;
    }

    /**
     * 使用gzip格式压缩一个文件
     *
     * @param resFile
     *            需要压缩的文件（夹）
     * @param zipout
     *            压缩的目的文件
     * @throws java.io.FileNotFoundException
     *             找不到文件时抛出
     * @throws java.io.IOException
     *             当压缩过程出错时抛出
     */
    private static void _gzipFile(File resFile, GZIPOutputStream zipout) throws FileNotFoundException, IOException {

        byte buffer[] = new byte[BUFF_SIZE];
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(resFile), BUFF_SIZE);

        int realLength;
        while ((realLength = in.read(buffer)) != -1) {
            zipout.write(buffer, 0, realLength);
        }
        in.close();
        zipout.flush();
    }

    /**
     * zipDirWithSizeLimit 压缩一个文件夹，限制每个文件的大小和压缩前的总大小
     * 如果单个文件超出大小则不压缩此文件，如果超出总文件大小则只压缩部分文件。 未压缩的文件会被记录到压缩文件的注释中。
     *
     * @param dirTobeZip
     *            待压缩目录
     * @param newZipFile
     *            压缩后生成的文件名，请确保不在压缩目录下
     * @param fileSizeLimit
     *            压缩前单个文件的大小
     * @param totalSizeLimit
     *            压缩前总的文件大小
     * @return SizeLimitZipResult
     * @throws java.io.IOException
     */
    private static SizeLimitZipResult zipDirWithSizeLimit(File dirTobeZip, File newZipFile, long fileSizeLimit,
            long totalSizeLimit) throws IOException {

        SizeLimitZipResult ret = SizeLimitZipResult.SizeLimitZipResult_OK;
        boolean hasFile = false;
        if (dirTobeZip.exists() && dirTobeZip.isDirectory()) {
            File lists[] = dirTobeZip.listFiles();
            hasFile = lists.length > 0;
            if (hasFile) {
                InputStream input = null;

                ZipOutputStream zipOut = null;
                FileOutputStream fOut = new FileOutputStream(newZipFile);
                zipOut = new ZipOutputStream(fOut);

                StringBuilder sb = null;
                boolean needSizeLimit = totalSizeLimit > 0 || fileSizeLimit > 0;

                if (needSizeLimit) {
                    sb = new StringBuilder();
                }
                int currentSize = 0;
                // 设置文件输入流
                // 每一个被压缩的文件都用ZipEntry表示，需要为每一个压缩后的文件设置
                // 过滤掉超出大小上限的文件。将文件摘要记录到Zip的Common里面。
                for (int i = 0; i < lists.length; i++) {
                    File currentFile = lists[i];
                    input = new FileInputStream(currentFile);
                    if (needSizeLimit) {
                        int fsize = input.available();
                        sb.append("[").append(i).append("/").append(lists.length).append("]");
                        sb.append(currentFile.getName());
                        sb.append("(").append(fsize).append(")");

                        if (fsize > fileSizeLimit) {// 单个文件超出大小
                            sb.append("[TOO BIG !!!]\n");
                            ret = SizeLimitZipResult.SizeLimitZipResult_TooBig;
                            continue;
                        } else {
                            if (currentSize + fsize < totalSizeLimit) {
                                currentSize += fsize;
                                sb.append('\n');
                            } else {
                                sb.append("[Tatol BIG !!!]\n");
                                ret = SizeLimitZipResult.SizeLimitZipResult_TooBig;
                                continue;
                            }
                        }
                    }

                    zipOut.putNextEntry(new ZipEntry(currentFile.getName()));
                    int readLen = 0;
                    // 接收输入的数据
                    byte[] buf = new byte[1024];
                    while ((readLen = input.read(buf, 0, 1024)) != -1) {
                        zipOut.write(buf, 0, readLen);
                    }
                    zipOut.closeEntry();
                    input.close();
                }
                if (needSizeLimit) {
                    if (currentSize == 0 && hasFile) {// 如果由于过滤了大文件，一个文件也没有压缩的话写个注释文件进去
                        zipOut.putNextEntry(new ZipEntry("common.txt"));
                        zipOut.write(sb.toString().getBytes());// 压缩输出内容
                        zipOut.closeEntry();
                    }
                    zipOut.setComment(sb.toString());
                }
                zipOut.close();
                fOut.close();
            } else {
                ret = SizeLimitZipResult.SizeLimitZipResult_NotFound;
            }

        } else {
            ret = SizeLimitZipResult.SizeLimitZipResult_NotFound;
        }

        return ret;
    }

    /**
     * 使用gzip格式压缩数据
     * 
     * @param input
     *            待压缩的字节数组
     * @return 压缩后的字节数组
     */
    public static byte[] GZip(byte[] input) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream zipOutputSteam = null;
        InputStream inputStream = null;
        try {
            zipOutputSteam = new GZIPOutputStream(baos);
            inputStream = new BufferedInputStream(new ByteArrayInputStream(input), BUFF_SIZE);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (zipOutputSteam != null)
                    zipOutputSteam.close();
                baos.close();
            } catch (Exception ex) {

            }
        }
        return baos.toByteArray();
    }

    public static void unGzipFile(InputStream inputStream, File destFile) {
        GZIPInputStream gzis = null;
        FileOutputStream out = null;
        try {
            gzis = new GZIPInputStream(inputStream);
            out = new FileOutputStream(destFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = gzis.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (gzis != null) {
                try {
                    gzis.close();
                } catch (Exception e) {
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
    }
}