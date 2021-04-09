
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileOutputStream;

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

    private ArrayList<Activity> logArray;
    private File logFile;
    private Activity currentActivity;
    private int numberOfNewActivities;
    private int numberOfSavedActivities;

    public ATM(Bank currentBank, File logFile) {
        this.state = ATM.START;
        this.currentBank = currentBank;
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

    private void loadLog() {
        try (Scanner tempScanner = new Scanner(this.logFile)) {
            while (tempScanner.hasNextLine()) {
                String s = tempScanner.nextLine();
                Activity activity = Activity.lineToActivity(s);
                logArray.add(activity);
            }
        } catch (FileNotFoundException fEx) {
            fEx.getStackTrace();
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        numberOfSavedActivities = logArray.size();
        numberOfNewActivities = 0;
    }

    private void saveLog() {

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
        this.addToLogArray(new Activity('A', this.currentNumber));
    }

    public boolean pinState(String currentPIN) {
        this.currentCustomer = this.currentBank.getCustomer(Bank.hash(currentPIN), this.currentNumber);
        if (this.currentCustomer != null) {
            this.setState(ATM.ACCOUNT);
            this.currentNumber = this.currentCustomer.getNumber();
            currentActivity = new Activity('L', this.currentNumber);
            this.currentCustomer.addActivity(currentActivity);
            this.addToLogArray(currentActivity);
            return true;
        } else {
            this.setState(ATM.START);
            this.addToLogArray(new Activity('F', this.currentNumber));
            return false;
        }
    }

    public void accountState(char option) {
        switch (option) {
        case 'a':
            this.currentAccount = this.currentCustomer.getCheckingAcount();
            currentAccountType = ATM.CHECKING;
            this.state = ATM.TRANSACT;
            this.addToLogArray(new Activity('C', this.currentNumber));
            break;
        case 'b':
            this.currentAccount = this.currentCustomer.getSavingAcount();
            currentAccountType = ATM.SAVING;
            this.state = ATM.TRANSACT;
            this.addToLogArray(new Activity('S', this.currentNumber));
            break;
        case 'c':
            this.state = ATM.SUMMARY;
            this.addToLogArray(new Activity('P', this.currentNumber));

            break;
        case 'd':
            this.addToLogArray(new Activity('Q', this.currentNumber));
            this.state = ATM.START;
            this.currentCustomer = null;
            this.currentAccount = null;
            this.currentNumber = null;
            saveLog();
            break;
        }
    }

    public void depositState(double ammount) {
        currentAccount.deposits(ammount);
        this.currentBank.save();
        currentActivity = new Transaction('D', this.currentCustomer, ammount, this.currentAccount.getBalance(),
                this.currentAccountType);
        this.currentCustomer.addActivity(currentActivity);
        this.state = ATM.TRANSACT;
        this.addToLogArray(currentActivity);
    }

    public void withdrawalsState(double ammount) {

        currentAccount.withdrawals(ammount);
        this.currentBank.save();
        currentActivity = new Transaction('W', this.currentCustomer, ammount, this.currentAccount.getBalance(),
                this.currentAccountType);
        this.currentCustomer.addActivity(currentActivity);
        this.state = ATM.TRANSACT;
        this.addToLogArray(currentActivity);
    }

    public ArrayList<Activity> getAccountSummary() {
        ArrayList<Activity> customerActivity = currentCustomer.getActivitiesList();
        ArrayList<Activity> result = new ArrayList<>();
        for (Activity element : customerActivity) {
            if (element.toString().contains(this.currentAccountType)) { // CheckingAccount

                result.add(element);
            }
        }

        this.addToLogArray(new Activity('P', this.currentNumber));
        return result;
    }

    public void backToAccountState() {

        this.state = ATM.ACCOUNT;
        this.addToLogArray(new Activity('B', this.currentCustomer.getNumber()));
    }

    public ArrayList<Activity> getLatest(char typeChar) {
        char activityChar = (typeChar == 'D') ? 'X' : 'Y';

        ArrayList<Activity> customerActivitiesArray = this.currentCustomer.getActivitiesList();
        ArrayList<Activity> result = new ArrayList<>();
        for (int i = 0; i < customerActivitiesArray.size(); i++) {
            if (customerActivitiesArray.get(i).getType() == typeChar) {
                result.add(customerActivitiesArray.get(i));
            }
        }

        this.addToLogArray(new Activity(activityChar, this.currentCustomer.getNumber()));
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

        this.addToLogArray(new Activity('Z', this.currentNumber));
        return result;
    }

    private void addToLogArray(Activity act) {
        this.numberOfNewActivities++;
        this.logArray.add(act);
    }

}