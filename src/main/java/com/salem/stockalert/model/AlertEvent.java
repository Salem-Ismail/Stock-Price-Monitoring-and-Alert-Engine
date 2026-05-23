package com.salem.stockalert.model;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;


public final class AlertEvent {
    private final String ruleId;
    private final Symbol symbol;
    private final BigDecimal price;
    private final Instant asOf;
    private final Instant triggeredAt;

    public AlertEvent(String ruleId, Symbol symbol, BigDecimal price, Instant asOf, Instant triggeredAt){
        this.ruleId = ruleId;
        this.symbol = symbol;
        this.price = price;
        this.asOf = asOf;
        this.triggeredAt = triggeredAt;
    }

    // getters:
    public String getRuleId() {
        return ruleId;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Instant getAsOf() {
        return asOf;
    }

    public Instant getTriggeredAt() {
        return triggeredAt;
    }

    // lifecycle methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertEvent that = (AlertEvent) o;
        return Objects.equals(ruleId, that.ruleId)
                && Objects.equals(symbol, that.symbol)
                && Objects.equals(price, that.price)
                && Objects.equals(asOf, that.asOf)
                && Objects.equals(triggeredAt, that.triggeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleId, symbol, price, asOf, triggeredAt);
    }

    @Override
    public String toString() {
        return "AlertEvent{" +
                "ruleId='" + ruleId + '\'' +
                ", symbol=" + symbol +
                ", price=" + price +
                ", asOf=" + asOf +
                ", triggeredAt=" + triggeredAt +
                '}';
    }
}
