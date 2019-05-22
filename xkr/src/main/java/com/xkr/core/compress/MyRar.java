/*
 * 文件名：		MyRar.java
 * 创建日期：	2013-7-22
 * 最近修改：	2013-7-22
 * 作者：		徐犇
 */
package com.xkr.core.compress;

import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author ben
 *
 */
@Component
public final class MyRar extends ArchiveProcessor {



	private static FileNameExtensionFilter filter = new FileNameExtensionFilter(
			"RAR压缩文件(*.rar)", "rar");

	@Override
	public final void doArchiver(File[] files, String destpath)
			throws IOException {
	}


	@Override
	public final void doUnArchiver(File srcfile, String destpath,
			String password) throws IOException {
		try {
			File dest = new File(destpath);
			if(!dest.exists()){
				dest.mkdir();
			}
			List<File> res = Junrar.extract(srcfile.getPath(),destpath);
			if(CollectionUtils.isEmpty(res)){
				throw new RuntimeException("unarchive error 解压缩");
			}

		} catch (RarException e) {
			throw new RuntimeException("unarchive error",e);
		}
	}

	@Override
	public final FileNameExtensionFilter getFileFilter() {
		return filter;
	}

}
