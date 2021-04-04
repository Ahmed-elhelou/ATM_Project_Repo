package final_project;

import java.text.ParseException;

class Transaction extends Activity {
    private double amount;
    private double currentBalance;
    private String accountType;
    private Customer currentCustomer;

    public Transaction(char type, Customer currentCustomer, double amount, double balance, String accountType) {
        super(type, "" + currentCustomer.getNumber());
        this.amount = amount;
        this.currentBalance = balance;
        this.accountType = accountType;
        this.currentCustomer = currentCustomer;
    }

    public Transaction(char type, Customer currentCustomer, double amount, double balance, String accountType,
            String date) throws ParseException {
        super(type, "" + currentCustomer.getNumber(), date);
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
            return String.format("Account number %s D %sAccount Diposit amount %.2f Current balance %.2f %s\n",
                    currentCustomer.getNumber(), this.accountType, this.amount, this.currentBalance, date.toString());

        case 'W': // Withdrawal
            return String.format("Account number %s W %sAccount Withdrawal amount %.2f Current balance %.2f %s\n",
                    currentCustomer.getNumber(), this.accountType, this.amount, this.currentBalance, date.toString());
        }
        return null;
    }

}