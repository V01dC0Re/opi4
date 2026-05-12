package com.github.itmovalesnikov.web3.bean;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A managed bean that provides the current time formatted as a string.
 * Used to display the current timestamp on web pages.
 */
@Named("clockBean")
@RequestScoped
public class ClockBean {
    
    /**
     * Returns the current date and time formatted as "yyyy-MM-dd HH:mm:ss".
     *
     * @return Current date and time as a formatted string
     */
    public String getCurrentTime() {
        return LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
    }
}