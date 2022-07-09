package com.alternabank.engine.xml.result;

public class XMLLoadResult {

    private XMLLoadStatus status;
    private String message;

    public XMLLoadResult(XMLLoadStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public XMLLoadStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
