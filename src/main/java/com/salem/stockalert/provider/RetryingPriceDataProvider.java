package com.salem.stockalert.provider;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.model.Symbol;

import java.time.Duration;

public class RetryingPriceDataProvider implements PriceDataProvider {
    private PriceDataProvider delegate;
    private int maxAttempts; // max total tries before giving up (including 1st)
    private Duration baseDelay; // starting wait time after 1st failure
    private Duration maxDelay; // max wait time between tries
    
    public RetryingPriceDataProvider(PriceDataProvider delegate, int maxAttempts, Duration baseDelay, Duration maxDelay){
        if (delegate == null){throw new IllegalArgumentException("delegate");}
        if (maxAttempts < 1){throw new IllegalArgumentException("maxAttempts");}
        if (baseDelay == null || baseDelay.isZero() || baseDelay.isNegative()){throw new IllegalArgumentException("baseDelay");}
        if (maxDelay == null || maxDelay.isZero() || maxDelay.isNegative()){throw new IllegalArgumentException("maxDelay");}
        this.delegate = delegate;
        this.maxAttempts = maxAttempts;
        this.baseDelay = baseDelay;
        this.maxDelay = maxDelay;
    }

    @Override
    public PriceQuote fetch(Symbol symbol) {
        int attempt = 1;

        // keeps trying until either returns quote, or throws
        while (true){
            try{
                return delegate.fetch(symbol);
            }
            
            // retry won't help; rethrow
            catch(FatalProviderException e){
                throw e;
            }
            
            // if out of attempts; rethrow
            catch(TransientProviderException e){
                if (attempt >= maxAttempts){
                    throw e;
                }
                // compute exponential delay (double wait time after each failure until maxDelay)
                long delayMs = baseDelay.toMillis() * (1L << (attempt - 1));
                delayMs = Math.min(delayMs, maxDelay.toMillis());

                try{
                    Thread.sleep(delayMs);
                }
                catch (InterruptedException ie){
                    Thread.currentThread().interrupt();
                    throw new TransientProviderException("Interrupted during backoff", ie);
                }

                attempt++;
            }
            

        }
    }
}
