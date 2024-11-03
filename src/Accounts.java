import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Accounts {
    private Connection connection;
    private Scanner scanner;

    public Accounts(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }


    public long openAccount(String email) {
        if (!accountExist(email)) {
            scanner.nextLine();
            System.out.print("Enter full name : ");
            String name = scanner.nextLine();
            System.out.print("Enter initial amount : ");
            double amount = scanner.nextDouble();
            scanner.nextLine();
            System.out.print("Enter security pin : ");
            String securityPin = scanner.nextLine();

            String query = "insert into accounts (account_number,name,email,balance,security_pin) values (?,?,?,?,?)";
            try {

                long accountNumber = generateAccountNumber();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setLong(1, accountNumber);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, email);
                preparedStatement.setDouble(4, amount);
                preparedStatement.setString(5, securityPin);

                int effectedRows = preparedStatement.executeUpdate();
                if (effectedRows > 0) {
                    return accountNumber;
                } else {
                    throw new RuntimeException("Account creation failed");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        throw new RuntimeException("Account already exist");
    }


    public long getAccountNumber(String email) {

        String query = "select account_number from accounts where email = ?";
        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("account_number");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Account number doesn't exist");

    }

    public long generateAccountNumber() {

        String query = "select account_number from accounts order by account_number DESC LIMIT 1";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                long lastAccountNumber = resultSet.getLong("account_number");
                return lastAccountNumber + 1;
            } else {
                return 10000100;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 10000100;
    }

    public boolean accountExist(String email) {

        String query = "select account_number from accounts where email = ?";

        try {

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
