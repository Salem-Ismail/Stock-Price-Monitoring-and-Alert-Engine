package com.salem.stockalert.publisher;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.subscriber.PriceSubscriber;

public interface PricePublisher {
    void subscribe(PriceSubscriber subscriber);
    void unsubscribe(PriceSubscriber subscriber);
    void publish(PriceQuote quote);
    
}
