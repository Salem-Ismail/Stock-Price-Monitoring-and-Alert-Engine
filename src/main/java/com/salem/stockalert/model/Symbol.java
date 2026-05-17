package com.salem.stockalert.model;
import java.util.Objects;

public final class Symbol{
    private final String ticker;

    public Symbol(String ticker){
        if (ticker == null || ticker.isBlank()){
            throw new IllegalArgumentException("ticker");
        }
        this.ticker = ticker.toUpperCase();
    }

    public String getTicker(){
        return this.ticker;
    }

    // lifecycle methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return Objects.equals(ticker, symbol.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "ticker='" + ticker + '\'' +
                '}';
    }
}