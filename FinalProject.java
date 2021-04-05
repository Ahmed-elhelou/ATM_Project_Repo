
import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.text.ParseException;

public class FinalProject {

    public static void main(String[] args) throws java.io.IOException, ParseException, InputMismatchException {
        // why is this here? shouldn't it be in the bank or in the atm?
        File file = new File("BankAccounts.txt");
        Bank bank = new Bank(file);

        ATM atm = new ATM(bank);
        atm.loadLog();
        while (atm.getState() != ATM.QUIT) {
            atm.prompts();
        }
    }
}