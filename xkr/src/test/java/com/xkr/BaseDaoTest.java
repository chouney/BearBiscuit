package com.xkr;

import com.xkr.core.config.MyBatisConfig;
import com.xkr.core.config.MyBatisMapperScannerConfig;
import junit.framework.TestCase;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * author xkr
 * date 2016/10/21 0021 下午 16:54
 */
@RunWith(SpringRunner.class)
/**
 *  这里指定的classes是可选的。如果不指定classes，则spring boot会启动整个spring容器，很慢（比如说会执行一些初始化，ApplicationRunner、CommandLineRunner等等）。不推荐
 *  指定的话，就只会初始化指定的bean，速度快，推荐
 */
@SpringBootTest(classes={DataSourceAutoConfiguration.class, MyBatisConfig.class, MyBatisMapperScannerConfig.class})
public class BaseDaoTest extends TestCase {
}
