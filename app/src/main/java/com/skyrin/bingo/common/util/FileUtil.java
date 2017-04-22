package com.skyrin.bingo.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by 罗延林 on 2016/10/14 0014.
 */

public class FileUtil {
    /**
     * 存储字符串到文件
     *
     * @param file 文件
     * @param inputText 存储内容
     * @return
     */
    public static boolean saveString(File file, String inputText) {
        boolean result = true;
        FileOutputStream out;
        BufferedWriter writer = null;
        try {
            out = new FileOutputStream(file, false);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 读取文件String内容
     *
     * @param file 文件
     * @return
     */
    public static String readString(File file) {
        if (!file.exists()) {
            return null;
        }
        FileInputStream in;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    /**
     * 文件是否存在
     * @param path 文件路径
     * @return
     */
    public static boolean isFileExist(String path){
        File file = new File(path);
        return file.exists();
    }
}
