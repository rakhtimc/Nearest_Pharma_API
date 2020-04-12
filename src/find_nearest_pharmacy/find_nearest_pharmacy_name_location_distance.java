package find_nearest_pharmacy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@Path("/find_nearest_pharmacy_name_location_distance")
public class find_nearest_pharmacy_name_location_distance {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String say_hello() {
		double argv[]= new double[2];
		argv[0]=-95.68695;
		argv[1]=39.001423;
		
		List<String> return_string = new ArrayList<>();
	    try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch (ClassNotFoundException e) {
            String unsucessful_msg = "Please include Classpath  Where your SQL Server Driver is located";
            e.printStackTrace();
            return unsucessful_msg;
        }
        System.out.println("SQL Server driver is loaded successfully");
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        ResultSet rset1=null;
        boolean found=false;
        try {
        	String connection_string = "jdbc:sqlserver://practicedb2020.database.windows.net:1433;database=pharma_db;"
        			+ "user=practicedb2020admin@practicedb2020;password=PracticeDB2020;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            conn = DriverManager.getConnection(connection_string);
            if (conn != null)
            {
                System.out.println("SQL Server Database Connected");
            }
            else
            {
                System.out.println("SQL Server connection Failed ");
            }
            String query1 = "SELECT TOP(1) pharma_db.dbo.pharmacies.[\"name\"], pharma_db.dbo.pharmacies.[\"address\"] FROM pharma_db.dbo.pharmacies " +  
            		"ORDER BY [\"SpatialLocation\"].STDistance('POINT("+argv[0]+" "+argv[1]+")');";
            
//            System.out.println(query1);
            pstmt1=conn.prepareStatement(query1);
            rset1=pstmt1.executeQuery();
            
            if(rset1!=null) {
            	 while(rset1.next())
                 {
            		 return_string.add(rset1.getString("\"name\""));
                 }                
            }
        } catch (SQLException e) {
            String query_exception = "Incorrect parameteres passed to the API";
            e.printStackTrace();
            return query_exception;
        }
        
        String final_string="";
        for(String each_string:return_string)
        	final_string += each_string;
        
        return final_string;
	}
}
