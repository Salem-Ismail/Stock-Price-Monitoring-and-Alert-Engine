package com.salem.stockalert.subscriber;

import com.salem.stockalert.model.PriceQuote;

public interface PriceSubscriber {
    void onPrice(PriceQuote quote);
    
}
