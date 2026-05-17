package com.salem.stockalert.alert;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.model.Symbol;

import java.math.BigDecimal;


public class PriceThresholdRule implements AlertRule { 
    public enum Direction{
        ABOVE,
        BELOW
    }
    private final String id;
    private final Symbol symbol;
    private final BigDecimal threshold;
    private final Direction direction;

    @Override
    public String id(){
        return id;
    }

    @Override
    public Symbol symbol(){
        return symbol;
    }

    public PriceThresholdRule(String id, Symbol symbol, BigDecimal threshold, Direction direction){
        // validating
        if (id.isBlank()){
            throw new IllegalArgumentException("id");
        }
        if (symbol == null){
            throw new IllegalArgumentException("symbol");
        }
        if (threshold == null){
            throw new IllegalArgumentException("threshold");
        }        
        if (direction == null){
            throw new IllegalArgumentException("direction");
        }

        // assigning
        this.id = id;
        this.symbol = symbol;
        this.threshold = threshold;
        this.direction = direction;
    }

    public BigDecimal getThreshold(){
        return this.threshold;
    }

    public Direction getDirection(){
        return this.direction;
    }    

    @Override
    public boolean matches(PriceQuote quote){
        // quote is null
        if (quote == null){
            return false; 
        }

        // symbols do not match
        if (!symbol.equals(quote.getSymbol())){
            return false; 
        }

        // comparing price to threshold: 0 if equal, -1 if price greater, and 1 if threshold greater
        int cmp = quote.getPrice().compareTo(threshold);

        // above: returns true when quoteprice above alert threshold
        if (direction == Direction.ABOVE){
            return cmp > 0;
        }
        // below: returns true when quoteprice below alert threshold
        else{
            return cmp < 0;
        }

        
    }

}
