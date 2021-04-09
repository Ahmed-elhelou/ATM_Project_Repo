
import java.util.ArrayList;

public class Customer {

    private ArrayList<Activity> activities;
    private BankAccount checkingAcount;
    private BankAccount savingAcount;
    private String number;
    private String PIN;

    public Customer(String PIN, String number) {
        this.checkingAcount = new BankAccount();
        this.savingAcount = new BankAccount();
        this.activities = new ArrayList<>();
        this.PIN = PIN;
        this.number = number;
    }

    public Customer(String PIN, String number, double checkingAccountBalance, double savingAcountBalance) {
        this.checkingAcount = new BankAccount(checkingAccountBalance);
        this.savingAcount = new BankAccount(savingAcountBalance);
        this.activities = new ArrayList<>();
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
        this.activities.add(tr);
    }

    public ArrayList<Activity> getActivitiesList() {
        return this.activities;
    }
}