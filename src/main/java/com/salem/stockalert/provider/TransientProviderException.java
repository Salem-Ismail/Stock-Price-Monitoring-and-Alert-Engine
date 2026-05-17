package com.salem.stockalert.provider;

public class TransientProviderException extends RuntimeException {
    public TransientProviderException(String message, Throwable cause){
        super(message, cause);
    }
    public TransientProviderException(String message){
        super(message);
    }
}
