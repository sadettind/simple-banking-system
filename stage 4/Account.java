package banking;

public class Account {
    int id;
    String cardNo;
    String pin;
    int balance;

    public Account(int id, String cardNo, String pin, int balance) {
        this.id = id;
        this.cardNo = cardNo;
        this.pin = pin;
        this.balance = balance;
    }

    public Account() {
        id = 0;
        cardNo = null;
        pin = null;
        balance = 0;

    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }
}
