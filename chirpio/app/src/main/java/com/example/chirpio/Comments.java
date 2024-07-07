package com.example.chirpio;

import java.io.Serializable;

public class Comments implements Serializable {

    String comment,date;

    public Comments(String comment, String date) {
        this.comment = comment;
        this.date = date;
    }

    public Comments() {
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
}
