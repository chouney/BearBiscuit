///*
// * 文件名：		MyGZip.java
// * 创建日期：	2013-7-22
// * 最近修改：	2013-7-22
// * 作者：		徐犇
// */
//package com.xkr.core.compress;
//
//import org.springframework.stereotype.Component;
//
//import javax.swing.filechooser.FileNameExtensionFilter;
//import java.io.*;
//import java.util.zip.GZIPInputStream;
//import java.util.zip.GZIPOutputStream;
//
///**
// *
// * @author ben
// *
// */
//@Component
//public final class MyGZip extends Compressor {
//
//	private static FileNameExtensionFilter filter = new FileNameExtensionFilter(
//			"GZIP压缩文件(*.gz)", "gz");
//
//	@Override
//	public final void doCompress(File file, String destpath) throws IOException {
//		FileInputStream fis = new FileInputStream(file);
//		BufferedInputStream bis = new BufferedInputStream(fis);
//
//		File gf = new File(destpath);
//		FileOutputStream fos = new FileOutputStream(gf);
//		GZIPOutputStream gzos = new GZIPOutputStream(fos);
//		BufferedOutputStream bos = new BufferedOutputStream(gzos);
//		readAndWrite(bis, bos);
//	}
//
//	@Override
//	public final void doUnCompress(File srcFile, String destpath) throws IOException {
//		FileInputStream fis = new FileInputStream(srcFile);
//		GZIPInputStream gzis = new GZIPInputStream(fis);
//		BufferedInputStream bis = new BufferedInputStream(gzis);
//
//		File tf = new File(destpath);
//		FileOutputStream fos = new FileOutputStream(tf);
//		BufferedOutputStream bos = new BufferedOutputStream(fos);
//		readAndWrite(bis, bos);
//	}
//
//	@Override
//	public final FileNameExtensionFilter getFileFilter() {
//		return filter;
//	}
//}
