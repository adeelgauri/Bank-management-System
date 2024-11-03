import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private Connection connection;
    private Scanner scanner;
    public User(Connection connection , Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }



    public void register(){

        scanner.nextLine();
        System.out.print("Enter full name : ");
        String name = scanner.nextLine();
        System.out.print("Enter email : ");
        String email = scanner.nextLine();
        System.out.print("Enter password : ");
        String password = scanner.nextLine();

        if(userExist(email)){
            System.out.println("User already exist for this email address");
            return;
        }

        String query = "insert into user (name , email , password) values (?,?,?) ";

        try{


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);

            int effectedRows = preparedStatement.executeUpdate();

            if(effectedRows > 0){
                System.out.println("Registration successfully");
            }else{
                System.out.println("Registration Failed");
            }

        }catch (SQLException e ){
            e.printStackTrace();
        }
    }

    public String login(){

        scanner.nextLine();
        System.out.print("Enter email : ");
        String email = scanner.nextLine();
        System.out.print("Enter password : ");
        String password = scanner.nextLine();

        String query = "select * from user where email = ? and password = ?";

        try{


            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            preparedStatement.setString(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return email;
            }else{
                return null;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean userExist(String email){

        String query = "select * from user where email = ?";

        try{
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }

        }catch(SQLException e ){
            e.printStackTrace();
        }

        return false;
    }
}
