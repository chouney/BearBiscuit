package com.xkr.util;

import com.xkr.common.FileTypeEnum;

import java.io.*;
import java.util.Objects;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/10
 */
public class FileUtil {

    /**
     * 判断文件的编码格式
     * @param file :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(File file) {
        try(BufferedInputStream bin = new BufferedInputStream(
                new FileInputStream(file))) {
            int p = (bin.read() << 8) + bin.read();
            String code = null;

            switch (p) {
                case 0xefbb:
                    code = "UTF-8";
                    break;
                case 0xfffe:
                    code = "Unicode";
                    break;
                case 0xfeff:
                    code = "UTF-16BE";
                    break;
                default:
                    code = "GBK";
            }
            return code;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static FileTypeEnum getFileType(File file){
        if(Objects.isNull(file)){
            return null;
        }
        if(file.isDirectory()){
            return FileTypeEnum.DIR;
        }
        String fileName = file.getName();
        int firstDotIndex = fileName.indexOf(".")+1;
        if(firstDotIndex == -1){
            return null;
        }
        String fullPostfixName = fileName.substring(firstDotIndex).toLowerCase();
        return FileTypeEnum.getEnumByFileDesc(fullPostfixName);
    }

}
