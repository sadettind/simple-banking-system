package banking;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Main {

    static HashMap<Long, Integer> account = new HashMap<>();

    public static void main(String[] args) {
        Random random = new Random();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    Long cardNo = generateCard();
                    int pin = generatePin();
                    System.out.println("Your card has been created");
                    System.out.println("Your card number:\n" + cardNo);
                    System.out.println("Your card PIN:\n" + pin);
                    account.put(cardNo, pin);
                    break;

                case 2:
                    System.out.println("Enter your card number:");
                    Long cardCheck = scanner.nextLong();
                    System.out.println("Enter your card PIN");
                    int pinCheck = scanner.nextInt();
                    if (account.containsKey(cardCheck) && pinCheck == account.get(cardCheck)) {
                        System.out.println("You have successfully logged in!\n");

                        System.out.println("1. Balance");
                        System.out.println("2. Log out");
                        System.out.println("0. Exit");
                        int choiceInner= scanner.nextInt();
                        switch (choiceInner) {
                            case 1:
                                System.out.printf("Balance: %d",0);
                                break;
                            case 2:
                                break;
                        }


                    }
                    break;

                case 3:

            }
        }

    }

    public static Long generateCard() {
        Random random = new Random();
        String bin = "400000";
        String checksum = "9";
        String custNo =  String.format("%09d",random.nextInt(1000000000));
        return Long.parseLong(bin + custNo + checksum);
    }

    public static int generatePin() {
        Random random = new Random();
        String pin =  String.format("%04d",random.nextInt(10000));
        return Integer.parseInt(pin);
    }


}