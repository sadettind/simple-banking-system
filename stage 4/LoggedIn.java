package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Scanner;

public class LoggedIn {
    Scanner scanner = new Scanner(System.in);
    Account account;
    SQLiteDataSource sqLiteDataSource;

    public LoggedIn(Account account, SQLiteDataSource sqLiteDataSource) {
        this.account = account;
        this.sqLiteDataSource = sqLiteDataSource;
    }

    public int start() {
        while (true) {

            int choiceInner = PrintMenu();
            switch (choiceInner) {
                case 1:
                    System.out.println("Balance: " + account.getBalance());
                    break;
                case 2:
                    System.out.println("Enter income:\n");
                    account.setBalance(account.getBalance() + scanner.nextInt());
                    System.out.println("Income was added!\n");
                    break;
                case 3:
                    transfer();
                    break;
                case 4:
                    close();
                    System.out.println("The account has been closed!\n");
                    break;
                case 5:
                    update(this.account);
                    System.out.println("You have successfully logged out!");
                    return 0;
                case 0:
                    update(this.account);
                    System.out.println("Bye!");
                    return -1;
                default:
                    System.out.println("You have entered a wrong input. Try again!\n");
            }
        }
    }

    private void transfer() {
        System.out.println("Enter card number:");
        String cardToCompare = scanner.next();
        Account receiver = new Account();
        if (cardToCompare.equals(account.getCardNo())) {
            System.out.println("You can't transfer money to the same account!");
            return;
        } else if (!Main.isValidNumber(cardToCompare)) {     //what could have been used instead of Main.method()???
            System.out.println("Probably you made a mistake in the card number. Please try again!\n");
            return;
        }
        //check if an account exists with entered card number
        if (!findAccount(cardToCompare, receiver)) {
            System.out.println("Such a card does not exist.");
            return;
        }
        System.out.println("Enter how much money you want to transfer:");
        int amount = scanner.nextInt();
        if (account.getBalance() < amount) {
            System.out.println("Not enough money!");
            return;
        }
        receiver.setBalance(receiver.getBalance() + amount);
        account.setBalance(account.getBalance() - amount);

        update(receiver);
        update(account);
        System.out.println("Success");


        return;
    }

    private void close() {

        try (Connection connection = sqLiteDataSource.getConnection()) {
            String command = "DELETE FROM card WHERE id = " + account.getId() + ";";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(command);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void update(Account accountToUpdate) {

        try (Connection connection = sqLiteDataSource.getConnection()) {
            String command = "UPDATE card SET balance= " + accountToUpdate.getBalance() + " WHERE id = " + accountToUpdate.getId() + ";";

            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(command);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int PrintMenu() {

        System.out.println("1. Balance");
        System.out.println("2. Add income");
        System.out.println("3. Do Transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit\n");

        return scanner.nextInt();

    }

    private boolean findAccount(String cardNumber, Account accountToFind) {

        //if the cardNumber is not found, then an exception will be thrown and which will end in "false" return.
        //So the exception mechanism becomes a check system here
        //if the cardNumber is found, receiver account's parameters are set

        try (Connection connection = sqLiteDataSource.getConnection()) {

            String select = "SELECT * FROM card WHERE number = " +
                    "'" + cardNumber + "';";

            try (Statement statement = connection.createStatement()) {

                ResultSet resultSet = statement.executeQuery(select);

                accountToFind.setId(resultSet.getInt("id"));
                accountToFind.setCardNo(resultSet.getString("number"));
                accountToFind.setPin(resultSet.getString("pin"));
                accountToFind.setBalance(resultSet.getInt("balance"));
            }

        } catch (SQLException throwables) {
            System.out.println("problem with sending to a non-existing account");
            return false;

        }
        return true;
    }
}


