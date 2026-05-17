package com.salem.stockalert.subscriber;

import com.salem.stockalert.alert.AlertRule;
import com.salem.stockalert.alert.AlertSink;
import com.salem.stockalert.alert.PriceThresholdRule;
import com.salem.stockalert.model.AlertEvent;
import com.salem.stockalert.model.PriceQuote;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AlertEvaluatorSubscriber implements PriceSubscriber {
    private final List<AlertRule> rules;
    private final AlertSink sink;
    private final ConcurrentHashMap<String, BigDecimal> lastPriceByRuleId = new ConcurrentHashMap<>();

    public AlertEvaluatorSubscriber(List<AlertRule> rules, AlertSink sink){
        if (rules == null || sink == null){
            throw new IllegalArgumentException();
        }
        this.rules = List.copyOf(rules);
        this.sink = sink;
    }

    @Override
    public void onPrice(PriceQuote quote) {
        if (quote == null) return;

        for (AlertRule rule : rules) {
            // only evaluate rules for this symbol
            if (!rule.symbol().equals(quote.getSymbol())) continue;

            // previous price for crossing detection
            BigDecimal prev = lastPriceByRuleId.get(rule.id());
            boolean isTrueNow = rule.matches(quote);

            // first observation: store and move on (no crossing yet)
            if (prev == null) {
                lastPriceByRuleId.put(rule.id(), quote.getPrice());
                continue;
            }

            // compute whether it was true before (based on prev price)
            boolean wasTrueBefore;
            if (rule instanceof PriceThresholdRule tr) {
                int prevCmp = prev.compareTo(tr.getThreshold());
                wasTrueBefore = (tr.getDirection() == PriceThresholdRule.Direction.ABOVE)
                    ? (prevCmp > 0)
                    : (prevCmp < 0);
            } else {
                // unsupported rule type: don't fire
                wasTrueBefore = true;
            }

            // fire only on false -> true transition
            if (!wasTrueBefore && isTrueNow) {
                AlertEvent event = new AlertEvent(
                    rule.id(),
                    quote.getSymbol(),
                    quote.getPrice(),
                    quote.getAsOf(),
                    java.time.Instant.now()
                );
                sink.publish(event);
            }

            // update last seen price
            lastPriceByRuleId.put(rule.id(), quote.getPrice());
        }
    }


}
