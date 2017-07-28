package us.fatehi.utf8db2;


import static us.fatehi.utf8db2.ConnectionUtility.execute;
import static us.fatehi.utf8db2.ConnectionUtility.getConnection;
import static us.fatehi.utf8db2.ConnectionUtility.insert;

import java.sql.Connection;
import java.sql.ResultSet;

public class UTF8DB2
{
  public static void main(final String[] args)
    throws Exception
  {

    // Request DB2 JDBC driver to gracefully handle encoding issues, by
    // using the Unicode "replacement" character
    // http://www-01.ibm.com/support/docview.wss?uid=swg1IC74895
    System.setProperty("db2.jcc.charsetDecoderEncoder", "3");

    try (final Connection connection = getConnection())
    {
      // 1. Clear table for this test
      execute(connection, "DROP TABLE TAB1");
      execute(connection, "CREATE TABLE TAB1 (COL1 VARCHAR(3))");
      System.out.println("Test table dropped and re-created");

      // 2. Insert good and bad UTF-8 bytes
      execute(connection, "INSERT INTO TAB1 VALUES ('Ã')");
      insert(connection,
             "INSERT INTO TAB1 (COL1) VALUES (?)",
             new byte[] {
                          (byte) 0xE2,
                          (byte) 0x82,
                          (byte) 0xAC
             });
      insert(connection,
             "INSERT INTO TAB1 (COL1) VALUES (?)",
             new byte[] {
                          (byte) 0xBF,
                          (byte) 0x20,
                          (byte) 0x52
             });
      System.out.println("Data inserted into table");

      // 3. Read them back out
      // http://www-01.ibm.com/support/docview.wss?uid=swg21684365
      try (final ResultSet results = connection.createStatement()
        .executeQuery("SELECT HEX(COL1), COL1 FROM TAB1");)
      {
        while (results.next())
        {
          System.out.format("row #%d: hex %s chars <%s>%n",
                            results.getRow(),
                            results.getString(1),
                            results.getString(2));
        }
      }
    }
  }

}
