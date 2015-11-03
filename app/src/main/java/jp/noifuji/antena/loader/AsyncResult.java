package jp.noifuji.antena.loader;

/**
 * Created by ryoma on 2015/11/02.
 */
public class AsyncResult<D> {
    private Exception exception;
    private String errorMessage;
    private D data;

    public void setException(Exception exception, String message) {
        this.exception = exception;
        this.errorMessage = message;

    }

    public Exception getException() {
        return exception;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setData(D data) {
        this.data = data;
    }

    public D getData() {
        return data;
    }

}
