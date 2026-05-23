package com.salem.stockalert.alert;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.model.Symbol;

public interface AlertRule {
    String id();
    Symbol symbol();
    boolean matches(PriceQuote quote);

}
