/*
 * 文件名：		MyTar.java
 * 创建日期：	2013-7-23
 * 最近修改：	2013-7-23
 * 作者：		徐犇
 */
package com.xkr.core.compress;

import com.xkr.exception.UnArchiverException;
import com.xkr.util.FileUtil;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.Objects;

/**
 * @author ben
 */
@Component
public final class MyTar extends ArchiveProcessor {

    private static FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "TAR打包文件(*.tar)", "tar");

    private void dfs(File[] files, TarArchiveOutputStream taos, String fpath)
            throws IOException {
        byte[] buf = new byte[1024];
        for (File child : files) {
            if (child.isFile()) { // 文件
                FileInputStream fis = new FileInputStream(child);
                BufferedInputStream bis = new BufferedInputStream(fis);
                TarArchiveEntry tae = new TarArchiveEntry(fpath
                        + child.getName());
                tae.setSize(child.length());
                taos.putArchiveEntry(tae);
                int len;
                while ((len = bis.read(buf)) > 0) {
                    taos.write(buf, 0, len);
                }
                bis.close();
                taos.closeArchiveEntry();
                continue;
            }
            File[] fs = child.listFiles();
            String nfpath = fpath + child.getName() + "/";
            if (fs.length <= 0) { // 空目录
                taos.putArchiveEntry(new TarArchiveEntry(nfpath));
                taos.closeArchiveEntry();
            } else { // 目录非空，递归处理
                dfs(fs, taos, nfpath);
            }
        }
    }

    @Override
    public final void doArchiver(File[] files, String destpath)
            throws IOException {
        /*
		 * 定义一个TarArchiveOutputStream 对象
		 */
        FileOutputStream fos = new FileOutputStream(destpath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        TarArchiveOutputStream taos = new TarArchiveOutputStream(bos);
        dfs(files, taos, "");
        taos.flush();
        taos.close();
    }

    @Override
    public void doUnArchiver(File srcfile, String destpath, String password)
            throws IOException {
        byte[] buf = new byte[1024];
        String charset = FileUtil.codeString(srcfile);
        if (Objects.isNull(charset)) {
            throw new UnArchiverException("unknown charset");
        }
        try (TarArchiveInputStream tais = new TarArchiveInputStream(
                new BufferedInputStream(new FileInputStream(srcfile)),
                charset
        )) {
            TarArchiveEntry tae = null;
            while ((tae = tais.getNextTarEntry()) != null) {
                File f = new File(destpath + "/" + tae.getName());
                if (tae.isDirectory()) {
                    f.mkdirs();
                } else {
				/*
				 * 父目录不存在则创建
				 */
                    File parent = f.getParentFile();
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    FileOutputStream fos = new FileOutputStream(f);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    int len;
                    while ((len = tais.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    bos.flush();
                    bos.close();
                }
            }
            tais.close();
        } catch (Exception e) {
            throw new UnArchiverException("unarchiver error",e);
        }
    }

    @Override
    public final FileNameExtensionFilter getFileFilter() {
        return filter;
    }
}
