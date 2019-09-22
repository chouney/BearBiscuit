package com.xkr.domain.dto.payment;

import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class PaymentDTO extends BaseDTO implements Serializable {


    private static final long serialVersionUID = 6151813980293642191L;


    private String url;

    private String html;

    private String codeUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }
}
