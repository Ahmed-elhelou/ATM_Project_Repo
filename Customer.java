
import java.util.ArrayList;

public class Customer {

    private ArrayList<Activity> transactions;
    private BankAccount checkingAcount;
    private BankAccount savingAcount;
    private String number;
    private String hashedPIN;

    public Customer(String hashedPIN, String number) {
        this.checkingAcount = new BankAccount();
        this.savingAcount = new BankAccount();
        this.transactions = new ArrayList<>();
        this.hashedPIN = hashedPIN;
        this.number = number;
    }

    public Customer(String hashedPIN, String number, double checkingAccountBalance, double savingAcountBalance) {
        this.checkingAcount = new BankAccount(checkingAccountBalance);
        this.savingAcount = new BankAccount(savingAcountBalance);
        this.transactions = new ArrayList<>();
        this.hashedPIN = hashedPIN;
        this.number = number;
    }

    public String getHashedPIN() {
        return this.hashedPIN;
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