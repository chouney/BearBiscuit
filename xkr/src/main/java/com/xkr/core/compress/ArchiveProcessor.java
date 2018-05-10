/*
 * 文件名：		Archiver.java
 * 创建日期：	2013-7-22
 * 最近修改：	2013-7-22
 * 作者：		徐犇
 */
package com.xkr.core.compress;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

/**
 * @author ben
 *
 */
public abstract class ArchiveProcessor {
	
	/**
	 * 打包或压缩文件
	 * @param files 需要打包和压缩的文件数组
	 * @param destpath 目标文件路径
	 * @throws IOException
	 */
	public abstract void doArchiver(File[] files, String destpath) throws IOException;
	
	/**
	 * 解压或解包文件
	 * @param srcfile 需要解压或解包的源文件
	 * @param destpath 目标路径
	 * @throws IOException
	 */
	public abstract void doUnArchiver(File srcfile, String destpath, String password) throws IOException;
		
	/**
	 * @return 本归档类对应文件的文件过滤器
	 */
	public abstract FileNameExtensionFilter getFileFilter();
		
}
