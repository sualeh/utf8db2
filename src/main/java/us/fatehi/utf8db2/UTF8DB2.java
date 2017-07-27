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

    /*
     * final String url = "jdbc:db2://192.168.56.101:50000/SAMPLE";
     * final String username = "db2inst1"; final String password =
     * "sualeh-db2-password";
     */
    final String url = "jdbc:db2://sfatehi-vm1:50000/bevap:securityMechanism=4;currentSchema=SFATEHI;";
    final String username = "sfatehi";
    final String password = null;

    try (final Connection connection = getConnection(url,
                                                     username,
                                                     password,
                                                     true))
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
        preparedStatement.setBytes(1,
                                   new byte[] {
                                                (byte) 0xE2,
                                                (byte) 0x82,
                                                (byte) 0xAC
                                   });
        preparedStatement.executeUpdate();

        preparedStatement.setBytes(1,
                                   new byte[] {
                                                (byte) 0xBF,
                                                (byte) 0x20,
                                                (byte) 0x52
                                   });
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

  private static Connection getConnection(final String url,
                                          final String username,
                                          final String password,
                                          final boolean handleBrokenUF8)
    throws SQLException
  {
    final Properties connectionProps = new Properties();
    connectionProps.put("user", username);
    if (password != null)
    {
      connectionProps.put("password", password);
    }

    if (handleBrokenUF8)
    {
      System.setProperty("db2.jcc.charsetDecoderEncoder", "3");
    }

    final Connection conn = DriverManager.getConnection(url, connectionProps);

    System.out.println("Connected to database, " + conn.toString());
    return conn;
  }

}
