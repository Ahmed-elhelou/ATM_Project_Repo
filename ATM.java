package final_project;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.security.*;

class ATM {

    public final static String START = "START";
    public final static String PIN = "PIN";
    public final static String ACCOUNT = "ACCOUNT";
    public final static String TRANSACT = "TRANSACT";
    public final static String SUMMARY = "SUMMARY";
    public final static String QUIT = "QUIT";
    public final static String SAVING = "Saving";
    public final static String CHECKING = "Checking";

    private Bank currentBank;
    private String state;
    private Customer currentCustomer;
    private BankAccount currentAccount;
    private String currentAccountType;
    private String currentNumber;
    private String currentPIN;
    private Scanner input;
    private ArrayList<Activity> logArray;
    private File logFile;
    private Activity currentActivity;
    private int numberOfNewActivities;
    private int numberOfSavedActivities;

    public ATM(Bank currentBank) {
        this.state = ATM.START;
        this.currentBank = currentBank;
        input = new Scanner(System.in);
        logArray = new ArrayList<>();
        logFile = new File(
                "C:\\Users\\HP\\Desktop\\University\\Porgramming II\\Assignments\\Project part 1\\final_project\\Log.txt");
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void prompts() throws FileNotFoundException {

        switch (this.state) {
        case ATM.START:
            System.out.print("Enter customer ID : ");
            this.currentNumber = this.input.next();
            this.setState(ATM.PIN);
            this.logArray.add(new Activity('A', this.currentNumber));
            numberOfNewActivities++;
            break;
        case ATM.PIN:
            System.out.print("Enter PIN: ");
            this.currentPIN = this.input.next();
            this.currentCustomer = this.currentBank.getCustomer(currentPIN, currentNumber);
            if (this.currentCustomer != null) {
                this.setState(ATM.ACCOUNT);
                this.currentNumber = this.currentCustomer.getNumber();
                currentActivity = new Activity('L', this.currentNumber);
                this.logArray.add(currentActivity);
                this.currentCustomer.addActivity(currentActivity);
                numberOfNewActivities++;
            } else {
                this.setState(ATM.START);
                this.logArray.add(new Activity('F', this.currentNumber));
                numberOfNewActivities++;
            }
            break;

        case ATM.ACCOUNT:
            System.out.print("A=Checking-Account, B=Savings-Account, C=Get-customers-activity-summary, D=Logout: ");
            char option = Character.toLowerCase(this.input.next().charAt(0));
            switch (option) {
            case 'a':
                this.currentAccount = this.currentCustomer.getCheckingAcount();
                currentAccountType = ATM.CHECKING;
                this.state = ATM.TRANSACT;
                this.logArray.add(new Activity('C', this.currentNumber));
                numberOfNewActivities++;
                break;
            case 'b':
                this.currentAccount = this.currentCustomer.getSavingAcount();
                currentAccountType = ATM.SAVING;
                this.state = ATM.TRANSACT;
                this.logArray.add(new Activity('S', this.currentNumber));
                numberOfNewActivities++;
                break;
            case 'c':
                this.state = ATM.SUMMARY;
                this.logArray.add(new Activity('P', this.currentNumber));
                numberOfNewActivities++;
                break;
            case 'd':
                this.logArray.add(new Activity('Q', this.currentNumber));
                numberOfNewActivities++;
                this.state = ATM.START;
                this.currentCustomer = null;
                this.currentAccount = null;
                this.currentNumber = null;
                this.currentPIN = null;
                saveLog();
                break;
            }
            break;

        case ATM.TRANSACT:

            System.out.println("Current Balance = " + this.currentAccount.getBalance());
            System.out.print("A=Deposit, B=Withdraw, C=Print-account-summary D=Back: ");
            option = Character.toLowerCase(this.input.next().charAt(0));
            double amount;
            switch (option) {
            case 'a':
                System.out.print("Amount: ");
                amount = this.input.nextDouble();
                currentAccount.deposits(amount);
                currentActivity = new Transaction('D', this.currentCustomer, amount, this.currentAccount.getBalance(),
                        this.currentAccountType);
                this.currentCustomer.addActivity(currentActivity);
                this.logArray.add(currentActivity);
                // System.out.println("error");
                // this.currentAccount = null;
                this.state = ATM.TRANSACT;
                numberOfNewActivities++;
                break;
            case 'b':
                System.out.print("Amount: ");
                amount = this.input.nextDouble();
                currentAccount.withdrawals(amount);
                currentActivity = new Transaction('W', this.currentCustomer, amount, this.currentAccount.getBalance(),
                        this.currentAccountType);
                this.currentCustomer.addActivity(currentActivity);
                this.logArray.add(currentActivity);
                // this.currentAccount = null;
                this.state = ATM.TRANSACT;
                numberOfNewActivities++;
                break;
            case 'c':
                ArrayList<Activity> customerActivity = currentCustomer.getTransactionsList();
                for (Activity element : customerActivity) {
                    if (element.toString().contains(this.currentAccountType + "Account")) { // CheckingAccount
                        System.out.print(element.toString());
                    }
                }
                this.logArray.add(new Activity('P', this.currentNumber));
                numberOfNewActivities++;
                break;
            case 'd':
                this.state = ATM.ACCOUNT;
                this.logArray.add(new Activity('B', this.currentCustomer.getNumber()));
                numberOfNewActivities++;
                break;
            }
            break;

        case ATM.SUMMARY:
            System.out.print("A=Print-deposites, B=Print-withdrawals, C=Get full activity summary, D=Back: ");
            ArrayList<Activity> customerActivitiesArray = this.currentCustomer.getTransactionsList();
            char option2 = Character.toLowerCase(this.input.next().charAt(0));
            char typeChar = 'K';
            char activityChar = 'K';
            switch (option2) {
            case 'a':
                typeChar = 'D';
                activityChar = 'X';
                break;

            case 'b':
                typeChar = 'W';
                activityChar = 'Y';
                break;

            case 'c':
                String[] activityString;
                for (Activity element : logArray) {
                    if (element != null) {
                        String s2 = element.toString();
                        activityString = s2.split(" ");
                        if (activityString[2].equals(this.currentCustomer.getNumber())) {
                            System.out.print(s2);
                        }
                    }
                }
                this.logArray.add(new Activity('Z', this.currentCustomer.getNumber()));
                numberOfNewActivities++;
                break;

            case 'd':
                this.state = ATM.ACCOUNT;
                activityChar = 'B';
                break;
            }
            if (typeChar != 'c') {
                for (int i = 0; i < customerActivitiesArray.size(); i++) {
                    if (customerActivitiesArray.get(i).getType() == typeChar) {
                        System.out.print(customerActivitiesArray.get(i).toString());
                    }
                }
                this.logArray.add(new Activity(activityChar, this.currentCustomer.getNumber()));
                numberOfNewActivities++;
            }
            break;
        }

    }

    public void loadLog() throws FileNotFoundException, ParseException {
        try (Scanner tempScanner = new Scanner(this.logFile)) {
            while (tempScanner.hasNextLine()) {
                String s = tempScanner.nextLine();
                logArray.add(lineToTransaction(s));
            }
        }
        numberOfSavedActivities = logArray.size();
        numberOfNewActivities = 0;
    }

    public void saveLog() throws FileNotFoundException {
        try (PrintWriter logWriter = new PrintWriter(new FileOutputStream(logFile, true))) {
            for (int i = numberOfSavedActivities; i < numberOfSavedActivities + numberOfNewActivities; i++) {
                logWriter.write(this.logArray.get(i).toString());
            }
            numberOfSavedActivities += numberOfNewActivities;
        }
    }

    private Activity lineToTransaction(String s) throws ParseException {
        String[] stringContent = s.split(" ");
        String[] reverseString = new String[6];
        String accountNumber = stringContent[2];
        String date = "";
        char activityType = Character.toLowerCase(stringContent[4].charAt(0));

        for (int i = stringContent.length - 1, j = 5; i > stringContent.length - 7; i--, j--) {
            reverseString[j] = stringContent[i];
        }
        for (int i = 0; i < reverseString.length; i++) {
            date += reverseString[i] + " ";
        }
        return new Activity(activityType, accountNumber, date);

    }

}