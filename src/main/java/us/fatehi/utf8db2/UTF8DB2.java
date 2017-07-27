package us.fatehi.utf8db2;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class UTF8DB2
{
  public static void main(final String[] args)
    throws Exception
  {
    try (final Connection connection = getConnection())
    {

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
