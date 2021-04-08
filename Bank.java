
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.security.*;

public class Bank {
    ArrayList<Customer> customers = new ArrayList<>();
    private File file;

    public Bank(File file) {
        try {
            this.file = file;
            Scanner input = new Scanner(this.file);
            input.useDelimiter(",");
            while (input.hasNext()) {
                String number = input.next().trim();
                String hashedPIN = input.next();
                double checkingAccountBalance = Double.parseDouble(input.next());
                double savingAcountBalance = Double.parseDouble(input.next());
                this.addCustomer(hashedPIN, number, checkingAccountBalance, savingAcountBalance);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }
    //loading a customer from the file, since the PIN is already hashed, take it as it is and add it to the list
    public void loadCustomer(String hashedPIN, String number, double checkingAccountBalance, double savingAcountBalance) {
        this.customers.add(new Customer(hashedPIN, number, checkingAccountBalance, savingAcountBalance));
    }
    //adding a new customer to the bank, we take his PIN and hash it, then save it to the new customer, then add him to the list
    public void addCustomer(String PIN, String number, double checkingAccountBalance, double savingAcountBalance) throws Exception {
        for (int i = 0; i < this.customers.size(); i++) {
            if (this.customers.get(i).getNumber().equals(number)) {
                System.out.println("this number: " + number + " is already registered!");
                return;
            }
        }
        customers.add(new Customer(Bank.hash(PIN), number, checkingAccountBalance, savingAcountBalance));
    }

    public static boolean cheackEnteries(Customer customer, String PIN, String number) throws Exception {
        return (customer.getHashedPIN().equals(Bank.hash(PIN))) && (customer.getNumber().equals(number));
    }

    public Customer getCustomer(String PIN, String number) throws Exception {
        for (int i = 0; i < this.customers.size(); i++) {
            if (Bank.cheackEnteries(this.customers.get(i), PIN, number))
                return this.customers.get(i);
        }
        return null;
    }

    public void save() {
        try (PrintWriter outBankAccounts = new PrintWriter(this.file)) {
            for (int i = 0; i < customers.size(); i++) {
                Customer currentCustomer = customers.get(i);
                outBankAccounts.printf("%s,%s,%f,%f,\n", currentCustomer.getNumber(), currentCustomer.getHashedPIN(),
                        currentCustomer.getCheckingAcount().getBalance(),
                        currentCustomer.getSavingAcount().getBalance());
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }

    }
    private static String hash(String text) throws Exception{
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        messageDigest.update(text.getBytes());
        String result = String.valueOf(messageDigest.digest());
        messageDigest.reset();
        return result;
    }

}