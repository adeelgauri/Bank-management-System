import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {
    private Connection connection;
    private Scanner scanner;

    public AccountManager(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }


    public void creditMoney(long accountNumber) throws SQLException {
        scanner.nextLine();
        System.out.print("Enter amount : ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter security PIN ");
        String securityPin = scanner.nextLine();

        try {

            connection.setAutoCommit(false);
            if (accountNumber != 0) {

                String query = "select * from accounts where account_number = ? and security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, securityPin);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    String creditQuery = "update accounts set balance = balance + ? where account_number = ? ";
                    PreparedStatement preparedStatement1 = connection.prepareStatement(creditQuery);
                    preparedStatement1.setDouble(1, amount);
                    preparedStatement1.setLong(2, accountNumber);
                    int effectedRows = preparedStatement1.executeUpdate();
                    if (effectedRows > 0) {
                        System.out.println("Rs " + amount + " Credit successfully");
                        connection.commit();
                        connection.setAutoCommit(true);
                        return;
                    } else {
                        System.out.println("Transaction failed");
                        connection.rollback();
                        connection.setAutoCommit(true);
                    }
                } else {
                    System.out.println("Invalid PIN ");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void debitMoney(long accountNumber) throws SQLException {

        scanner.nextLine();
        System.out.print("Enter amount : ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter security pin : ");
        String securityPin = scanner.nextLine();

        try {

            connection.setAutoCommit(false);
            if (accountNumber != 0) {
                String query = "select * from accounts where account_number = ? and security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, securityPin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");
                    if (amount <= currentBalance) {
                        String debitQuery = "update accounts set balance = balance - ? where account_number = ? ";

                        PreparedStatement preparedStatement1 = connection.prepareStatement(debitQuery);
                        preparedStatement1.setDouble(1, amount);
                        preparedStatement1.setLong(2, accountNumber);
                        int effectedRows = preparedStatement1.executeUpdate();
                        if (effectedRows > 0) {
                            System.out.println("Rs " + amount + " debited successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction failed ");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }

                    } else {
                        System.out.println("Insufficient balance");
                    }
                } else {
                    System.out.println("Invalid PIN");
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        connection.setAutoCommit(true);

    }


    public void transferMoney(long sanderAccountNumber) throws SQLException {

        scanner.nextLine();
        System.out.print("Enter receiver account number : ");
        long receiverAccountNumber = scanner.nextLong();
        System.out.print("Enter amount : ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter security PIN ");
        String securityPin = scanner.nextLine();

        try {
            connection.setAutoCommit(false);
            if (sanderAccountNumber != 0 && receiverAccountNumber != 0) {
                String query = "select * from accounts where account_number = ? and security_pin = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, sanderAccountNumber);
                preparedStatement.setString(2, securityPin);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    double currentBalance = resultSet.getDouble("balance");
                    if (amount <= currentBalance) {

                        String debitQuery = "update accounts set balance = balance - ? where account_number = ?";
                        String creditQuery = "update accounts set balance = balance + ? where account_number = ?";

                        PreparedStatement debitPreparedStatement = connection.prepareStatement(debitQuery);
                        PreparedStatement creditPreparedStatement = connection.prepareStatement(creditQuery);

                        debitPreparedStatement.setDouble(1, amount);
                        debitPreparedStatement.setLong(2, sanderAccountNumber);
                        creditPreparedStatement.setDouble(1, amount);
                        creditPreparedStatement.setLong(2, receiverAccountNumber);

                        int debitEffectedrows = debitPreparedStatement.executeUpdate();
                        int creditEffectedrows = creditPreparedStatement.executeUpdate();
                        if (debitEffectedrows > 0 && creditEffectedrows > 0) {
                            System.out.println("Rs " + amount + " Transferred successfully");
                            connection.commit();
                            connection.setAutoCommit(true);
                            return;
                        } else {
                            System.out.println("Transaction failed");
                            connection.rollback();
                            connection.setAutoCommit(true);
                        }
                    } else {
                        System.out.println("Insufficient balance");
                    }
                } else {
                    System.out.println("Invalid security PIN ");
                }
            } else {
                System.out.println("Invalid account number");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        connection.setAutoCommit(true);
    }

    public void checkBalance(long accountNumber) {

        scanner.nextLine();
        System.out.print("Enter security pin : ");
        String securityPin = scanner.nextLine();
        String query = "select balance from accounts where account_number = ? and security_pin = ?";
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, accountNumber);
            preparedStatement.setString(2, securityPin);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                double balance = resultSet.getDouble("balance");
                System.out.println("Balance : " + balance);
            } else {
                System.out.println("Invalid pin");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getAccountInformation (String email){
        String query = "select * from accounts where email = ? ";
        try{

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                long accountNumber = resultSet.getLong("account_number");
                String name = resultSet.getString("name");
                double balance = resultSet.getDouble("balance");
                System.out.println("+----------------+-------------+-------------------+---------+");
                System.out.println("| account_number | name        | email             | balance |");
                System.out.println("+----------------+-------------+-------------------+---------+");
                System.out.printf("| %-14s | %-11s | %-17s | %-7s |\n", accountNumber,name,email,balance);
                System.out.println("+----------------+-------------+-------------------+---------+");
            }

        }catch(SQLException e ){
            e.printStackTrace();
        }

    }

}
