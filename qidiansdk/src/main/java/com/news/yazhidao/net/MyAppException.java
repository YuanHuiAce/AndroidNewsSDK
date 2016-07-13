package com.news.yazhidao.net;

/**
 * Created by fengjigang on 15/1/8.
 */
public class MyAppException extends Exception {
    private  ExceptionStatus exceptionStatus;

    public enum ExceptionStatus{
        IOException, ServerException, TimeOutException, CanceledException

    }
    public MyAppException(ExceptionStatus status,String detailMessage) {
        super(detailMessage);
        this.exceptionStatus=status;
    }
    public ExceptionStatus getExceptionStatus(){
        return exceptionStatus;
    }

}
