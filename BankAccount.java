
public class BankAccount {
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

}