package com.floydjohn.pizzaplanet.data.time;

public class Istante {
    private int day;
    private int hrs;
    private int min;

    public Istante(int day, int hrs, int min) {
        this.day = day;
        this.hrs = hrs;
        this.min = min;
    }

    public int getDay() {
        return day;
    }

    public int getHrs() {
        return hrs;
    }

    public int getMin() {
        return min;
    }

    public void incMin() {
        min++;
        if (min >= 60) {
            min = 0;
            hrs++;
        }
        if (hrs >= 23) {
            min = 0;
            hrs = 17;
            day++;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Istante(day, hrs, min);
    }

    public String getMinString() {
        if (min < 10) return "0" + min;
        return String.valueOf(min);
    }

    @Override
    public String toString() {
        return "Day " + day + ", " + hrs + ":" + getMinString();
    }
}
