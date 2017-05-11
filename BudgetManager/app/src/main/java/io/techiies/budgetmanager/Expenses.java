package io.techiies.budgetmanager;

import java.util.Date;

/**
 * Created by Joe Ku on 5/10/2017.
 */

public class Expenses {

    private String title;
    private float amount;
    private Date date;
    //still need category

    public Expenses (String title, float amount, Date date) {
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
