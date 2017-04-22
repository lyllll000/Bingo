package com.skyrin.bingo.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 压缩文件工具类
 */
public class ZIP {
    /**
     * 解压文件
     * @param str
     * @param str2
     * @return
     * @throws Exception
     */
    public static String UnZipFolder(String str, String str2) throws Exception {
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(str));
        String str3 = "";
        while (true) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            if (nextEntry == null) {
                zipInputStream.close();
                return str3;
            }
            str3 = nextEntry.getName();
            if (nextEntry.isDirectory()) {
                str3 = str3.substring(0, str3.length() - 1);
                new File(new StringBuilder(String.valueOf(str2)).append(File.separator).append(str3).toString()).mkdirs();
            } else {
                File file = new File(new StringBuilder(String.valueOf(str2)).append(File.separator).append(str3).toString());
                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[1024];
                while (true) {
                    int read = zipInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    fileOutputStream.write(bArr, 0, read);
                    fileOutputStream.flush();
                }
                fileOutputStream.close();
            }
        }
    }
}
