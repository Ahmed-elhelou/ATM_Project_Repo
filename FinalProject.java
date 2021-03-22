import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.*;

//adding some test notes
public class FinalProject {

    public static void main(String[] args) throws java.io.IOException {

        File file = new File("BankAccounts.txt");
        Scanner input = new Scanner(file);
        input.useDelimiter(",");
        Bank bank = new Bank();
        while (input.hasNext()) {
            String number = input.next().trim();
            String PIN = input.next();
            double checkingAccountBalance = input.nextDouble();
            double savingAcountBalance = input.nextDouble();
            bank.addCustomer(PIN, number);
        }
        // change 1
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

    public void deposits(double amount) {
        this.balance += amount;
    }

    public void withdrawals(double amount) {
        this.balance -= amount;
    }

    public double getBalance() {
        return this.balance;
    }

    public double test() {
        return this.balance;
    }

}

class Customer {

    private ArrayList<Activity> transactions;
    private BankAccount checkingAcount;
    private BankAccount savingAcount;
    // change 2
    private String number;
    private String PIN;

    public Customer(String PIN, String number) {
        this.checkingAcount = new BankAccount();
        this.savingAcount = new BankAccount();
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

    public ArrayList<Activity> getArrayList() {
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
    public final static String QUIT = "QUIT";
    public final static String SUMMARY = "SUMMARY";

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
        logFile = new File("Log.text");
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public void prompts() {

        switch (this.state) {
        case ATM.START:
            System.out.print("Enter customer ID : ");
            this.currentNumber = this.input.nextInt();
            this.setState(ATM.PIN);
            this.logArray.add(new Activity('A'));
            break;
        case ATM.PIN:
            System.out.print("Enter PIN: ");
            this.currentPIN = this.input.next();
            this.currentCustomer = this.currentBank.getCustomer(currentPIN, currentNumber);
            if (this.currentCustomer != null) {
                this.setState = ATM.ACCOUNT;
                this.currentNumber = this.currentCustomer.getNumber();
                currentActivity = new Activity('L', this.currentNumber);
                this.logArray.add(currentActivity);
                this.currentCustomer.addActivity(currentActivity);
            } else {
                this.setState = ATM.START;
                this.logArray.add(new Activity('F', this.currentNumber));
            }

        case ATM.ACCOUNT:
            System.out.print("A=Checking, B=Savings, C=GetSummary, D=Logout: ");
            char option = Character.toLowerCase(this.input.next().charAt(0));
            switch (option) {
            case 'a':
                this.currentAccount = this.currentCustomer.getCheckingAcount();
                currentAccountType = "checking";
                this.state = ATM.TRANSACT;
                break;
                this.logArray.add(new Activity('C', this.currentNumber));
            case 'b':
                this.currentAccount = this.currentCustomer.getSavingAcount();
                currentAccountType = "saving";
                this.state = ATM.TRANSACT;
                break;
                this.logArray.add(new Activity('S', this.currentNumber));
            case 'c':
                this.state = ATM.SUMMARY;
                this.currentCustomer.printSummary();
                this.logArray.add(new Activity('P', this.currentNumber));
                break;
            case 'd':
                this.logArray.add(new Activity('Q', this.currentNumber));
                this.state = ATM.START;
                this.currentCustomer = null;
                this.currentAccount = null;
                this.currentNumber = -1;
                this.currentPIN = null;
                saveLog();
                break;
            }
            break;
        case ATM.SUMMARY:
            System.out.print("A=deposites, B=withdrawals, C=full Summary, D=back: ");
            char option2 = Character.toLowerCase(this.input.next().charAt(0));
            ArrayList<Activity> customerArray = this.currentCustomer.getArrayList();
            switch (option2) {
            case 'a':
            for (int i = 0; i < customerArray.size(); i++) {
                if(customerArray.get(i).getType() == 'd'){
                    customerArray.get(i).toString();
                }
            }
                
                break;
            case 'b':
            for (int i = 0; i < customerArray.size(); i++) {
                if(customerArray.get(i).getType() == 'w'){
                    customerArray.get(i).toString();
                }
            }

                break;
            case 'c': 
                for (int i = 0; i < customerArray.size(); i++) {
                
                        customerArray.get(i).toString();
                    
                }
                break;
            case 'd':
                this.state = ATM.ACCOUNT;
                break;
            }

            break;
        case ATM.TRANSACT:

            System.out.println("Balance = " + this.currentAccount.getBalance());
            System.out.print("A=Deposit, B=Withdrawal, C=Cancel: ");
            option = Character.toLowerCase(this.input.next().charAt(0));
            double amount;
            switch (option) {
            case 'a':
                System.out.print("Amount: ");
                amount = this.input.nextDouble();
                currentAccount.deposits(amount);
                currentActivity = new Transaction('D', amount, this.currentAccount.getBalance(),
                        this.currentAccountType);
                this.currentCustomer.addActivity(currentActivity);
                this.logArray.add(currentActivity);
                this.currentAccount = null;
                this.state = ATM.ACCOUNT;
                break;
            case 'b':
                System.out.print("Amount: ");
                amount = this.input.nextDouble();
                currentAccount.withdrawals(amount);
                currentActivity = new Transaction('W', amount, this.currentAccount.getBalance(), this.currentAccountType);
                this.currentCustomer.addActivity(currentActivity);
                this.logArray.add(currentActivity);
                this.currentAccount = null;
                this.state = ATM.ACCOUNT;
                break;
            case 'c':
                this.state = ATM.ACCOUNT;
                this.logArray.add(new Activity('B', ));
                break;
            }
            break;

        }

    }

    public void loadLog() {
        try (Scanner tempScanner = new Scanner(this.logFile)) {
            while (tempScanner.hasNextLine()) {
                String s = tempScanner.nextLine();
                Transaction t3 = lineToTransaction(s);
                logArray.add(e);
            }
        }
    }

    public void saveLog() {
        try (FileOutputStream logWriter = new FileOutputStream("Log.txt")) {
            int count = 0;
            while (count < this.logArray.size()) {
                logWriter.write(this.logArray.get(count++));
            }
        }
    }

    private Activity lineToTransaction(String s) {
        // take the line s, use charAt and next() to split the line into variables, then
        // use the variables to create a transaction
        // return Transaction
        return transaction;
    }

}

class Transaction extends Activity {
    private double amount;
    private double currentBalance;
    private String accountType;
    private Customer currentCustomer;

    public Transaction(char type, Customer currentCustomer, double amount, double balance, String accountType) {
        super(type);
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
            return String.format("Account number:%s PIN:%s %sAccount Diposite amount:%f Current balance:%f %s",
                    currentCustomer.getNumber(), currentCustomer.getPIN(), this.accountType, this.amount,
                    this.currentBalance, date.toString());

        case 'W': // Withdrawal
            return String.format("Account number:%s PIN:%s %sAccount Withdrawal amount:%f Current balance:%f %s",
                    currentCustomer.getNumber(), currentCustomer.getPIN(), this.accountType, this.amount,
                    this.currentBalance, date.toString());
        }
        return null;
    }

}

class Activity {
    protected java.util.Date date;
    protected char type;
    protected String currentNumber;

