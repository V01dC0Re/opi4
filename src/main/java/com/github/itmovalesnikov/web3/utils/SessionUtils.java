package com.github.itmovalesnikov.web3.utils;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpSession;

/**
 * Utility class for retrieving session-related information from JSF context.
 * Provides methods to access the current HTTP session ID.
 */
public class SessionUtils {

    /**
     * Retrieves the current HTTP session ID from the Faces context.
     *
     * @return The session ID string for the current user session
     * @throws IllegalStateException if FacesContext is not available
     */
    public static String getSessionId() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            throw new IllegalStateException("FacesContext is not available");
        }

        HttpSession session = (HttpSession) facesContext
                .getExternalContext()
                .getSession(true);

        return session.getId();
    }
}