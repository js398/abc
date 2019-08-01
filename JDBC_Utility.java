import java.sql.*;

public class JDBC_Utility{
  public Connection conn;
 
  public JDBC_Utility(){
    try
    {
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      String url = "jdbc:mysql://localhost/js398";
      conn = DriverManager.getConnection(url, "root", "root");
    }
    catch (ClassNotFoundException ex) {System.err.println(ex.getMessage());}
    catch (IllegalAccessException ex) {System.err.println(ex.getMessage());}
    catch (InstantiationException ex) {System.err.println(ex.getMessage());}
    catch (SQLException ex)           {System.err.println(ex.getMessage());}
  }

  public ResultSet doSelect(String query)
  {
    try{
      Statement st = conn.createStatement();
      ResultSet rs = st.executeQuery(query);
      /*while (rs.next()){
        String s = rs.getString("Username");
        int n = rs.getInt("ID");
        System.out.println(s + "   " + n);
      }*/
	
	return rs;
    }catch (SQLException ex){
      System.err.println(ex.getMessage());
    }
	return null;
  }

  public void doUpdate(String query){
    try{
      Statement st = conn.createStatement();
      st.executeUpdate(query);
    }catch (SQLException ex){
      System.err.println(ex.getMessage());
    }
  }
}
