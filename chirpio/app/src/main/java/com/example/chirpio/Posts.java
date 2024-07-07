package com.example.chirpio;

import java.io.Serializable;

public class Posts implements Serializable {
    private String name, post, date, id,user_id;
    private long like_count;

    public Posts() {
    }

    public Posts(String name, String post, String date, long like_count) {
        this.name = name;
        this.post = post;
        this.date = date;
        this.like_count = like_count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public String getPost() {
        return post;
    }

    public String getDate() {
        return date;
    }

   // public long getComment_count() {
      //  return comment_count;
    //}

    public long getLike_count() {
        return like_count;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}
