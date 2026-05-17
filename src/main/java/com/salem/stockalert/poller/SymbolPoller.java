package com.salem.stockalert.poller;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.model.Symbol;
import com.salem.stockalert.provider.PriceDataProvider;
import com.salem.stockalert.publisher.PricePublisher;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

public class SymbolPoller implements Runnable {
    private final Symbol symbol;
    private final PriceDataProvider provider;
    private final PricePublisher publisher;
    private final Duration interval;
    private final AtomicBoolean running = new AtomicBoolean(true);
    
    public SymbolPoller(Symbol symbol, PriceDataProvider provider, PricePublisher publisher, Duration interval){
        this.symbol = symbol;
        this.provider = provider;
        this.publisher = publisher;
        this.interval = interval;

        if (symbol==null||provider== null||publisher==null||interval==null){
            throw new IllegalArgumentException();
        }
        
        if (interval.isZero()||interval.isNegative()){
            throw new IllegalArgumentException("interval must be > 0");
        }
    }

    public void run(){
        while (running.get()){
            try{
                PriceQuote quote = provider.fetch(symbol);
                publisher.publish(quote);
                
                Thread.sleep(interval.toMillis());
            }
            catch (InterruptedException e){
                // Usually means: shutdown requested
                Thread.currentThread().interrupt(); // preserve interrupt status
                break;
            }
            catch (Exception e){
                // Provider/publish failed; log and keep going
                System.err.println("[" + name() + "] error: " + e.getMessage());
            }            
        }    
    }
    
    public void stop(){
        running.set(false);
    }

    public String name(){
        return "poller-" + symbol.getTicker();
    }

}
