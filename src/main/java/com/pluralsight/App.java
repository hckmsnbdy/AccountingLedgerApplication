package com.pluralsight;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Transaction> transactions = loadTransactions();

        try {
            boolean running = true;
            while (running) {

                System.out.println();
                System.out.println("=== HOME SCREEN ===");
                System.out.println("D) Add Deposit");
                System.out.println("P) Make Payment (Debit)");
                System.out.println("L) Ledger");
                System.out.println("X) Exit");
                System.out.print("Enter command: ");

                String cmd = scanner.nextLine().trim().toLowerCase();

                if (cmd.equalsIgnoreCase("D")) {
                    addDeposit(scanner);

                } else if (cmd.equalsIgnoreCase("P")) {
                    makePayment(scanner);

                } else if (cmd.equalsIgnoreCase("L")) {
                    showLedgerScreen(transactions,scanner);

                } else if (cmd.equalsIgnoreCase("X")) {
                    running = false;
                    System.out.println("Goodbye!");
                } else {
                    System.out.println("Invalid command. Please try again.");
                }

// Alternative
//                switch (cmd) {
//                    case "D":
//                        addDeposit(scanner,transactions);
//                        break;
//                    case "P":
//                        makePayment(scanner,transactions);
//                        break;
//                    case "L":
//                        showLedgerScreen(scanner);
//                        break;
//                    case "X":
//                        running = false;
//                        System.out.println("Goodbye!");
//                        break;
//                    default:
//                        System.out.println("Invalid command. Please try again.");
//                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public static HashMap<String, Transaction> loadTransactions() {
        HashMap<String,Transaction> transactions = new HashMap<>();
            try {
                BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
                String input;
                while ((input = reader.readLine())!=null) {
                String [] tokens = input.split("\\|");
                    if (!tokens[0].equals("date")) {
                        String date  = tokens[0];
                        String time = tokens[1];
                        String description = tokens[2];
                        String vendor = tokens[3];
                        double amount = Double.parseDouble(tokens[4]);
                        transactions.put(time, new Transaction(date, time, description, vendor, amount));
                    }}
                reader.close();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        return transactions;
    }
    public static void addDeposit(Scanner scanner) {
        System.out.print("Enter Deposit Amount:");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter Vendor:");
        String vendor = scanner.nextLine();

        System.out.print("Enter a Description:");
        String description = scanner.nextLine();

        String date = String.valueOf(LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = LocalTime.now().format(formatter);
        try {
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv", true));
            bufWriter.write(String.format("%s|%s|%s|%s|%.2f%n", date, time, description, vendor, amount));
            bufWriter.close(); //Saves file
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public static void makePayment(Scanner scanner){
        System.out.print("Enter Spent Amount:");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        System.out.print("Enter Vendor:");
        String vendor = scanner.nextLine();

        System.out.print("Enter a Description:");
        String description = scanner.nextLine();

        String date = String.valueOf(LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = LocalTime.now().format(formatter);
        try {
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv", true));
            bufWriter.write(String.format("%s|%s|%s|%s|-%.2f%n", date, time, description, vendor, amount));
            bufWriter.close(); //Saves file
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    public static void showLedgerScreen(HashMap<String, Transaction> transactions,Scanner scanner) {

        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("=== LEDGER ===");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("O) Home");
            System.out.print("Enter command: ");

            String cmd = scanner.nextLine().toUpperCase().trim();

//            if (cmd.equalsIgnoreCase("A")) {
//                showAll();
//            } else if (cmd.equalsIgnoreCase("D")) {
//                deposits();
//            } else if (cmd.equalsIgnoreCase("P")) {
//                payments();
//            } else if (cmd.equalsIgnoreCase("R")) {
//                showReportsScreen(scanner);
//            } else if (cmd.equalsIgnoreCase("0")) {
//                back = false;
//                System.out.println("Goodbye!");
//            } else {
//                System.out.println("Invalid command. Please try again.");
//            }

//Alternative
            switch (cmd) {
                case "A":
                    showAll(transactions);
                    break;
                case "D":
                    displayDeposit(transactions);
                    break;
                case "P":
                    displayPayments(transactions);
                    break;
                case "R":
                    showReportsScreen(transactions,scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }
    public static void showAll(HashMap<String, Transaction> transactions) {
        System.out.println();
        for (Transaction list : transactions.values()){
            displayTransaction(list);
        }
    }
    public static void showReportsScreen(HashMap<String, Transaction> transactions,Scanner scanner) {

        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("=== REPORTS ===");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");
            System.out.print("Enter command: ");

            String cmd = scanner.nextLine().trim();

            switch (cmd) {
                case "1":
                    monthToDate(scanner);
                    break;
                case "2":
                    previousMonth(scanner);
                    break;
                case "3":
                    yearToDate(scanner);
                    break;
                case "4":
                    previousYear(scanner);
                    break;
                case "5":
                    searchbyVendor(transactions, scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }
    public static void monthToDate(Scanner scanner) {
        System.out.println("");
    }                                                                                    //
    public static void previousMonth(Scanner scanner) {

    }                                                                                    //
    public static void yearToDate(Scanner scanner) {

    }                                                                                    //
    public static void previousYear(Scanner scanner) {

    }                                 //
    public static void searchbyVendor(HashMap<String,Transaction> transactions, Scanner scanner) {
        System.out.print("Please enter Vendor:");
        String vendor = scanner.nextLine();
        for (Transaction t: transactions.values()){
            if (t.getVendor().equals(vendor)){
            displayTransaction(t);}
        }

    }                                                                                    //
    public static void displayTransaction(Transaction t) {
        System.out.printf("Date: %s | Time: %s | Description: %s | Vendor: %s | Amount: %.2f%n", t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount()
        );    }
    public static void displayDeposit(HashMap<String, Transaction> transactions) {
        System.out.println();
        System.out.println("=== DEPOSITS ===");
        for (Transaction t : transactions.values()) {
            if (t.getAmount() > 0) {
                displayTransaction(t);
            }
        }
    }
    public static void displayPayments(HashMap<String, Transaction> transactions) {
        System.out.println();
        System.out.println("=== Payments ===");
        for (Transaction t : transactions.values()) {
            if (t.getAmount() < 0) {
                displayTransaction(t);
            }
        }
    }
}
