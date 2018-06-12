package com.xkr.service.api;

import com.xkr.util.DateUtil;
import com.xkr.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/15
 */
@Service
public class DbBackUpApiService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * MySQL安装目录的Bin目录的绝对路径
     */
    @Value("${mysql.bin.path}")
    private String mysqlBinPath;

    /**
     * 访问MySQL的备份文件夹
     */
    @Value("${mysql.backup.path}")
    private String backupPath;

    /**
     * 访问MySQL数据库的用户名
     */
    @Value("${spring.datasource.username}")
    private String username;

    /**
     * 访问MySQL数据库的密码
     */
    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void init() {
        if (!mysqlBinPath.endsWith(File.separator)) {
            mysqlBinPath = mysqlBinPath + File.separator;
        }
        if (!backupPath.endsWith(File.separator)) {
            backupPath = backupPath + File.separator;
        }
    }

    /**
     * 备份数据库，如果指定路径的文件不存在会自动生成
     *
     * @param dest 备份文件
     * @param dbname   要备份的数据库
     * @return 返回文件本地路径
     */
    public String backup(String dest, String dbname) {
        if (StringUtils.isEmpty(dbname)) {
            dbname = "--all-databases";
        }
        String[] commands = new String[]{
                mysqlBinPath + "mysqldump", "-u" + username, "-p" + password, dbname, "-r" + dest, "--default-character-set=utf8"
        };
        try {
            Process process = Runtime.getRuntime().exec(commands);
            int processComplete = process.waitFor();
            if (processComplete == 0) {
                logger.info("backup success dest:{},dbname:{}", dest, dbname);
                return dest;
            }
            StringBuilder stb = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))){
                reader.lines().forEach(stb::append);
                logger.error("backup failed failed info:{}",stb.toString());
            }catch (IOException e){
                logger.error("backup failed failed info:{}, error:{}",stb.toString(),e);
            }
        } catch (IOException | InterruptedException e) {
            logger.error("backup failed dest:{},dbname:[],error: ", dest, dbname, e);
        }
        return null;
    }

    /**
     * 恢复数据库
     *
     * @param dest 备份文件路径
     */
    public boolean restore(String dest) {
        if(StringUtils.isEmpty(dest)){
            return false;
        }
        try {
            String[] commands = new String[]{
                    mysqlBinPath + "mysql", "-u" + username, "-p" + password, "-e source " + dest
            };
            Process process = Runtime.getRuntime().exec(commands);

            int processComplete = process.waitFor();
            if (processComplete == 0) {
                logger.info("restore success dest:{}", dest);
                return true;
            }
            StringBuilder stb = new StringBuilder();
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))){
                reader.lines().forEach(stb::append);
                logger.error("restore failed failed failedInfo:{}",stb.toString());
            }catch (IOException e){
                logger.error("restore failed info:{}, error:{}",stb.toString(),e);
            }
        } catch (InterruptedException | IOException e) {
            logger.error("restore failed dest:{}, error: ", dest, e);
        }
        return false;
    }

    public String getBackupPath() {
        return backupPath;
    }

//    public void test(){
//        System.out.println("测试");
//    }

    public static void main(String[] args) {
        DbBackUpApiService dbBackUpApiService = new DbBackUpApiService();
        dbBackUpApiService.mysqlBinPath = "/usr/local/mysql/bin";
        dbBackUpApiService.backupPath = "/Users/chriszhang/mysql_backup";
        dbBackUpApiService.username = "root";
        dbBackUpApiService.password = "123456";
        dbBackUpApiService.init();
        dbBackUpApiService.restore( "db.bak");
//        dbBackUpService.backup("db.bak", null);
    }
}
