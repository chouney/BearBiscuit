package com.xkr.service.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/15
 */
@Service
public class DbBackUpService {

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
     * @param backFile   备份文件
     * @param dbname 要备份的数据库
     */
    public void backup(String backFile, String dbname) {
        if (StringUtils.isEmpty(dbname)) {
            dbname = "--all-databases";
        }
        String dest = backFile+backupPath;
        String command = mysqlBinPath + "mysqldump -u" + username
                + " -p" + password + " " + dbname + " -r " + dest + " --set-charset=utf8 ";
        String[] commands = new String[]{
                mysqlBinPath + "mysqldump", "-u"+username , "-p"+password,  dbname , "-r " + dest,"--set-charset=utf8"
        } ;
        try {
            Process process = Runtime.getRuntime().exec(command);

            int processComplete = process.waitFor();
            if (processComplete == 0) {
                System.out.println("备份成功.");
            } else {
                throw new RuntimeException("备份数据库失败.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 恢复数据库
     *
     * @param backFile 备份文件
     */
    public void restore(String backFile) {
        try {
            String dest = backFile+backupPath;
            String command = mysqlBinPath + "mysql -u" + username
                    + " -p" + password + " " + "-e 'source " + dest + "'";
            String[] commands = new String[]{
                mysqlBinPath + "mysql", "-u"+username, "-p"+password, "-e source "+dest
            } ;
            Process process = Runtime.getRuntime().exec(commands);

            int processComplete = process.waitFor();
            if (processComplete == 0) {
                System.out.println("还原成功.");
            } else {
                throw new RuntimeException("还原数据库失败.");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DbBackUpService dbBackUpService = new DbBackUpService();
        dbBackUpService.mysqlBinPath = "/usr/local/mysql/bin";
        dbBackUpService.backupPath = "/Users/chriszhang/mysql_backup";
        dbBackUpService.username = "root";
        dbBackUpService.password = "123456";
        dbBackUpService.init();
        dbBackUpService.restore( "db.bak");
//        dbBackUpService.backup(dbBackUpService.backupPath+"db.bak",null);
    }
}
