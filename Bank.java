
import java.io.File;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;

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
                String PIN = input.next();
                double checkingAccountBalance = Double.parseDouble(input.next());
                double savingAcountBalance = Double.parseDouble(input.next());
                this.addCustomer(PIN, number, checkingAccountBalance, savingAcountBalance);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    public void addCustomer(String PIN, String number, double checkingAccountBalance, double savingAcountBalance) {

        customers.add(new Customer(PIN, number, checkingAccountBalance, savingAcountBalance));
        save();
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

    public void save() {
        try (PrintWriter outBankAccounts = new PrintWriter(this.file)) {
            for (int i = 0; i < customers.size(); i++) {
                Customer currentCustomer = customers.get(i);
                outBankAccounts.printf("%s,%s,%f,%f,\n", currentCustomer.getNumber(), currentCustomer.getPIN(),
                        currentCustomer.getCheckingAcount().getBalance(),
                        currentCustomer.getSavingAcount().getBalance());
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }

    }

    public static String hash(String text) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        messageDigest.update(text.getBytes());
        byte[] result = messageDigest.digest();

        return byteToString(result);
    }

    private static String byteToString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i] + "");
        }
        return new String(sb);
    }

}