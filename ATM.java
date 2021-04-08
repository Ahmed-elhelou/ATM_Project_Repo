
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.security.*;

public class ATM {

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

    public ATM(Bank currentBank, File logFile) {
        this.state = ATM.START;
        this.currentBank = currentBank;
        this.input = new Scanner(System.in);
        this.logArray = new ArrayList<>();
        this.logFile = logFile;
        this.loadLog();
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

    public BankAccount getCurrentAccount() {
        return this.currentAccount;
    }

    public Customer getCurrentCustomer() {
        return this.currentCustomer;
    }

    public void loadLog() {
        try (Scanner tempScanner = new Scanner(this.logFile)) {
            while (tempScanner.hasNextLine()) {
                String s = tempScanner.nextLine();
                Activity activity = Activity.lineToActivity(s);
                logArray.add(activity);
            }
        } catch (FileNotFoundException fEx) {
            fEx.getStackTrace();
        } catch (ParseException pEx) {
            pEx.getStackTrace();
        }
        numberOfSavedActivities = logArray.size();
        numberOfNewActivities = 0;
    }

    public void saveLog() {

        try (PrintWriter logWriter = new PrintWriter(new FileOutputStream(logFile, true))) {
            for (int i = numberOfSavedActivities; i < numberOfSavedActivities + numberOfNewActivities; i++) {
                String s = this.logArray.get(i).toString();
                if (s != null)
                    logWriter.println(s);
            }
        } catch (FileNotFoundException ex) {
            ex.getStackTrace();
        }

        numberOfSavedActivities += numberOfNewActivities;
        numberOfNewActivities = 0;
    }

    public void start(String currentNumber) {
        this.currentNumber = currentNumber;
        this.setState(ATM.PIN);
        this.logArray.add(new Activity('A', this.currentNumber));
        numberOfNewActivities++; // i think to put it in add() method
    }

    public boolean pinState(String currentPIN) {
        this.currentCustomer = this.currentBank.getCustomer(Bank.hash(currentPIN), this.currentNumber);
        if (this.currentCustomer != null) {
            this.setState(ATM.ACCOUNT);
            this.currentNumber = this.currentCustomer.getNumber();
            currentActivity = new Activity('L', this.currentNumber);
            this.logArray.add(currentActivity);
            this.currentCustomer.addActivity(currentActivity);
            numberOfNewActivities++;
            return true;
        } else {
            this.setState(ATM.START);
            this.logArray.add(new Activity('F', this.currentNumber));
            numberOfNewActivities++;
            return false;
        }
    }

    public void accountState(char option) {
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
            this.currentBank.save();
            break;
        }
    }

    public void depositState(double ammount) {
        currentAccount.deposits(ammount);
        currentActivity = new Transaction('D', this.currentCustomer, ammount, this.currentAccount.getBalance(),
                this.currentAccountType);
        this.currentCustomer.addActivity(currentActivity);
        this.logArray.add(currentActivity);

        this.state = ATM.TRANSACT;
        numberOfNewActivities++;
    }

    public void withdrawalsState(double ammount) {

        currentAccount.withdrawals(ammount);
        currentActivity = new Transaction('W', this.currentCustomer, ammount, this.currentAccount.getBalance(),
                this.currentAccountType);
        this.currentCustomer.addActivity(currentActivity);
        this.logArray.add(currentActivity);
        this.state = ATM.TRANSACT;
        numberOfNewActivities++;
    }

    public ArrayList<Activity> getAccountSummary() {
        ArrayList<Activity> customerActivity = currentCustomer.getTransactionsList();
        ArrayList<Activity> result = new ArrayList<>();
        for (Activity element : customerActivity) {
            if (element.toString().contains(this.currentAccountType + "Account")) { // CheckingAccount

                result.add(element);
            }
        }
        this.logArray.add(new Activity('P', this.currentNumber));
        numberOfNewActivities++;
        return result;
    }

    public void backToAccountState() {

        this.state = ATM.ACCOUNT;
        this.logArray.add(new Activity('B', this.currentCustomer.getNumber()));
        numberOfNewActivities++;
    }

    public ArrayList<Activity> getLatest(char typeChar, char activityChar) {
        ArrayList<Activity> customerActivitiesArray = this.currentCustomer.getTransactionsList();
        ArrayList<Activity> result = new ArrayList<>();
        for (int i = 0; i < customerActivitiesArray.size(); i++) {
            if (customerActivitiesArray.get(i).getType() == typeChar) {
                result.add(customerActivitiesArray.get(i));
            }
        }
        this.logArray.add(new Activity(activityChar, this.currentCustomer.getNumber()));
        numberOfNewActivities++;
        return result;
    }

    public ArrayList<Activity> getFullActivity() {

        String[] activityString;
        ArrayList<Activity> result = new ArrayList<>();
        for (Activity element : logArray) {

            String s2 = element.toString();
            if (s2 != null) {
                activityString = s2.split(" ");
                if (activityString[2].equals(this.currentCustomer.getNumber())) {
                    result.add(element);
                }
            }
        }
        return result;
    }

}