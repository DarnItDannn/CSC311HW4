package org.example.csc311hw4;

import javafx.scene.control.Alert;

public class Validation
{
    private String verifyTitle;
    private String verifyYear;
    private String verifySales;

    private static String checker1 = "";
    private static String checker2 = "";
    private static String checker3 = "";

    public Validation(String verifyTitle, String verifyYear, String verifySales)
    {
        this.verifyTitle = verifyTitle;
        this.verifyYear = verifyYear;
        this.verifySales = verifySales;
    }

    public static String checkTitle(String verifyTitle, String regex)
    {
        boolean result;
        result = verifyTitle.matches(regex);

        if (result) {
            System.out.println("");
            checker1 = "";

            return checker1;
        }

        else if(verifyTitle.isEmpty())
        {
            checker1 = "Title is empty!\n";
            return checker1;
        }

        else {

            checker1 = "Title cannot be empty and must start with an uppercase.\n";
            return checker1;

        }

    }

    public static String checkYear(String verifyYear, String regex)
    {
        boolean result;
        result = verifyYear.matches(regex);

        if (result) {
            System.out.println("");
            checker2 = "";

            return checker2;
        }

        else if(verifyYear.isEmpty())
        {
            checker2 = "Year is empty!\n";
            return checker2;
        }

        else {

            checker2 = "Year must contain four digits.\n";
            return checker2;

        }
    }

    public static String checkSales(String verifySales, String regex)
    {
        boolean result;
        result = verifySales.matches(regex);

        if (result == true) {
            System.out.println("");
            checker3 = "";
            return checker3;
        }

        else if(verifySales.isEmpty())
        {
            checker3 = "Sales is empty!\n";
            return checker3;
        }

        else {

            checker3 = "Sales can only contain digits. The decimal point is optional.\n" +
                    "If the decimal point is included there must be at\n least one number before " +
                    "and at least one number after it.";

            return checker3;


        }
    }

    public String getChecker1()
    {
        return checker1;
    }

    public String getChecker2()
    {
        return checker2;
    }

    public String getChecker3()
    {
        return checker3;
    }


}
