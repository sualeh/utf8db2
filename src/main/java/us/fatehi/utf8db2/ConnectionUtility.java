package us.fatehi.utf8db2;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class ConnectionUtility
{

  public static void execute(final Connection connection, final String sql)
  {
    try (final Statement statement = connection.createStatement();)
    {
      statement.execute(sql);
    }
    catch (final Exception e)
    {
      System.err.println(e.getMessage());
    }
  }

  public static Connection getConnection(final boolean handleBrokenUF8)
    throws SQLException
  {
    /*
     * final String url = "jdbc:db2://192.168.56.101:50000/SAMPLE";
     * final String username = "db2inst1"; final String password =
     * "sualeh-db2-password";
     */
    final String url = "jdbc:db2://sfatehi-vm1:50000/bevap:securityMechanism=4;currentSchema=SFATEHI;";
    final String username = "sfatehi";
    final String password = null;

    final Properties connectionProps = new Properties();
    connectionProps.put("user", username);
    if (password != null)
    {
      connectionProps.put("password", password);
    }
    connectionProps.put("retrieveMessagesFromServerOnGetMessage", "true");

    if (handleBrokenUF8)
    {
      System.setProperty("db2.jcc.charsetDecoderEncoder", "3");
    }

    final Connection conn = DriverManager.getConnection(url, connectionProps);

    final DatabaseMetaData metaData = conn.getMetaData();
    System.out.format("Connected to database, %s %s%n",
                      metaData.getDatabaseProductName(),
                      metaData.getDatabaseProductVersion());
    return conn;
  }

  public static void insertBytes(final Connection connection,
                                 final String insertTableSQL,
                                 final byte[] bytes)
    throws SQLException
  {
    try (final PreparedStatement preparedStatement = connection
      .prepareStatement(insertTableSQL);)
    {
      preparedStatement.setBytes(1, bytes);
      preparedStatement.executeUpdate();
    }
  }

  public static void insertString(final Connection connection,
                                  final String insertTableSQL,
                                  final String string)
    throws SQLException
  {
    try (final PreparedStatement preparedStatement = connection
      .prepareStatement(insertTableSQL);)
    {
      preparedStatement.setString(1, string);
      preparedStatement.executeUpdate();
    }
  }

  private ConnectionUtility()
  {
    // Prevent instantiation
  }

}
