package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static SQLiteDataSource datasource = new SQLiteDataSource();
    static int id = 0;
    static Account currentAccount;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String dbLocation = args[1];
        datasource.setUrl("jdbc:sqlite:" + dbLocation);
        createDatabase();
        while (true) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    createCard();
                    break;

                case 2:
                    System.out.println("Enter your card number:");
                    String cardCheck = scanner.next();
                    System.out.println("Enter your card PIN");
                    String pinCheck = scanner.next();

                    boolean login = selectAccount(cardCheck, pinCheck);

                    if (!login) {
                        System.out.println("Wrong card number or PIN!\n");
                    } else {
                        System.out.println("You have successfully logged in!\n");
                        LoggedIn loggedIn = new LoggedIn(currentAccount, datasource);
                        int res = loggedIn.start();
                        if (res == -1) {
                            return;
                        }
                    }
                    break;

                case 0:
                    System.out.println("Bye!");
                    return;

            }
        }
    }

    private static void createCard() {

        Account account;

        //try until a unique card is found (unique id)
        while (true) {
            account = new Account(id, generateCard(), generatePin(), 0);
            boolean notExists = saveAccount(account);

            if (notExists) {
                break;
            }
        }
        System.out.println("Your card has been created");
        System.out.println("Your card number:\n" + account.getCardNo());
        System.out.println("Your card PIN:\n" + account.getPin());
    }


    static boolean selectAccount(String cardNo, String pin) {

        try (Connection connection = datasource.getConnection()) {

            String select = "SELECT * FROM card WHERE number = " +
                    "'" + cardNo + "' AND pin = " +
                    "'" + pin + "';";

            try (Statement statement = connection.createStatement()) {

                ResultSet resultSet = statement.executeQuery(select);

                currentAccount = new Account(resultSet.getInt("id"),
                        resultSet.getString("number"),
                        resultSet.getString("pin"),
                        resultSet.getInt("balance")
                );
            }

        } catch (SQLException throwables) {
            return false;
        }
        return true;
    }

    private static boolean saveAccount(Account account) {

       /* String addNewAccount = "INSERT INTO card(id,number,pin,balance) VALUES(" +
                account.getId() + "," +
                "'" + account.getCardNo() + "'," +
                "'" + account.getPin() + "'," +
                account.getBalance() + ")";

        try (Connection connection = datasource.getConnection()) {

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(addNewAccount);
            }
        } catch (SQLException throwables) {
            System.out.println("Exception while saving card to db.");
            return false;
        }
        id++;
        return true;*/
        String command = "INSERT INTO card(id,number,pin,balance) VALUES(?,?,?,?);";

        try (Connection connection = datasource.getConnection();
             PreparedStatement statement = connection.prepareStatement(command)) {


            statement.setInt(1, account.getId());
            statement.setString(2, account.getCardNo());
            statement.setString(3, account.getPin());
            statement.setInt(4, account.getBalance());
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Exception while saving card to db.");
            return false;
        }
        id++;
        return true;
    }

    public static String generateCard() {
        Random random = new Random();
        String bin = "400000";
        String checksum = "9";
        String custNo = null;
        String cardNo;

        while (true) {
            custNo = String.format("%09d", random.nextInt(1000000000));
            cardNo = bin + custNo + checksum;
            if (isValidNumber(cardNo)) {
                break;
            }
        }
        return cardNo;
    }

    public static String generatePin() {
        Random random = new Random();
        String pin = String.format("%04d", random.nextInt(10000));
        return pin;
    }

    public static boolean isValidNumber(String cardNo) {
        int[] cardArray = new int[cardNo.length()];
        int sum = 0;
        for (int i = 0; i < cardNo.length(); i++) {
            cardArray[i] = Integer.parseInt(String.valueOf(cardNo.charAt(i)));
        }
        for (int i = 0; i < cardNo.length(); i = i + 2) {
            cardArray[i] = cardArray[i] * 2;
            if (cardArray[i] > 9) {
                cardArray[i] = cardArray[i] - 9;
            }
        }
        for (int i = 0; i < cardNo.length(); i++) {
            sum = sum + cardArray[i];
        }
        if (sum % 10 == 0) {
            return true;
        } else
            return false;


    }

    private static void createDatabase() {

        try (Connection connection = datasource.getConnection()) {

            String createTable = "CREATE TABLE IF NOT EXISTS card (" +
                    "id INTEGER PRIMARY KEY," +
                    "number TEXT NOT NULL," +
                    "pin TEXT NOT NULL," +
                    "balance INTEGER DEFAULT 0" +
                    ");";

            String checkIfTableIsEmpty = "SELECT Count(*) FROM card;";
            String getHighestID = "SELECT Max(id) FROM card;";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTable);

                ResultSet resultSet = statement.executeQuery(checkIfTableIsEmpty);

                //Check if the table is empty, if so start with id 1, if not increment id
                if (resultSet.getInt(1) == 0) {
                    id = 1;
                } else {
                    resultSet = statement.executeQuery(getHighestID);
                    id = resultSet.getInt(1) + 1;
                }
            }

        } catch (SQLException throwables) {
            System.out.println("Problem in creating table.");
            throwables.printStackTrace();
        }
    }

}