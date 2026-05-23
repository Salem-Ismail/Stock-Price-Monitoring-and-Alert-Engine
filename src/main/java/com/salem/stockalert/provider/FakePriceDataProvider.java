package com.salem.stockalert.provider;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.model.Symbol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fake provider for testing: generates a simple random-walk price per symbol.
 */
public final class FakePriceDataProvider implements PriceDataProvider {
    private final ConcurrentHashMap<Symbol, BigDecimal> lastPrices = new ConcurrentHashMap<>();

    @Override
    public PriceQuote fetch(Symbol symbol) {
        if (symbol == null) throw new IllegalArgumentException("symbol");

        BigDecimal next = lastPrices.compute(symbol, (s, prev) -> {
            BigDecimal base = (prev == null) ? new BigDecimal("100.00") : prev;
            double delta = ThreadLocalRandom.current().nextDouble(-1.0, 1.0); // [-1.00, +1.00)
            BigDecimal updated = base.add(BigDecimal.valueOf(delta));
            if (updated.signum() <= 0) updated = new BigDecimal("0.01");
            return updated.setScale(2, RoundingMode.HALF_UP);
        });

        return new PriceQuote(symbol, next, Instant.now());
    }
}
