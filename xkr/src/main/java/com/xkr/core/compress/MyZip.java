/*
 * 文件名：		MyGZip.java
 * 创建日期：	2013-7-22
 * 最近修改：	2013-7-22
 * 作者：		徐犇
 */

package com.xkr.core.compress;

import com.xkr.exception.UnArchiverException;
import com.xkr.util.FileUtil;
import org.springframework.stereotype.Component;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 使用java原生方法处理ZIP文件
 * 
 * @author ben
 */
@Component
public final class MyZip extends ArchiveProcessor {

	/**
	 * zip文件格式的过滤器
	 */
	private static FileNameExtensionFilter filter = new FileNameExtensionFilter(
			"ZIP压缩文件(*.zip)", "zip");

	private void dfs(File[] files, ZipOutputStream zos, String fpath)
			throws IOException {
		byte[] buf = new byte[1024];
		for (File child : files) {
			if (child.isFile()) { // 文件
				FileInputStream fis = new FileInputStream(child);
				BufferedInputStream bis = new BufferedInputStream(fis);
				zos.putNextEntry(new ZipEntry(fpath + child.getName()));
				int len;
				while((len = bis.read(buf)) > 0) {
					zos.write(buf, 0, len);
				}
				bis.close();
				zos.closeEntry();
				continue;
			}
			File[] fs = child.listFiles();
			String nfpath = fpath + child.getName() + "/";
			if (fs.length <= 0) { // 空目录
				zos.putNextEntry(new ZipEntry(nfpath));
				zos.closeEntry();
			} else { // 目录非空，递归处理
				dfs(fs, zos, nfpath);
			}
		}
	}

	@Override
	public final void doArchiver(File[] files, String destpath)
			throws IOException {
		/*
		 * 定义一个ZipOutputStream 对象
		 */
		FileOutputStream fos = new FileOutputStream(destpath);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		ZipOutputStream zos = new ZipOutputStream(bos);
		dfs(files, zos, "");
		zos.flush();
		zos.close();
	}

	@Override
	public final void doUnArchiver(File srcfile, String destpath,
			String password) throws IOException {
		byte[] buf = new byte[1024];
		String charset = FileUtil.codeString(srcfile);
		if(Objects.isNull(charset)){
			throw new UnArchiverException("unknown charset");
		}
		try(ZipInputStream zis = new ZipInputStream(
				new BufferedInputStream(
						new FileInputStream(srcfile)),Charset.forName(charset))) {
			ZipEntry zn = null;
			while ((zn = zis.getNextEntry()) != null) {
				File f = new File(destpath + "/" + zn.getName());
				if (zn.isDirectory()) {
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
					while ((len = zis.read(buf)) != -1) {
						bos.write(buf, 0, len);
					}
					bos.flush();
				}
				zis.closeEntry();
			}
		}catch (Exception e){
			throw new UnArchiverException("unarchiver error" ,e);
		}
	}

	@Override
	public final FileNameExtensionFilter getFileFilter() {
		return filter;
	}

}
