import com.mysql.cj.protocol.a.LocalDateValueEncoder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        /*UserDAO.create(new User("toto.cutugno@hotmail.com", PasswordHasher.hashPassword("toto123"), "Toto", "Cutugno", LocalDate.of(1943, 7,7)));
        UserDAO.create(new User("nicokib@hotmail.com", PasswordHasher.hashPassword("nico123"), "Nicolas", "Kibonge", LocalDate.of(1980, 1,1)));*/


        UserDAO.findAll().forEach(u -> System.out.println(u));


    }
}