package com.xkr.core.compress;

import com.xkr.util.CamelCaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * 解压缩封装类
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/9
 */
@Component
public class UnCompressProcessorFacade implements ApplicationContextAware {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ApplicationContext context;

    public File unCompressFile(File file, Class clazz) {
        if (Objects.isNull(clazz) || Objects.isNull(file)) {
            return null;
        }
        try {
            String parent = file.getParent() == null ? "" : file.getParent() + "/";
            String desFilePath = parent + System.currentTimeMillis()+file.getName().split("\\.")[0];
            if (ArchiveProcessor.class.isAssignableFrom(clazz)) {
                ArchiveProcessor archiveProcessor = context.getBean(CamelCaseUtil.toLowwerCamelCase(clazz.getSimpleName()), ArchiveProcessor.class);
                archiveProcessor.doUnArchiver(file, desFilePath, null);
                return new File(desFilePath);
            } else if (Compressor.class.isAssignableFrom(clazz)) {
                Compressor compressor = context.getBean(CamelCaseUtil.toLowwerCamelCase(clazz.getSimpleName()), Compressor.class);
                compressor.doUnCompress(file, desFilePath);
                return new File(desFilePath);
            }
        } catch (Exception e) {
            logger.error("解压缩文件失败 fileName:{}",file.getName(),e);
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
