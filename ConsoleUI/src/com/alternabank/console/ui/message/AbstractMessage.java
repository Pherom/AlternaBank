package com.alternabank.console.ui.message;

public abstract class AbstractMessage implements Message {

    private final String contents;

    protected AbstractMessage(String contents) {
        this.contents = contents;
    }

    @Override
    public String getContents() {
        return contents;
    }

    @Override
    public void display() {
        System.out.println(contents);
        System.out.println();
    }
}
