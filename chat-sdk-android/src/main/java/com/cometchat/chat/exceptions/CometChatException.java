package com.cometchat.chat.exceptions;

import java.util.Map;

/**
 * A class to inform the developers about any error or exception that has occurred
 * Created by adityagokula on 04/09/18.
 */

public class CometChatException extends Exception {

    private String code;
    private String details;
    private Map<String, Object> errorParams;

    /**
     *  get the code of the exception occurred
     *
     * @version <b>v2</b>
     * @since   <b>v1</b>
     *
     */
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     *  get the details of the exception occurred if any
     *
     * @version <b>v2</b>
     * @since   <b>v1</b>
     *
     */
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * Structured, machine-readable parameters that accompany a server error (for example the
     * {@code limit} on a pinned/saved cap error, or the RBAC scope on a permission denial).
     * Populated from the API {@code error.details} object; may be {@code null} when the error
     * carries no params.
     *
     * @return the error params, or {@code null}
     * @since <b>v5</b>
     */
    public Map<String, Object> getErrorParams() {
        return errorParams;
    }

    public void setErrorParams(Map<String, Object> errorParams) {
        this.errorParams = errorParams;
    }

    public CometChatException(String code, String message) {
        super(message);
        this.setCode(code);
    }
    public CometChatException(String code, String message,String details) {
        super(message);
        this.setCode(code);
        this.setDetails(details);
    }
    public CometChatException(String code, String message, String details, Map<String, Object> errorParams) {
        super(message);
        this.setCode(code);
        this.setDetails(details);
        this.setErrorParams(errorParams);
    }
}