    public Activity(char type, String currentNumber) { // for failed attempt, takes the current number as a String
                                                       // instead of currentCustomer.getNumber
        this.type = type;
        date = new java.util.Date();
        this.currentNumber = currentNumber;
    }

    public char getType() {
        return this.type;
    }

    @Override
    public String toString() {
        switch (this.type) {

        case 'A': // Attempt login
            return String.format("Account number:%s login-Attempt %s", this.currentNumber, date.toString());

        case 'F': // failed login
            return String.format("Account number:%s failed-login %s", this.currentNumber, date.toString());

        case 'L': // Login successful
            return String.format("Account number:%s successful-login %s", this.currentNumber, date.toString());

        case 'C': // CheckingAccount access
            return String.format("Account number:%s Accessed checking account %s", this.currentNumber, date.toString());

        case 'S': // SavingAccount access
            return String.format("Account number:%s Accessed saving account %s", this.currentNumber, date.toString());

        case 'P': // PrintSummary
            return String.format("Account number:%s requested Account summary %s", this.currentNumber, date.toString());

        case 'B': // Back to account screen
            return String.format("Account number:%s went back to accounts screen %s", currentCustomer.getNumber(),
                    date.toString());

        case 'Q': // Quit
            return new String.format("Account number:%s PIN:%s logout %s", currentCustomer.getNumber(),
                    currentCustomer.getPIN(), date.toString());

        }
        return null;
    }
}