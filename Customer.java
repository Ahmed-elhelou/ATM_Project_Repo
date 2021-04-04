package final_project;

import java.util.ArrayList;

class Customer {

    private ArrayList<Activity> transactions;
    private BankAccount checkingAcount;
    private BankAccount savingAcount;
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

    public ArrayList<Activity> getTransactionsList() {
        return this.transactions;
    }
}