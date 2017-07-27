package us.fatehi.utf8db2;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class UTF8DB2
{
  public static void main(final String[] args)
    throws Exception
  {
    try (final Connection connection = getConnection())
    {
      final String insertTableSQL = "INSERT INTO TAB1 (COL1, COL2) VALUES (?, ?)";
      try (PreparedStatement preparedStatement = connection
        .prepareStatement(insertTableSQL);)
      {

        preparedStatement.setString(1, "tst");
        preparedStatement.setString(2, "sys");

        preparedStatement.executeUpdate();
      }
    }
  }

  private static Connection getConnection()
    throws SQLException
  {
    final Properties connectionProps = new Properties();
    connectionProps.put("user", "db2inst1");
    connectionProps.put("password", "sualeh-db2-password");

    final Connection conn = DriverManager
      .getConnection("jdbc:db2://192.168.56.101:50000/SAMPLE", connectionProps);

    System.out.println("Connected to database, " + conn.toString());
    return conn;
  }

}
