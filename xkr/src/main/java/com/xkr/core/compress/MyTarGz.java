package com.xkr.core.compress;

import com.xkr.exception.UnArchiverException;
import com.xkr.util.FileUtil;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Objects;

/**
 * @author ben
 */
@Component
public final class MyTarGz extends MyTar {

    private static FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "TAR.GZ打包文件(*.tar.gz)", "tar.gz");


    @Override
    public final void doArchiver(File[] files, String destpath)
            throws IOException {
        super.doArchiver(files, destpath);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(destpath));

        GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(new BufferedOutputStream(new FileOutputStream(destpath + ".gz")));

        byte[] buffer = new byte[1024];
        int read;
        while ((read = bis.read(buffer)) != -1) {
            gcos.write(buffer, 0, read);
        }
        gcos.close();
        bis.close();
    }

    @Override
    public void doUnArchiver(File srcfile, String destpath, String password)
            throws IOException {
        String charset = FileUtil.codeString(srcfile);
        if (Objects.isNull(charset)) {
            throw new UnArchiverException("unknown charset");
        }
        String tarDestPath = srcfile.getParent()+ "/" + System.currentTimeMillis()+srcfile.getName().split("\\.")[0];
        try (BufferedInputStream bis =
                     new BufferedInputStream(new FileInputStream(srcfile));
             BufferedOutputStream bos =
                     new BufferedOutputStream(new FileOutputStream(tarDestPath));
             GzipCompressorInputStream gcis =
                     new GzipCompressorInputStream(bis)) {


            byte[] buffer = new byte[1024];
            int read;
            while ((read = gcis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            gcis.close();
            bos.close();
            super.doUnArchiver(new File(tarDestPath), destpath, password);
        } catch (Exception e) {
            throw new UnArchiverException("unarchiver error",e);
        }
    }

    @Override
    public FileNameExtensionFilter getFileFilter() {
        return filter;
    }
}
