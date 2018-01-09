package com.example.kangw.evolut.models;

/**
 * Created by User on 10/1/2018.
 */

public class Transactions {
    String to;
    String time;
    Double amount;
    String comments;

    public Transactions(String to, String time, Double amount, String comments) {
        this.to = to;
        this.time = time;
        this.amount = amount;
        this.comments = comments;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String timeStamp) {
        this.time = timeStamp;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
