package com.salem.stockalert.model;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;


public final class PriceQuote {
    private final Symbol symbol;
    private final BigDecimal price;
    private final Instant asOf;
    
    public PriceQuote(Symbol symbol, BigDecimal price, Instant asOf){
        // validating
        if (symbol == null){
            throw new IllegalArgumentException("symbol");
        }
        if (price == null){
            throw new IllegalArgumentException("price");
        }
        if (price.signum() < 0){
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (asOf == null){
            throw new IllegalArgumentException("asOf");
        }
        
        // assigning
        this.symbol = symbol;
        this.price = price;
        this.asOf = asOf;
    }
    
    // getters
    public final Symbol getSymbol(){
        return this.symbol;
    }
    public final BigDecimal getPrice(){
        return this.price;
    }
    public final Instant getAsOf(){
        return this.asOf;
    }

    // lifecycle methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceQuote that = (PriceQuote) o;
        return Objects.equals(symbol, that.symbol)
                && Objects.equals(price, that.price)
                && Objects.equals(asOf, that.asOf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, price, asOf);
    }

    @Override
    public String toString() {
        return "PriceQuote{" +
                "symbol=" + symbol +
                ", price=" + price +
                ", asOf=" + asOf +
                '}';
    }
}
