package org.hao.Client.Request;

/**
 * Message for response from Server to Client
 * @author Ninghao Zhu 1446180
 */
public class Response {
    private boolean success;
    private String message;

    public Response() {}

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "success=" + success + ", message=" + message;
    }
}
