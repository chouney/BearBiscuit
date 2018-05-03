package com.xkr.util.compress;

import com.xkr.common.CompressFileTypeEnum;
import org.apache.commons.compress.archivers.tar.TarUtils;

import java.io.File;
import java.nio.file.Files;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/9
 */
public class UnCompressUtil {


    public static File unCompressFile(CompressFileTypeEnum compressFileTypeEnum, File file) throws Exception {
        if(CompressFileTypeEnum.TAR.equals(compressFileTypeEnum)){
            String sourceFileName = file.getPath();
            File newDir = new File(sourceFileName+"-un");
            newDir.mkdir();
            TarUtil.decompressTarFile(sourceFileName,newDir.getPath());
            return newDir;
        } else if (CompressFileTypeEnum.ZIP.equals(compressFileTypeEnum)) {
            String sourceFileName = file.getPath();
            File newDir = new File(sourceFileName+"-un");
            newDir.mkdir();
            ZipUtil.decompressZipFile(sourceFileName,newDir.getPath());
            return newDir;
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        File zip = new File("g.zip");
        File tar = new File("g.tar");
        File targz = new File("g.tar.gz");
        unCompressFile(CompressFileTypeEnum.ZIP,zip);

        unCompressFile(CompressFileTypeEnum.TAR,tar);
        unCompressFile(CompressFileTypeEnum.TAR,targz);
    }

}
