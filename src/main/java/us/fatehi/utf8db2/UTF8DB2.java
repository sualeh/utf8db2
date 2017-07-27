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

    try (final Connection connection = getConnection(true))
    {
      // 1. Clear table for this test
      execute(connection, "DROP TABLE TAB1");
      execute(connection, "CREATE TABLE TAB1 (COL1 VARCHAR(3))");
      System.out.println("Test table dropped and re-created");

      // 2. Insert bad UTF-8 bytes
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
        .executeQuery("SELECT * FROM TAB1");)
      {
        while (results.next())
        {
          System.out.println(results.getString(1));
        }
      }
    }
  }

}
