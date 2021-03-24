import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class FinalProject {


    public static void main(String[] args) throws java.io.IOException, FileNotFoundException, ParseException {


        File file = new File("BankAccounts.txt");
        Scanner input = new Scanner(file);
        input.useDelimiter(",");
        Bank bank = new Bank();
        while (input.hasNext()) {
            String number = input.next().trim();
            String PIN = input.next();
            double checkingAccountBalance = Double.parseDouble(input.next());
            double savingAcountBalance = Double.parseDouble(input.next());
            // i modified addCustomer() and Customer class's constructor to receive
            // checkingAccountBalance and savingAcountBalance
            bank.addCustomer(PIN, number, checkingAccountBalance, savingAcountBalance);
        }
        ATM atm = new ATM(bank);
        atm.loadLog();
        while (atm.getState() != ATM.QUIT) {
            atm.prompts();
        }
        // atm.SaveLog();
    }
}

class BankAccount {
    private double balance;

    public BankAccount(double balance) {
        this.balance = balance;
    }

    public BankAccount() {
        this(0);
    }

    public void deposits(double amount) {
        this.balance += amount;
    }

    public void withdrawals(double amount) {
        this.balance -= amount;
    }

    public double getBalance() {
        return this.balance;
    }


    public void setBalance(double balance) {
        this.balance = balance;
    }



}

class Customer {

    private ArrayList<Activity> transactions;
    private BankAccount checkingAcount;
    private BankAccount savingAcount;
    private String number;
    private String PIN;

    public Customer(String PIN, String number, double checkingBalane, double savingBalance) {
        this.checkingAcount = new BankAccount(checkingBalane);
        this.savingAcount = new BankAccount(savingBalance);

        this.transactions = new ArrayList<>();
        this.PIN = PIN;
        this.number = number;
    }

    public String getPIN() {
        return this.PIN;
    }

    public String getNumber() {
        return this.number;
    }

    public BankAccount getCheckingAcount() {
        return this.checkingAcount;
    }

    public BankAccount getSavingAcount() {
        return this.savingAcount;
    }

    public void addActivity(Activity tr) {
        this.transactions.add(tr);
    }

    public void printSummary() {
        System.out.println("Account Summary: ");
        for (int i = 0; i < this.transactions.size(); i++) {
            System.out.println(this.transactions.get(i).toString());
        }
    }

    public ArrayList<Activity> getTransactionsList() {
        return this.transactions;
    }
}

class Bank {
    ArrayList<Customer> customers = new ArrayList<>();

    public void addCustomer(String PIN, String number, double checkingBalane, double savingBalance) {
        for (int i = 0; i < this.customers.size(); i++) {
            if (this.customers.get(i).getNumber().equals(number)) {
                System.out.println("this number: " + number + " is already registered!");
                return;
            }
        }
        customers.add(new Customer(PIN, number, checkingBalane, savingBalance));
    }

    public static boolean cheackEnteries(Customer customer, String PIN, String number) {
        return (customer.getPIN().equals(PIN)) && (customer.getNumber().equals(number));
    }

