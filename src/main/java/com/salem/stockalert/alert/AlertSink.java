package com.salem.stockalert.alert;

import com.salem.stockalert.model.AlertEvent;

public interface AlertSink {
    void publish(AlertEvent event);
    
}
