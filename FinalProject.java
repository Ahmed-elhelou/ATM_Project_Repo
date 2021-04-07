
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FinalProject {

    public static void main(String[] args) throws FileNotFoundException {
        Scanner input = new Scanner(System.in);

        File bankAccountFile = new File("BankAccounts.txt");
        File logFile = new File("Log.txt");
        Bank bank = new Bank(bankAccountFile);
        ATM atm = new ATM(bank, logFile);

        while (atm.getState() != ATM.QUIT) {
            switch (atm.getState()) {
            case ATM.START:
                System.out.print("Enter customer ID : ");
                atm.start(input.next());
                break;
            case ATM.PIN:
                System.out.print("Enter PIN: ");
                boolean success = atm.pinState(input.next());
                if (!success)
                    System.out.println("Your Number Or PIN Not Correct, Please Try Again");
                break;

            case ATM.ACCOUNT:
                System.out.print("A=Checking-Account, B=Savings-Account, C=Get-customers-activity-summary, D=Logout: ");
                char option = Character.toLowerCase(input.next().charAt(0));
                atm.accountState(option);
                break;

            case ATM.TRANSACT:

                System.out.println("Current Balance = " + atm.getCurrentAccount().getBalance());
                System.out.print("A=Deposit, B=Withdraw, C=Print-account-summary D=Back: ");
                option = Character.toLowerCase(input.next().charAt(0));
                double amount;
                switch (option) {
                case 'a':
                    System.out.print("Amount: ");
                    amount = input.nextDouble();
                    atm.depositState(amount);
                    break;
                case 'b':
                    System.out.print("Amount: ");
                    amount = input.nextDouble();
                    atm.withdrawalsState(amount);
                    break;
                case 'c':
                    Activity.printlnArray(atm.getAccountSummary());

                    break;
                case 'd':
                    atm.backToAccountState();
                    break;
                }
                break;

            case ATM.SUMMARY:
                System.out.print(
                        "A=Print-latest-deposites, B=Print-latest-withdrawals, C=Get full activity summary, D=Back: ");
                option = Character.toLowerCase(input.next().charAt(0));

                switch (option) {
                case 'a':
                    Activity.printlnArray(atm.getLatest('D', 'X'));
                    break;

                case 'b':
                    Activity.printlnArray(atm.getLatest('W', 'Y'));
                    break;

                case 'c':
                    Activity.printlnArray(atm.getFullActivity());
                    break;

                case 'd':
                    atm.backToAccountState();

                    break;
                }

                break;

            }
        }
    }

}