    public Customer getCustomer(String PIN, String number) {
        for (int i = 0; i < this.customers.size(); i++) {
            if (Bank.cheackEnteries(this.customers.get(i), PIN, number))
                return this.customers.get(i);
        }
        return null;
    }
}

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
        logFile = new File("Log.text");
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
            System.out.print("A=Deposit, B=Withdraw, C=Print-account-summary D=Cancel: ");
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
                this.currentAccount = null;
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
                this.currentAccount = null;
                this.state = ATM.TRANSACT;
                numberOfNewActivities++;
                break;
            case 'c':
                ArrayList<Activity> customerActivity = currentCustomer.getTransactionsList();
                for (Activity element : customerActivity) {
                    if (element.toString().contains(this.currentAccountType + " account")) {
                        System.out.println(element.toString());
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
            System.out.print("A=Print-deposites, B=Print-withdrawals, C=Get full activity summary, D=back: ");
            char option2 = Character.toLowerCase(this.input.next().charAt(0));
            ArrayList<Activity> customerActivitiesArray = this.currentCustomer.getTransactionsList();
            switch (option2) {
            case 'a':
                for (int i = 0; i < customerActivitiesArray.size(); i++) {
                    if (customerActivitiesArray.get(i).getType() == 'd') {
                     customerActivitiesArray.get(i).toString();
                    }
                }
                this.logArray.add(new Activity('X', this.currentCustomer.getNumber()));
                numberOfNewActivities++;
                break;

            case 'b':
                for (int i = 0; i < customerActivitiesArray.size(); i++) {
                    if (customerActivitiesArray.get(i).getType() == 'w') {
                     customerActivitiesArray.get(i).toString();
                    }
                }
                this.logArray.add(new Activity('Y', this.currentCustomer.getNumber()));
                numberOfNewActivities++;
                break;

            case 'c':
                this.logArray.add(new Activity('Z', this.currentCustomer.getNumber()));
                String[] activityString;
                for (Activity element : logArray) {
                    activityString = element.toString().split(" ");
                    if (activityString[2].equals(this.currentCustomer.getNumber())) {
                        System.out.println(element.toString());
                    }
                }
                numberOfNewActivities++;

            case 'd':
                this.state = ATM.ACCOUNT;
                this.logArray.add(new Activity('B', this.currentCustomer.getNumber()));
                numberOfNewActivities++;
                break;
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
        try (PrintWriter logWriter = new PrintWriter(logFile)) {
            for (int i = numberOfNewActivities; i < numberOfNewActivities + numberOfNewActivities; i++){
                logWriter.write(this.logArray.get(numberOfSavedActivities).toString());
            }
            numberOfSavedActivities += numberOfNewActivities;
        }
    }

    private Activity lineToTransaction(String s) throws ParseException {
        String[] stringContent = s.split(" ");
        String accountNumber = stringContent[3];
        String date = "";
        char activityType = Character.toLowerCase(s.charAt(18));
        switch (activityType) {
        case 'a':
            for (int i = 4; i < 10; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('A', accountNumber, date);

        case 'f':
            for (int i = 4; i < 10; i++) {
                date += stringContent[i] + " ";
            }   
            return new Activity('F', accountNumber, date);

        case 'c':
            for (int i = 6; i < 12; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('C', accountNumber, date);

        case 's':
            for (int i = 6; i < 12; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('S', accountNumber, date);

        case 'p':
            for (int i = 6; i < 12; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('P', accountNumber, date);

        case 'b':
            for (int i = 8; i < 14; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('B', accountNumber, date);

        case 'q':
            for (int i = 4; i < 10; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('Q', accountNumber, date);

        case 'd':
            for (int i = 9; i < 15; i++) {
                date += stringContent[i] + " ";
            }
            return new Transaction('D', this.currentCustomer, Double.parseDouble(stringContent[7]),
                    Double.parseDouble(stringContent[9]), stringContent[4], date);

        case 'w':
            for (int i = 9; i < 15; i++) {
                date += stringContent[i] + " ";
            }
            return new Transaction('W', this.currentCustomer, Double.parseDouble(stringContent[7]),
            Double.parseDouble(stringContent[9]), stringContent[4], date);
        
        case 'x':
            for (int i = 9; i < 15; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('X', accountNumber);
        
        case 'y':
            for (int i = 9; i < 15; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('Y', accountNumber);
        }
        return null;
    }

}

class Transaction extends Activity {
    private double amount;
    private double currentBalance;
    private String accountType;
    private Customer currentCustomer;

    public Transaction(char type, Customer currentCustomer, double amount, double balance, String accountType) {
        super(type, ""+currentCustomer.getNumber());
        this.amount = amount;
        this.currentBalance = balance;
        this.accountType = accountType;
        this.currentCustomer = currentCustomer;
    }
    public Transaction(char type, Customer currentCustomer, double amount, double balance, String accountType, String date) throws ParseException {
        super(type, ""+currentCustomer.getNumber(), date);
        this.amount = amount;
        this.currentBalance = balance;
        this.accountType = accountType;
        this.currentCustomer = currentCustomer;

    }

    public Transaction(char type, String currentNumber) {
        super(type, currentNumber);

    }

    @Override
    public String toString() {
        switch (this.type) {

        case 'D': // Deposite
            return String.format("Account number %s D %sAccount Diposit amount %f Current balance %f %s",
                    currentCustomer.getNumber(), this.accountType, this.amount, this.currentBalance, date.toString());

        case 'W': // Withdrawal
            return String.format("Account number %s W %sAccount Withdrawal amount %f Current balance %f %s",
                    currentCustomer.getNumber(), this.accountType, this.amount, this.currentBalance, date.toString());
        }
        return null;
    }

}

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
            return String.format("Account number %s A login-Attempt %s", this.currentNumber, date.toString());

        case 'F': // failed login
            return String.format("Account number %s F failed-login %s", this.currentNumber, date.toString());

        case 'L': // Login successful
            return String.format("Account number %s L successful-login %s", this.currentNumber, date.toString());

        case 'C': // CheckingAccount access
            return String.format("Account number %s C Accessed checking account %s", this.currentNumber,
                    date.toString());

        case 'S': // SavingAccount access
            return String.format("Account number %s S Accessed saving account %s", this.currentNumber,
                    date.toString());

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
            return String.format("Account number %s Z made a print all customer activities request %s", this.currentNumber,
                    date.toString());

        }
        return null;
    }
}
        this.PIN = PIN;
        this.number = number;
    }

    public String getPIN() {
        return this.PIN;
    }

    public String getNumber() {
        return this.number;
    }

    public BankAccount getCheckingAcount() {
        return this.checkingAcount;
    }

    public BankAccount getSavingAcount() {
        return this.savingAcount;
    }

    public void addActivity(Activity tr) {
        this.transactions.add(tr);
    }

    public void printSummary() {
        System.out.println("Account Summary: ");
        for (int i = 0; i < this.transactions.size(); i++) {
            System.out.println(this.transactions.get(i).toString());
        }
    }

    public ArrayList<Activity> getTransactionsList() {
        return this.transactions;
    }
}

class Bank {
    ArrayList<Customer> customers = new ArrayList<>();

    public void addCustomer(String PIN, String number) {
        for (int i = 0; i < this.customers.size(); i++) {
            if (this.customers.get(i).getNumber().equals(number)) {
                System.out.println("this number: " + number + " is already registered!");
                return;
            }
        }
        customers.add(new Customer(PIN, number));
    }

    public static boolean cheackEnteries(Customer customer, String PIN, String number) {
        return (customer.getPIN().equals(PIN)) && (customer.getNumber().equals(number));
    }

    public Customer getCustomer(String PIN, String number) {
        for (int i = 0; i < this.customers.size(); i++) {
            if (Bank.cheackEnteries(this.customers.get(i), PIN, number))
                return this.customers.get(i);
        }
        return null;
    }
}

class ATM {

    public final static String START = "START";
    public final static String PIN = "PIN";
    public final static String ACCOUNT = "ACCOUNT";
    public final static String TRANSACT = "TRANSACT";
    public final static String SUMMARY = "SUMMARY";
    public final static String QUIT = "QUIT";

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

    public ATM(Bank currentBank) {
        this.state = ATM.START;
        this.currentBank = currentBank;
        input = new Scanner(System.in);
        logArray = new ArrayList<>();
        logFile = new File("Log.txt");
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

            this.logArray.add(new Activity('A', "" + this.currentNumber));

           

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
            } else {
                this.setState(ATM.START);
                this.logArray.add(new Activity('F', this.currentNumber));
            }
            break;
        case ATM.ACCOUNT:
            System.out.print("A=Checking-Account, B=Savings-Account, C=Get-customers-activity-summary, D=Logout: ");
            char option = Character.toLowerCase(this.input.next().charAt(0));
            switch (option) {
            case 'a':
                this.currentAccount = this.currentCustomer.getCheckingAcount();
                currentAccountType = "checking";
                this.state = ATM.TRANSACT;

                this.logArray.add(new Activity('C', "" + this.currentNumber));

                break;
            case 'b':
                this.currentAccount = this.currentCustomer.getSavingAcount();
                currentAccountType = "saving";
                this.state = ATM.TRANSACT;

                this.logArray.add(new Activity('S', "" + this.currentNumber));
                break;
            case 'c':
                this.state = ATM.SUMMARY;
                // this is not needed, since the next state (SUMMARY) gives options on what to
                // print
                // this.currentCustomer.printSummary();
                this.logArray.add(new Activity('P', "" + this.currentNumber));
                break;
            case 'd':
                this.logArray.add(new Activity('Q', "" + this.currentNumber));

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
            System.out.print("A=Deposit, B=Withdraw, C=Print-account-summary D=Cancel: ");
            option = Character.toLowerCase(this.input.next().charAt(0));
            double amount;
            switch (option) {
            case 'a':
                System.out.print("Amount: ");
                amount = this.input.nextDouble();
                currentAccount.deposits(amount);


                currentActivity = new Transaction('D', currentCustomer, amount, currentAccount.getBalance(),
                        currentAccountType);
                this.currentCustomer.addActivity(currentActivity);
                this.logArray.add(currentActivity);
                // this.currentAccount = null; //this line throws an exception
                this.state = ATM.TRANSACT; // instead of going to account state, it should stay in transact state until
                                           // the user chooses 'back'

                break;
            case 'b':
                System.out.print("Amount: ");
                amount = this.input.nextDouble();
                currentAccount.withdrawals(amount);
                currentActivity = new Transaction('W', this.currentCustomer, amount, this.currentAccount.getBalance(),
                        this.currentAccountType);
                this.currentCustomer.addActivity(currentActivity);
                this.logArray.add(currentActivity);

                // this.currentAccount = null; //this line throws an exception too
                this.state = ATM.TRANSACT; // same thing here
                break;
            case 'c':
                // search the customer's arraylist of activities using currentAccountType and
                // print all the found activities in this bankaccount

                ArrayList<Activity> customerActivity = currentCustomer.getTransactionsList();
                for (Activity element : customerActivity) {
                    if (element.toString().contains(this.currentAccountType + " account")) {
                        System.out.println(element.toString());

                    }
                }
                break;
            case 'd':
                this.state = ATM.ACCOUNT;

                this.logArray.add(new Activity('B', "" + this.currentCustomer.getNumber()));

                break;
            }
            break;

        case ATM.SUMMARY:
            System.out.print("A=Print-deposites, B=Print-withdrawals, C=Get full activity summary, D=back: ");
            char option2 = Character.toLowerCase(this.input.next().charAt(0));
            ArrayList<Activity> customerActivitiesArray = this.currentCustomer.getTransactionsList();
            switch (option2) {
            case 'a':
                for (int i = 0; i < customerActivitiesArray.size(); i++) {
                    if (customerActivitiesArray.get(i).getType() == 'd') {

                     customerActivitiesArray.get(i).toString();
                    }
                }
                this.logArray.add(new Activity('X', ""+this.currentCustomer.getNumber()));
                break;

            case 'b':
                for (int i = 0; i < customerActivitiesArray.size(); i++) {
                    if (customerActivitiesArray.get(i).getType() == 'w') {

                     customerActivitiesArray.get(i).toString();
                    }
                }
                this.logArray.add(new Activity('Y', ""+this.currentCustomer.getNumber()));
                break;

            case 'c':
                this.logArray.add(new Activity('Z', ""+this.currentCustomer.getNumber()));
                //instead of just searching through the customer's transactions, if the customer wants a full activity summary
                //we should get it from the logArray 
                // for (int i = 0; i < customerActivitiesArray.size(); i++) {
                //     customerActivitiesArray.get(i).toString();
                // }
                // break;

                //search the logArray and get all activities related to this customer
                String[] activityString;
                for (Activity element : logArray) {
                    activityString = element.toString().split(" ");
                    if (activityString[2].equals(""+this.currentCustomer.getNumber())) {
                        System.out.println(element.toString());
                    }
                }

            case 'd':
                this.state = ATM.ACCOUNT;

                this.logArray.add(new Activity('B', ""+this.currentCustomer.getNumber()));
                break;
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
    }

    public void saveLog() throws FileNotFoundException {

        //i need a way to delete the content of the file before i re-write the logArray into the logFile
        //i thought about deleting the file and creating a new one but maybe you know a better way? 
        //logFile.delete();
        //logFile.mkdirs();
        //i also thought about replacing the content of the logFile with "" instead of deleting the file
        try (PrintWriter logWriter = new PrintWriter(logFile)) {
            int count = 0;
            while (count < this.logArray.size()) {
                logWriter.write(this.logArray.get(count++).toString());
            }
        }
    }

    private Activity lineToTransaction(String s) throws ParseException {
        String[] stringContent = s.split(" ");
        String accountNumber = stringContent[3];
        String date = "";
        char activityType = Character.toLowerCase(s.charAt(19));
        switch (activityType) {
        case 'a':
            for (int i = 4; i < 10; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('A', accountNumber, date);

        case 'f':
            for (int i = 4; i < 10; i++) {
                date += stringContent[i] + " ";

            }   
            return new Activity('F', accountNumber, date);

        case 'c':
            for (int i = 6; i < 12; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('C', accountNumber, date);

        case 's':
            for (int i = 6; i < 12; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('S', accountNumber, date);

        case 'p':
            for (int i = 6; i < 12; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('P', accountNumber, date);

        case 'b':
            for (int i = 8; i < 14; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('B', accountNumber, date);

        case 'q':
            for (int i = 4; i < 10; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('Q', accountNumber, date);

        case 'd':
            for (int i = 9; i < 15; i++) {
                date += stringContent[i] + " ";
            }
            return new Transaction('D', this.currentCustomer, Double.parseDouble(stringContent[7]),
                    Double.parseDouble(stringContent[9]), stringContent[4], date);

        case 'w':
            for (int i = 9; i < 15; i++) {
                date += stringContent[i] + " ";
            }
            return new Transaction('W', this.currentCustomer, Double.parseDouble(stringContent[7]),

            Double.parseDouble(stringContent[9]), stringContent[4], date);
        
        case 'x':
            for (int i = 9; i < 15; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('X', accountNumber);
        case 'y':
            for (int i = 9; i < 15; i++) {
                date += stringContent[i] + " ";
            }
            return new Activity('Y', accountNumber);
        }
        return null;
    }

}

class Transaction extends Activity {
    private double amount;
    private double currentBalance;
    private String accountType;
    private Customer currentCustomer;

    public Transaction(char type, Customer currentCustomer, double amount, double balance, String accountType) {
        super(type, ""+currentCustomer.getNumber());
        this.amount = amount;
        this.currentBalance = balance;
        this.accountType = accountType;
        this.currentCustomer = currentCustomer;
    }

    public Transaction(char type, Customer currentCustomer, double amount, double balance, String accountType, String date) throws ParseException {
        super(type, ""+currentCustomer.getNumber(), date);
        this.amount = amount;
        this.currentBalance = balance;
        this.accountType = accountType;
        this.currentCustomer = currentCustomer;

    }

    public Transaction(char type, String currentNumber) {
        super(type, currentNumber);

    }

    @Override
    public String toString() {
        switch (this.type) {

        case 'D': // Deposite

            return String.format("Account number %s 'D' %sAccount Diposit amount %f Current balance %f %s\n",
                    currentCustomer.getNumber(), this.accountType, this.amount, this.currentBalance, date.toString());

        case 'W': // Withdrawal
            return String.format("Account number %s 'W' %sAccount Withdrawal amount %f Current balance %f %s\n",

                    currentCustomer.getNumber(), this.accountType, this.amount, this.currentBalance, date.toString());
        }
        return null;
    }

}

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

            return String.format("Account number %s 'A' login-Attempt %s\n", this.currentNumber, date.toString());

        case 'F': // failed login
            return String.format("Account number %s 'F' failed-login %s\n", this.currentNumber, date.toString());

        case 'L': // Login successful
            return String.format("Account number %s 'L' successful-login %s\n", this.currentNumber, date.toString());

        case 'C': // CheckingAccount access
            return String.format("Account number %s 'C' Accessed checking account %s\n", this.currentNumber,
                    date.toString());

        case 'S': // SavingAccount access
            return String.format("Account number %s 'S' Accessed saving account %s\n", this.currentNumber,
                    date.toString());

        case 'P': // PrintSummary
            return String.format("Account number %s 'P' requested Account summary %s\n", this.currentNumber,
                    date.toString());

        case 'B': // Back to account screen
            return String.format("Account number %s 'B' went back to accounts screen %s\n", this.currentNumber,
                    date.toString());

        case 'Q': // Quit
            String s = String.format("Account number %s 'Q' logout %s\n", this.currentNumber, date.toString());
            return s;

        case 'X': // print diposites request
            return String.format("Account number %s 'X' made a print diposites request %s\n", this.currentNumber,
                    date.toString());

        case 'Y': // print withdrawals request
            return String.format("Account number %s 'Y' made a print withdrawals request %s\n", this.currentNumber,
                    date.toString());

        case 'Z': // print all activities request
            return String.format("Account number %s 'Z' made a print all customer activities request %s\n",


        }
        return null;
    }
}
