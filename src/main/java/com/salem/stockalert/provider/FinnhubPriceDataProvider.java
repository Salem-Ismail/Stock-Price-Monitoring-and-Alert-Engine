package com.salem.stockalert.provider;

import com.salem.stockalert.model.PriceQuote;
import com.salem.stockalert.model.Symbol;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import com.google.gson.Gson;

public class FinnhubPriceDataProvider implements PriceDataProvider {
    private static final String API_KEY_ENV = "FINNHUB_API_KEY";
    private static final String BASE_URL = "https://finnhub.io/api/v1/quote";
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    
    private static class QuoteDto{
        BigDecimal c; 
        Long t;
    }
    
    @Override
    public PriceQuote fetch(Symbol symbol) {
        // validate inputs
        if (symbol == null){
            throw new IllegalArgumentException("symbol");
        }
        
        // read the API key, and throw error if null/blank
        String key = System.getenv(API_KEY_ENV);
        if (key == null || key.isEmpty()){
            throw new IllegalStateException("Missing FINNHUB_API_KEY");
        }

        // requesting url
        String encoded = URLEncoder.encode(symbol.getTicker(), StandardCharsets.UTF_8);
        String url = BASE_URL + "?symbol=" + encoded + "&token=" + key;

        // sending http request
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).GET().build();
        HttpResponse<String> response;
        try {
            response = http.send(request, BodyHandlers.ofString());
        } catch (IOException e) {
            throw new TransientProviderException("Network/IO failure talking to Finnhub", e);
        }
          catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TransientProviderException("Interrupted while calling Finnhub", e);
        }       

        int status = response.statusCode();
        String body = response.body();

        // http status handling
        if (status == 401 || status == 403){
            throw new FatalProviderException("Finnhub unauthorized (check FINNHUB_API_KEY). status=" + status); 
        }
        if (status == 429) {
            throw new TransientProviderException("Finnhub rate limited. status=429");
        }
        if (status >= 500 && status <= 599) {
            throw new TransientProviderException("Finnhub server error. status=" + status);
        }
        if (status != 200) {
            throw new FatalProviderException("Finnhub unexpected status=" + status + " body=" + body);
        }

        // parsing JSON
        QuoteDto dto = gson.fromJson(response.body(), QuoteDto.class);

        // validating parsed fields
        if (dto == null || dto.c == null || dto.t == null || dto.t <= 0){
            throw new FatalProviderException("Bad Finnhub response body=" + body);
        }

        return new PriceQuote(symbol, dto.c, Instant.ofEpochSecond(dto.t));

    }
}
