import java.sql.*;
import java.util.Scanner;

public class Hotel_management_system {

    private static final String url = "jdbc:mysql://localhost:3306/hotel";

    private static final String username = "root";

    private static final String password = "krishpatel13579";


    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println(connection);
            while (true) {
                System.out.println();
                System.out.println("Hotel Management System");
                Scanner scanner = new Scanner(System.in);
                System.out.println("1, Reserve a room");
                System.out.println("2, view Reservation");
                System.out.println("3, Get Room number");
                System.out.println("4, Get Contact number");
                System.out.println("5, view guest details");
                System.out.println("6, Update Reservation");
                System.out.println("7, Delete Reservation");
                System.out.println("0, Exit");
                System.out.println("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getroom(connection, scanner);
                        break;
                    case 4:
                        getcontact(connection, scanner);
                        break;
                    case 5:
                        viewguestdetails(connection, scanner);
                        break;
                    case 6:
                        updateReservation(connection, scanner);
                        break;
                    case 7:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        return;
                    default:
                        System.out.println("invalid choice. Try again");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private static void reserveRoom(Connection connection, Scanner scanner) {
        System.out.println("Enter guest name");
        String guestName = scanner.next();
        scanner.nextLine();
        System.out.println("Enetr room nunmber");
        int roomNumber = scanner.nextInt();
        System.out.println("Enter contact number");
        String contactNumber = scanner.next();

        String sql = "insert into reservations(guest_name,room_number,contact_number)" +
                "values('" + guestName + "'," + roomNumber + ",'" + contactNumber + "')";

        try (Statement statement = connection.createStatement()) {
            int rowsaffected = statement.executeUpdate(sql);

            if (rowsaffected > 0) {
                System.out.println(" Congratulation Reservation succesfull!!");
            } else {
                System.out.println("Reservation is failed!!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewReservations(Connection connection) throws SQLException {
        String sql = "select reservation_id,guest_name,room_number,contact_number,reservation_date from reservations";
        try (Statement statement = connection.createStatement()) {
            ResultSet st = statement.executeQuery(sql);
            System.out.println("Current Reservations:");
            System.out.println("+----------------+--------------+---------------+----------------+----------------------------------+");
            System.out.println("| Reservation ID | Guest        | Room Number   | Contact NUmber | Reservation Date                 |");
            System.out.println("+----------------+--------------+---------------+----------------+----------------------------------+");

            while (st.next()) {
                int reservationId = st.getInt("reservation_id");
                String guest = st.getString("guest_name");
                int room = st.getInt("room_number");
                String contact = st.getString("contact_number");
                String date = st.getTimestamp("reservation_date").toString();

                //fomat for reservation date in table-like format
                System.out.printf("| %14d | %-12s | %-13d | %-14s | %-32s |\n",
                        reservationId, guest, room, contact, date);
            }
            System.out.println("+----------------+--------------+---------------+----------------+----------------------------------+");
        }

    }

    private static void getroom(Connection connection, Scanner scanner) {
        System.out.println("Enter Reseravtion id:");
        int reservationid = scanner.nextInt();
        scanner.nextLine();


        String sql = " select room_number from reservations where reservation_id=" + reservationid;


        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                int number = resultSet.getInt("room_number");
                System.out.println("Room number is:" + number);
            } else {
                System.out.println("Reservation not found for the given ID and guest name!!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }


    private static void getcontact(Connection connection, Scanner scanner) {
        System.out.println("Enter Reseravtion id:");
        int reservationid = scanner.nextInt();
        scanner.nextLine();



        String query=" select contact_number from reservations where reservation_id=" + reservationid;


        try (Statement statement = connection.createStatement()) {
            ResultSet rs=statement.executeQuery(query);


            if(rs.next())
            {
                String number = rs.getString("contact_number");
                System.out.println("Contact number is:" + number);
            }else {
                System.out.println("Contact number does not found for this reservation ID!!");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
    }

    private static void viewguestdetails(Connection connection, Scanner scanner) {
        System.out.println("Enter guest name:");
        String guestname = scanner.next();
        scanner.nextLine();

        String query = "select*from reservations where guest_name='" + guestname + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                int reservationId = rs.getInt("reservation_id");
                String guest = rs.getString("guest_name");
                int room = rs.getInt("room_number");
                String contact = rs.getString("contact_number");
                String date = rs.getTimestamp("reservation_date").toString();
                System.out.println();
                System.out.println("Guest details are as follows");
                System.out.println("+------------------------------------------------------------------------------------+");
                System.out.println("Reservation ID: " + reservationId);
                System.out.println("Guest Name: " + guest);
                System.out.println("Room Number: " + room);
                System.out.println("Contact NUmber: " + contact);
                System.out.println("Date: " + date);
                System.out.println("+------------------------------------------------------------------------------------+");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }


    private static void updateReservation(Connection connection, Scanner scanner) throws SQLException {

        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationID = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (!reservationExists(connection, reservationID)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.next();
            scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();
            scanner.nextLine();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationID;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException ae) {
            System.out.println(ae.getMessage());
        }

    }


    private static void deleteReservation(Connection connection, Scanner scanner) {


        System.out.print("Enter reservation ID to delete: ");
        int reservationId = scanner.nextInt();

        if (!reservationExists(connection, reservationId)) {
            System.out.println("Reservation not found for the given ID.");
            return;
        }

        String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

        try (Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);

            if (affectedRows > 0) {
                System.out.println("Reservation deleted successfully!");
            } else {
                System.out.println("Reservation deletion failed.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String s = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(s)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false; // Handle database errors as needed
        }
    }




    private static void exit () {
        System.out.print("Exiting System");
        int i = 6;
        while (i != 0) {
            System.out.print(".");
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");

    }
}

