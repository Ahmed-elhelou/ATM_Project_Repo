package final_project;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

class Activity {
    protected java.util.Date date;
    protected char type;
    protected String currentNumber;

    public Activity(char type, String currentNumber) {
        this.type = type;
        date = new java.util.Date();
        this.currentNumber = currentNumber;
    }

    public Activity(char type, String currentNumber, String date) throws ParseException {
        this.type = type;
        this.date = stringToDate(date);
        this.currentNumber = currentNumber;
    }

    private Date stringToDate(String s) throws NullPointerException, IllegalArgumentException, ParseException {
        return new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(s);
    }

    public char getType() {
        return this.type;
    }

    @Override
    public String toString() {
        switch (this.type) {

        case 'A': // Attempt login
            return String.format("Account number %s A login-Attempt %s\n", this.currentNumber, date.toString());

        case 'F': // failed login
            return String.format("Account number %s F failed-login %s\n", this.currentNumber, date.toString());

        case 'L': // Login successful
            return String.format("Account number %s L successful-login %s\n", this.currentNumber, date.toString());

        case 'C': // CheckingAccount access
            return String.format("Account number %s C Accessed checking account %s\n", this.currentNumber,
                    date.toString());

        case 'S': // SavingAccount access
            return String.format("Account number %s S Accessed saving account %s\n", this.currentNumber,
                    date.toString());

        case 'P': // PrintSummary
            return String.format("Account number %s P requested Account summary %s\n", this.currentNumber,
                    date.toString());

        case 'B': // Back to account screen
            return String.format("Account number %s B went back to accounts screen %s\n", this.currentNumber,
                    date.toString());

        case 'Q': // Quit
            String s = String.format("Account number %s Q logout %s\n", this.currentNumber, date.toString());
            return s;

        case 'X': // print diposites request
            return String.format("Account number %s X made a print diposites request %s\n", this.currentNumber,
                    date.toString());

        case 'Y': // print withdrawals request
            return String.format("Account number %s Y made a print withdrawals request %s\n", this.currentNumber,
                    date.toString());

        case 'Z': // print all activities request
            return String.format("Account number %s Z made a print all customer activities request %s\n",
                    this.currentNumber, date.toString());

        }
        return null;
    }
}