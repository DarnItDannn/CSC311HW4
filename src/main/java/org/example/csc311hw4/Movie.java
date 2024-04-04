package org.example.csc311hw4;

public class Movie
{
    private String title;
    private int year;
    private double sales;

    public Movie(String title, int year, double sales)
    {
        this.title = title;
        this.year = year;
        this.sales = sales;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }
}