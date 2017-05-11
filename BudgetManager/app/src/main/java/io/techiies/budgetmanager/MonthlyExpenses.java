package io.techiies.budgetmanager;

import java.util.Date;

/**
 * Created by Joe Ku on 5/10/2017.
 */

public class MonthlyExpenses {


    public enum Month
    {
        JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE,
        JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    }
    private float amount;

    public MonthlyExpenses () {
        this.amount = 0;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
