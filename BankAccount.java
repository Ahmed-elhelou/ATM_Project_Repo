package final_project;

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

}