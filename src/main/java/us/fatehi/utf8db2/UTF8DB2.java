package us.fatehi.utf8db2;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class UTF8DB2
{
  public static void main(final String[] args)
    throws Exception
  {
    try (final Connection connection = getConnection())
    {
      // 1. Clear table for this test
      try (final Statement statement = connection.createStatement();)
      {
        statement.execute("DROP TABLE TAB1");
        statement.execute("CREATE TABLE TAB1 (COL1 VARCHAR(3))");
      }
      System.out.println("Test table dropped and re-created");

      // 2. Insert bad UTF-8 bytes
      final String insertTableSQL = "INSERT INTO TAB1 (COL1) VALUES (?)";
      try (final PreparedStatement preparedStatement = connection
        .prepareStatement(insertTableSQL);)
      {
        preparedStatement
          .setBytes(1, new byte[] { (byte) 0xE2, (byte) 0x82, (byte) 0xAC });
        preparedStatement.executeUpdate();

        preparedStatement
          .setBytes(1, new byte[] { (byte) 0xBF, (byte) 0x20, (byte) 0x52 });
        preparedStatement.executeUpdate();
      }
      System.out.println("Data inserted into table");

      // 3. Read them back out
      // http://www-01.ibm.com/support/docview.wss?uid=swg21684365
      try (final ResultSet results = connection.createStatement()
        .executeQuery("SELECT * FROM TAB1");)
      {
        while (results.next())
        {
          System.out.println(results.getString(1));
        }
      }
    }
  }

  private static Connection getConnection()
    throws SQLException
  {
    final Properties connectionProps = new Properties();
    connectionProps.put("user", "db2inst1");
    connectionProps.put("password", "sualeh-db2-password");

    System.setProperty("db2.jcc.charsetDecoderEncoder", "3");

    final Connection conn = DriverManager
      .getConnection("jdbc:db2://192.168.56.101:50000/SAMPLE", connectionProps);

    System.out.println("Connected to database, " + conn.toString());
    return conn;
  }

}
