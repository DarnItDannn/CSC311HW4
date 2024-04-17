package org.example.csc311hw4;

import javafx.scene.control.Alert;

public class Validation
{
    private String verifyTitle;
    private String verifyYear;
    private String verifySales;

    private static String checker1 = "";

    public Validation(String verifyTitle, String verifyYear, String verifySales)
    {
        this.verifyTitle = verifyTitle;
        this.verifyYear = verifyYear;
        this.verifySales = verifySales;
    }

    public static void checkTitle(String verifyTitle, String regex)
    {
        boolean result;
        result = verifyTitle.matches(regex);

        if (result == true) {
            System.out.println("");
            checker1 = "";
        }

        else {

            checker1 = "error";
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Title cannot be empty.\nYear must contain four digits.\nSales can only" +
                    "contain digits. The decimal point is optional. If the decimal point is included there must" +
                    "be at least one number before and at least one number after it.");
            alert.showAndWait();
            System.out.println("Does not match");
        }

    }

    public static void checkYear(String verifyYear, String regex)
    {
        boolean result;
        result = verifyYear.matches(regex);

        if (result == true) {
            System.out.println("");
        }

        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Title cannot be empty.\nYear must contain four digits.\nSales can only" +
                    "contain digits. The decimal point is optional. If the decimal point is included there must" +
                    "be at least one number before and at least one number after it.");
            alert.showAndWait();
            System.out.println("Does not match");
        }
    }

    public static void checkSales(String verifySales, String regex)
    {
        boolean result;
        result = verifySales.matches(regex);

        if (result == true) {
            System.out.println("");
        }

        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Title cannot be empty.\nYear must contain four digits.\nSales can only" +
                    "contain digits. The decimal point is optional. If the decimal point is included there must" +
                    "be at least one number before and at least one number after it.");
            alert.showAndWait();
            System.out.println("Does not match");
        }
    }


    public String getVerifyTitle() {
        return verifyTitle;
    }

    public void setVerifyTitle(String verifyTitle) {
        this.verifyTitle = verifyTitle;
    }

    public String getVerifyYear() {
        return verifyYear;
    }

    public void setVerifyYear(String verifyYear) {
        this.verifyYear = verifyYear;
    }

    public String getVerifySales() {
        return verifySales;
    }

    public void setVerifySales(String verifySales) {
        this.verifySales = verifySales;
    }
}
