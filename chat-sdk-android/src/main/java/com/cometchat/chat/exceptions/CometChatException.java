package com.cometchat.chat.exceptions;

/**
 * A class to inform the developers about any error or exception that has occurred
 * Created by adityagokula on 04/09/18.
 */

public class CometChatException extends Exception {

    private String code;
    private String details;

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

    public CometChatException(String code, String message) {
        super(message);
        this.setCode(code);
    }
    public CometChatException(String code, String message,String details) {
        super(message);
        this.setCode(code);
        this.setDetails(details);
    }
}
