
package com.tntrip.understand.generic;

public class ApiResponse<T> {
    private boolean success;
    private String msg;
    private int errorCode;
    private T data;

    public static <T> ApiResponse<T> create(boolean success, String msg, int errorCode, T data) {
        ApiResponse<T> rsp = new ApiResponse<>();
        rsp.setSuccess(success);
        rsp.setMsg(msg);
        rsp.setErrorCode(errorCode);
        rsp.setData(data);
        return rsp;
    }

    public void setup(boolean success, String msg, int errorCode, T data) {
        this.setSuccess(success);
        this.setMsg(msg);
        this.setErrorCode(errorCode);
        this.setData(data);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
