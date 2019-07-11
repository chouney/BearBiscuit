package com.xkr.core;

import com.alibaba.fastjson.JSON;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;
import com.xkr.common.CaptchaEnum;
import com.xkr.common.Const;
import com.xkr.common.ErrorStatus;
import com.xkr.util.DateUtil;
import com.xkr.web.model.BasicResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Properties;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/21
 */
public class CaptchaServlet extends HttpServlet implements Servlet {

    private static final long serialVersionUID = -3487316985986540994L;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    private static final String CHECK_TYPE_KEY_PARAM = "checkType";

    private Properties props = new Properties();
    private Producer kaptchaProducer = null;
    private String sessionKeyValue = null;
    private String sessionKeyDateValue = null;

    public CaptchaServlet() {
    }

    public void init(ServletConfig conf) throws ServletException {
        super.init(conf);
        ImageIO.setUseCache(false);
        Enumeration initParams = conf.getInitParameterNames();

        while (initParams.hasMoreElements()) {
            String key = (String) initParams.nextElement();
            String value = conf.getInitParameter(key);
            this.props.put(key, value);
        }

        Config config = new Config(this.props);
        this.kaptchaProducer = config.getProducerImpl();
        this.sessionKeyValue = Const.CAPTCHA_SESSION_KEY_BASE;
        this.sessionKeyDateValue = Const.CAPTCHA_SESSION_DATE_BASE;
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.setDateHeader("Expires", 0L);
            resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
            resp.setHeader("Pragma", "no-cache");
            resp.setContentType("image/jpeg");
            String param = req.getParameter(CHECK_TYPE_KEY_PARAM);
            if(StringUtils.isEmpty(param)){
                throw new IllegalArgumentException("request param error");
            }
            CaptchaEnum captchaEnum = CaptchaEnum.getByCode(Integer.valueOf(req.getParameter(CHECK_TYPE_KEY_PARAM)));
            if(Objects.isNull(captchaEnum)){
                throw new IllegalArgumentException("request param error");
            }
            String capText = this.kaptchaProducer.createText();
            Session session = SecurityUtils.getSubject().getSession();
            session.setAttribute(this.sessionKeyValue+"_"+session.getId()+"_"+captchaEnum.getCode(), capText);
            session.setAttribute(this.sessionKeyDateValue+"_"+session.getId()+"_"+captchaEnum.getCode(), DateUtils.addSeconds(new Date(),60));
            BufferedImage bi = this.kaptchaProducer.createImage(capText);
            ServletOutputStream out = resp.getOutputStream();
            ImageIO.write(bi, "jpg", out);
        } catch (Exception e) {
            logger.error("CaptchaServlet doGet captcha error :", e);
            resp.setCharacterEncoding("UTF-8");
            resp.setContentType("application/json");
            resp.getWriter().write(JSON.toJSONString(new BasicResult(ErrorStatus.ERROR)));
        }
    }


    public static void main(String[] args){
        Date date = new Date();
        System.out.println(date);
        System.out.println(DateUtils.addSeconds(new Date(),60));
    }
}
