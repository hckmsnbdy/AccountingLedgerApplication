package com.pluralsight;
// Core IO for file reading/writing
import java.io.*;
// For formatting times (HH:mm:ss)
import java.time.format.DateTimeFormatter;
// HashMap for in-memory transaction storage
import java.util.HashMap;
// Scanner for console input
import java.util.Scanner;
// LocalDate/LocalTime for dates and times
import java.time.LocalDate;
import java.time.LocalTime;

public class App {
    public static void main(String[] args) {
        // One Scanner for the whole app to read user input from console
        Scanner scanner = new Scanner(System.in);
        // Load existing transactions from CSV into memory (HashMap)
        HashMap<String, Transaction> transactions = loadTransactions();

        try {
            // Outer application loop, runs until user chooses Exit
            boolean running = true;
            while (running) {
                //  Home Screen Menu
                System.out.println();
                System.out.println("=== HOME SCREEN ===");
                System.out.println("D) Add Deposit");
                System.out.println("P) Make Payment (Debit)");
                System.out.println("L) Ledger");
                System.out.println("X) Exit");
                System.out.print("Enter command: ");

                // Read command and normalize spaces/case
                String cmd = scanner.nextLine().trim().toLowerCase();

                // Branch by command (case-insensitive)
                if (cmd.equalsIgnoreCase("D")) {
                    // Add a positive transaction, also append to CSV and update in-memory map
                    addDeposit(scanner, transactions);

                } else if (cmd.equalsIgnoreCase("P")) {
                    // Add a negative transaction (payment), CSV + in-memory map
                    makePayment(scanner, transactions);

                } else if (cmd.equalsIgnoreCase("L")) {
                    // Open the ledger sub-menu (All / Deposits / Payments / Reports)
                    showLedgerScreen(transactions, scanner);

                } else if (cmd.equalsIgnoreCase("X")) {
                    // Exit the app loop
                    running = false;
                    System.out.println("Goodbye!");
                } else {
                    // Any other input is invalid
                    System.out.println("Invalid command. Please try again.");
                }

            }
        } catch (Exception e) {
            // Catch to avoid crashing app; prints the message for quick debugging
            System.out.println(e.getMessage());
        }
    }
    public static HashMap<String, Transaction> loadTransactions() {
        // HashMap acts as an in-memory “database” of transactions
        HashMap<String, Transaction> transactions = new HashMap<>();
        try {
            // Open the CSV file
            BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/transactions.csv"));
            String input;

            // Read the file line-by-line
            while ((input = reader.readLine()) != null) {
                // CSV is pipe-delimited: date|time|description|vendor|amount
                String[] tokens = input.split("\\|");

                // Skip header line if present (first token equals "date")
                if (!tokens[0].equals("date")) {
                    String date = tokens[0];
                    String time = tokens[1];
                    String description = tokens[2];
                    String vendor = tokens[3];
                    // Convert amount string to double
                    double amount = Double.parseDouble(tokens[4]);
                    // Store it in the map using time as the key
                    transactions.put(time, new Transaction(date, time, description, vendor, amount));

                }
            }
            // Close the file
            reader.close();
        } catch (Exception e) {
            // Print any load errors for quick feedback
            System.out.println(e.getMessage());
        }
        // Return the in-memory map for the rest of the app to use
        return transactions;
    }
    public static void addDeposit(Scanner scanner, HashMap<String, Transaction> transactions) {
        // Prompt user for a positive amount
        System.out.print("Enter Deposit Amount:");
        double amount = scanner.nextDouble();
        // Consume leftover newline from nextDouble()
        scanner.nextLine();

        // Vendor name
        System.out.print("Enter Vendor:");
        String vendor = scanner.nextLine();

        //Description
        System.out.print("Enter a Description:");
        String description = scanner.nextLine();

        // Generate date/time stamps for this record
        String date = String.valueOf(LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = LocalTime.now().format(formatter);

        // Append the new deposit to the CSV file
        try {
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv", true));
            // Write format: date|time|description|vendor|amount
            bufWriter.write(String.format("%s|%s|%s|%s|%.2f%n", date, time, description, vendor, amount));
            bufWriter.close(); //Saves file
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        // Ensure positive amount in memory
        double positive = Math.abs(amount);
        Transaction transaction = new Transaction(date, time, description, vendor, positive);
        transactions.put(time, transaction);

    }
    public static void makePayment(Scanner scanner, HashMap<String, Transaction> transactions) {
        // Prompt for spending amount (user can type positive or negative
        System.out.print("Enter Spent Amount:");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        // Vendor for the payment
        System.out.print("Enter Vendor:");
        String vendor = scanner.nextLine();

        // Description for the payment
        System.out.print("Enter a Description:");
        String description = scanner.nextLine();

        // Date/time stamps
        String date = String.valueOf(LocalDate.now());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = LocalTime.now().format(formatter);

        // Append the new payment to CSV with a negative amount
        try {
            BufferedWriter bufWriter = new BufferedWriter(new FileWriter("src/main/resources/transactions.csv", true));
            // The CSV line forces negative amount with "-%.2f"
            bufWriter.write(String.format("%s|%s|%s|%s|-%.2f%n", date, time, description, vendor, amount));
            bufWriter.close(); //Saves file
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Negative even if user typed a negative
        double negative = -Math.abs(amount);
        Transaction transaction = new Transaction(date, time, description, vendor, negative);

        // Insert into the map so the UI immediately reflects the new payment
        transactions.put(time, transaction);
    }
    public static void showLedgerScreen(HashMap<String, Transaction> transactions, Scanner scanner) {
        // Inner menu for ledger-related screens and reports
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

            // Read command for the ledger section
            String cmd = scanner.nextLine().toUpperCase().trim();

            // Use a switch for clarity
            switch (cmd) {
                case "A":
                    // Show all transactions without filtering
                    showAll(transactions);
                    break;
                case "D":
                    // Show only positive transactions
                    displayDeposit(transactions);
                    break;
                case "P":
                    // Show only negative transactions
                    displayPayments(transactions);
                    break;
                case "R":
                    // Open reports sub-menu (MTD, Prev Month, YTD, Prev Year, Search)
                    showReportsScreen(transactions, scanner);
                    break;
                case "0":
                    // Return to the home screen
                    back = true;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }
    public static void showAll(HashMap<String, Transaction> transactions) {
        // Iteration over the map’s values
        System.out.println();
        for (Transaction list : transactions.values()) {
            // Use a common printer so the format stays consistent
            displayTransaction(list);
        }
    }
    public static void showReportsScreen(HashMap<String, Transaction> transactions, Scanner scanner) {
        // Reports sub-menu loop
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

            // Read report selection
            String cmd = scanner.nextLine().trim();

            switch (cmd) {
                case "1":
                    // Filter: same year and same month as today
                    monthToDate(transactions);
                    break;
                case "2":
                    // Filter: the month right before the current month
                    previousMonth(transactions);
                    break;
                case "3":
                    // Filter: Jan 1 of this year up to and including today
                    yearToDate(transactions);
                    break;
                case "4":
                    // Filter: all transactions from last calendar year
                    previousYear(transactions);
                    break;
                case "5":
                    // Vendor search
                    searchbyVendor(transactions, scanner);
                    break;
                case "0":
                    // Go back to ledger menu
                    back = true;
                    break;
                default:
                    System.out.println("Invalid command. Please try again.");
            }
        }
    }
    public static void monthToDate(HashMap<String, Transaction> transactions) {
        // Get today's year and month using split()
        String[] currentDate = String.valueOf(LocalDate.now()).split("-");
        int currentYear  = Integer.parseInt(currentDate[0]);
        int currentMonth = Integer.parseInt(currentDate[1]);

        // Iterate through all transactions and filter by current month/year
        for (Transaction t: transactions.values()){
            try {
                // Take the date from the transaction
                String transactionDate = t.getDate();
                // Split
                String[] dateParts = transactionDate.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);

                // Keep only transactions in the same YEAR and MONTH as today
                if (year == currentYear && month == currentMonth) {
                    displayTransaction(t);
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public static void previousMonth(HashMap<String, Transaction> transactions) {
        // Find the previous month and year
        LocalDate today = LocalDate.now();
        LocalDate prev  = today.minusMonths(1); //
        int targetYear  = prev.getYear();
        int targetMonth = prev.getMonthValue();

        // Loop through the in-memory transactions, filter by target year and target month
        for (Transaction t : transactions.values()) {
            try {
                String transactionDate = t.getDate();
                // Split
                String[] dateParts = transactionDate.split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                // Keep only those in previous month
                if (year == targetYear && month == targetMonth) {
                    displayTransaction(t);
                }
            }
                catch (Exception e) {
                System.out.println(e.getMessage());
                }
            }
    }
    public static void yearToDate(HashMap<String, Transaction> transactions) {
        // Today components for boundary check
        String[] todayParts = String.valueOf(LocalDate.now()).split("-"); // ["2025","10","15"]
        int currentYear  = Integer.parseInt(todayParts[0]);
        int currentMonth = Integer.parseInt(todayParts[1]);
        int currentDay   = Integer.parseInt(todayParts[2]);

        // Include all records from Jan 1 to today of the same year
        for (Transaction t : transactions.values()) {
            try {
                String[] parts = t.getDate().split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]);

                // Keep only: same year and not after today
                // Compare (month,day) with (currentMonth,currentDay)
                boolean sameYear = (year == currentYear);
                boolean beforeOrEqualToday = (month <currentMonth) || (month == currentMonth && day <= currentDay);
                if (sameYear && beforeOrEqualToday) {
                    displayTransaction(t);
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
    public static void previousYear(HashMap<String, Transaction> transactions) {
        // Determine the target year (previous year)
        // Target year is one less than the current year
        String[] todayParts = String.valueOf(LocalDate.now()).split("-");
        int currentYear   = Integer.parseInt(todayParts[0]);
        int targetYear    = currentYear - 1;

        // Loop over in-memory transactions, include all records whose year equals targetYear
        for (Transaction t : transactions.values()) {
            try {
                String[] parts = t.getDate().split("-");
                int year = Integer.parseInt(parts[0]);

                // Keep only records from previous year
                if (year == targetYear) {
                    displayTransaction(t);
                }
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }
    public static void searchbyVendor(HashMap<String,Transaction> transactions, Scanner scanner) {
        // Prompt for vendor name
        System.out.print("Please enter Vendor:");
        String vendor = scanner.nextLine();
        // Scan all records and print exact matches
        for (Transaction t: transactions.values()){
            if (t.getVendor().equals(vendor)){
            displayTransaction(t);}
        }

    }
    public static void displayTransaction(Transaction t) {
        // Standardized print format for one transaction
        System.out.printf("Date: %s | Time: %s | Description: %s | Vendor: %s | Amount: %.2f%n", t.getDate(), t.getTime(), t.getDescription(), t.getVendor(), t.getAmount()
        );    }
    public static void displayDeposit(HashMap<String, Transaction> transactions) {
        // Print only deposits (positive amounts)
        System.out.println();
        System.out.println("=== DEPOSITS ===");
        for (Transaction t : transactions.values()) {
            if (t.getAmount() > 0) {
                displayTransaction(t);
            }
        }
    }
    public static void displayPayments(HashMap<String, Transaction> transactions) {
        // Print only payments (negative amounts)
        System.out.println();
        System.out.println("=== Payments ===");
        for (Transaction t : transactions.values()) {
            if (t.getAmount() < 0) {
                displayTransaction(t);
            }
        }
    }
}
