package com.salem.stockalert;

import com.salem.stockalert.alert.AlertRule;
import com.salem.stockalert.alert.AlertSink;
import com.salem.stockalert.alert.LoggingAlertSink;
import com.salem.stockalert.alert.PriceThresholdRule;
import com.salem.stockalert.model.Symbol;
import com.salem.stockalert.poller.SymbolPoller;
import com.salem.stockalert.provider.FinnhubPriceDataProvider;
import com.salem.stockalert.provider.PriceDataProvider;
import com.salem.stockalert.provider.RetryingPriceDataProvider;
import com.salem.stockalert.publisher.InMemoryPricePublisher;
import com.salem.stockalert.publisher.PricePublisher;
import com.salem.stockalert.subscriber.AlertEvaluatorSubscriber;
import com.salem.stockalert.subscriber.LoggingPriceSubscriber;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Application entry point: polls symbols, publishes price updates, and evaluates
 * threshold alert rules. Stops cleanly on Ctrl+C via a shutdown hook.
 */
public final class App {
    public static void main(String[] args) {
        PricePublisher publisher = new InMemoryPricePublisher();
        publisher.subscribe(new LoggingPriceSubscriber());

        PriceDataProvider provider = new RetryingPriceDataProvider(
            new FinnhubPriceDataProvider(),
            5,
            Duration.ofMillis(250),
            Duration.ofSeconds(5)
        );
        Duration interval = Duration.ofSeconds(6);

        List<SymbolPoller> pollers = List.of(
            new SymbolPoller(new Symbol("AAPL"), provider, publisher, interval),
            new SymbolPoller(new Symbol("MSFT"), provider, publisher, interval)
        );

        // alerts go here (for now: print to console)
        AlertSink sink = new LoggingAlertSink();

        // demo rule: trigger once when aapl crosses ABOVE 274.00
        List<AlertRule> rules = List.of(
            new PriceThresholdRule(
                "aapl-above-274",
                new Symbol("AAPL"),
                new BigDecimal("274.00"),
                PriceThresholdRule.Direction.ABOVE
            )
        );

        // subscribe alert evaluator to price updates (observer pattern)
        publisher.subscribe(new AlertEvaluatorSubscriber(rules, sink));

        ExecutorService exec = Executors.newFixedThreadPool(pollers.size());
        pollers.forEach(exec::execute);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            pollers.forEach(SymbolPoller::stop);
            exec.shutdownNow(); // interrupts sleeping threads
            try {
                exec.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));

        System.out.println("Running pollers. Press Ctrl+C to stop.");
    }
}
