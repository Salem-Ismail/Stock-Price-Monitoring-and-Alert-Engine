package com.salem.stockalert.provider;

public class FatalProviderException extends RuntimeException {
    public FatalProviderException(String message, Throwable cause){
        super(message, cause);
    }
    public FatalProviderException(String message){
        super(message);
    }
    
}
