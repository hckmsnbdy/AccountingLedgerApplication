package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;
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

                String cmd = scanner.nextLine().trim();

                switch (cmd) {
                    case "D":
                        addDeposit(scanner,transactions);
                        break;
                    case "P":
                        makePayment(scanner,transactions);
                        break;
                    case "L":
                        showLedgerScreen(scanner);
                        break;
                    case "X":
                        running = false;
                        System.out.println("Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid command. Please try again.");
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private static HashMap<String, Transaction> loadTransactions() {
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
                        transactions.put(date, new Transaction(date, time, description, vendor, amount));
                    }}
                reader.close();
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        return transactions;
    }
    private static void addDeposit(Scanner scanner, HashMap<String, Transaction> transactions) {}
    private static void makePayment(Scanner scanner, HashMap<String, Transaction> transactions){}
    private static void showLedgerScreen(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("=== LEDGER ===");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("0) Home");
            System.out.print("Enter command: ");

            String cmd = scanner.nextLine().trim();

            switch (cmd) {
                case "A":
                    showAll();
                    break;
                case "D":
                    System.out.print("Enter a name or part of it: ");
                    deposits();
                    break;
                case "P":
                    System.out.print("Enter a department or part of it: ");
                    payments();
                    break;
                case "R":
                    System.out.print("Enter SKU: ");
                    showReportsScreen(scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid command. Please choose 1-6.");
            }
        }
    }
    private static void showAll(){}
    private static void deposits(){}
    private static void payments(){}
    private static void showReportsScreen(Scanner scanner) {

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
                    System.out.print("Enter a name or part of it: ");
                    previousMonth(scanner);
                    break;
                case "3":
                    System.out.print("Enter a department or part of it: ");
                    yearToDate(scanner);
                    break;
                case "4":
                    System.out.print("Enter SKU: ");
                    previousYear(scanner);
                    break;
                case "5":
                    searchbyVendor(scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }
    private static void monthToDate(Scanner scanner) {}
    private static void previousMonth(Scanner scanner) {}
    private static void yearToDate(Scanner scanner) {}
    private static void previousYear(Scanner scanner) {}
    private static void searchbyVendor(Scanner scanner) {}






}
