package com.salem.stockalert.provider;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.model.Symbol;

public interface PriceDataProvider {
    PriceQuote fetch(Symbol symbol);
    
}
