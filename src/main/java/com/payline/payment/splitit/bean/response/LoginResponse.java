package com.payline.payment.splitit.bean.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("ResponseHeader")
    ResponseHeader responseHeader;
    @SerializedName("SessionId")
    String sessionId;

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public String getSessionId() {
        return sessionId;
    }

}
