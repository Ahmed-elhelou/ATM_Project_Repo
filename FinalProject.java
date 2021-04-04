package final_project;

import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.text.ParseException;

public class FinalProject {

    public static void main(String[] args) throws java.io.IOException, ParseException, InputMismatchException {
        // why is this here? shouldn't it be in the bank or in the atm?
        File file = new File(
                "C:\\Users\\HP\\Desktop\\University\\Porgramming II\\Assignments\\Project part 1\\final_project\\BankAccounts.txt");
        Scanner input = new Scanner(file);
        input.useDelimiter(",");
        Bank bank = new Bank();
        while (input.hasNext()) {
            String number = input.next().trim();
            String PIN = input.next();
            double checkingAccountBalance = Double.parseDouble(input.next());
            double savingAcountBalance = Double.parseDouble(input.next());
            bank.addCustomer(PIN, number);
        }

        ATM atm = new ATM(bank);
        atm.loadLog();
        while (atm.getState() != ATM.QUIT) {
            atm.prompts();
        }
    }
}