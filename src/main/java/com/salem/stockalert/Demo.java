package com.salem.stockalert;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.model.Symbol;
import com.salem.stockalert.publisher.InMemoryPricePublisher;
import com.salem.stockalert.publisher.PricePublisher;
import com.salem.stockalert.subscriber.LoggingPriceSubscriber;
import com.salem.stockalert.subscriber.PriceSubscriber;

import java.math.BigDecimal;
import java.time.Instant;

public final class Demo {
    public static void main(String[] args) {
        PricePublisher publisher = new InMemoryPricePublisher();
        PriceSubscriber subscriber = new LoggingPriceSubscriber();

        publisher.subscribe(subscriber);

        PriceQuote quote = new PriceQuote(
            new Symbol("AAPL"),
            new BigDecimal("250.12"),
            Instant.now()
        );

        publisher.publish(quote);
    }
}
