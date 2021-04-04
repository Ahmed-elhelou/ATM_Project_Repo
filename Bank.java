package final_project;

import java.util.ArrayList;

class Bank {
    ArrayList<Customer> customers = new ArrayList<>();

    public void addCustomer(String PIN, String number) {
        for (int i = 0; i < this.customers.size(); i++) {
            if (this.customers.get(i).getNumber().equals(number)) {
                System.out.println("this number: " + number + " is already registered!");
                return;
            }
        }
        customers.add(new Customer(PIN, number));
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
}