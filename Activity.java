
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Activity {
    protected java.util.Date date;
    protected char type;
    protected String currentNumber;

    public Activity(char type, String currentNumber) {
        this.type = type;
        date = new java.util.Date();
        this.currentNumber = currentNumber;
    }

    public Activity(char type, String currentNumber, String date){
        this.type = type;
        this.date = stringToDate(date);
        this.currentNumber = currentNumber;
    }

    private Date stringToDate(String s)  {
        Date sdf = null;
        try {
            sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy").parse(s);
        } catch (Exception e) {
            e.getStackTrace();
        }
        return sdf;
    }

    public char getType() {
        return this.type;
    }

    public static void printlnArray(ArrayList<Activity> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            System.out.println(arrayList.get(i));
        }
    }

    @Override
    public String toString() {
        switch (this.type) {

        case 'A': // Attempt login
            return String.format("Account number %s A login-Attempt %s", this.currentNumber, date.toString());

        case 'F': // failed login
            return String.format("Account number %s F failed-login %s", this.currentNumber, date.toString());

        case 'L': // Login successful
            return String.format("Account number %s L successful-login %s", this.currentNumber, date.toString());

        case 'C': // CheckingAccount access
            return String.format("Account number %s C Accessed checking account %s", this.currentNumber,
                    date.toString());

        case 'S': // SavingAccount access
            return String.format("Account number %s S Accessed saving account %s", this.currentNumber, date.toString());

        case 'P': // PrintSummary
            return String.format("Account number %s P requested Account summary %s", this.currentNumber,
                    date.toString());

        case 'B': // Back to account screen
            return String.format("Account number %s B went back to accounts screen %s", this.currentNumber,
                    date.toString());

        case 'Q': // Quit
            String s = String.format("Account number %s Q logout %s", this.currentNumber, date.toString());
            return s;

        case 'X': // print diposites request
            return String.format("Account number %s X made a print diposites request %s", this.currentNumber,
                    date.toString());

        case 'Y': // print withdrawals request
            return String.format("Account number %s Y made a print withdrawals request %s", this.currentNumber,
                    date.toString());

        case 'Z': // print all activities request
            return String.format("Account number %s Z made a print all customer activities request %s",
                    this.currentNumber, date.toString());

        }
        return null;
    }

    public static Activity lineToActivity(String s){
        String[] stringContent = s.split(" ");
        String[] reverseString = new String[6];
        String accountNumber = stringContent[2];
        String date = "";
        // here is the Malicious error stringContent[4] =>stringContent[3]
        char activityType = Character.toUpperCase(stringContent[3].charAt(0));

        for (int i = stringContent.length - 1, j = 5; i > stringContent.length - 7; i--, j--) {
            reverseString[j] = stringContent[i];
        }
        for (int i = 0; i < reverseString.length; i++) {
            date += reverseString[i] + " ";
        }

        if (activityType == 'D' || activityType == 'W') {
            double amount = Double.parseDouble(stringContent[7]);
            double currentBalance = Double.parseDouble(stringContent[10]);
            String accountType = stringContent[4];
            Customer currentCustomer = new Customer("", accountNumber);
            return new Transaction(activityType, currentCustomer, amount, currentBalance, accountType);
        }

        return new Activity(activityType, accountNumber, date);

    }
}