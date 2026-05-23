package com.salem.stockalert.publisher;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.subscriber.PriceSubscriber;

import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryPricePublisher implements PricePublisher {
    private final CopyOnWriteArrayList<PriceSubscriber> subscribers = new CopyOnWriteArrayList<>();

    // ensures isn't null, then adds subscriber to subscribers list
    @Override
    public void subscribe(PriceSubscriber subscriber){
        if (subscriber == null){
            throw new IllegalArgumentException("subscriber");}
        
        subscribers.addIfAbsent(subscriber);
    }

    // removes subscriber from subscribers list
    @Override
    public void unsubscribe(PriceSubscriber subscriber){
        subscribers.remove(subscriber);
    }

    // ensures isn't null, then publishes price to subscribers
    @Override
    public void publish(PriceQuote quote){
        if (quote == null){
            throw new IllegalArgumentException("quote");}
        
        for (PriceSubscriber s : subscribers){
            s.onPrice(quote);
        }
    }
    
}
