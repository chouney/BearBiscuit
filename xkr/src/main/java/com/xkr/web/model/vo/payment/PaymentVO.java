package com.xkr.web.model.vo.payment;

import com.xkr.domain.dto.BaseDTO;

import java.io.Serializable;

/**
 * @author chriszhang
 * @version 1.0
 * @date 2018/5/11
 */
public class PaymentVO implements Serializable {


    private static final long serialVersionUID = 5919754398552480512L;

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
