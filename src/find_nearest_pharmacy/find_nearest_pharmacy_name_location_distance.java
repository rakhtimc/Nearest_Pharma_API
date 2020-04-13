package find_nearest_pharmacy;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//import com.sun.research.ws.wadl.Response;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Application;

@Path("/find_nearest_pharmacy_name_location_distance")
public class find_nearest_pharmacy_name_location_distance {

	@POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createMessage(@FormParam("latitude") String latitude,@FormParam("longitude") String longitude) {
		List<String> return_string = new ArrayList<>();
	    try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
        catch (ClassNotFoundException e) {
            String unsucessful_msg = "Please include Classpath  Where your SQL Server Driver is located";
            e.printStackTrace();
            return Response.created(URI.create("/messages/" + String.valueOf(UUID.randomUUID()))).entity(unsucessful_msg).build();
        }
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
            	String query1 = "SELECT TOP(1) pharma_db.dbo.pharmacies.[\"name\"] as name, pharma_db.dbo.pharmacies.[\"address\"] as address, [\"SpatialLocation\"].STDistance('POINT("+longitude+" "+latitude+")') / 1609.344 as distance_in_miles FROM pharma_db.dbo.pharmacies " +  
            		"ORDER BY [\"SpatialLocation\"].STDistance('POINT("+longitude+" "+latitude+")');";
            
            	pstmt1=conn.prepareStatement(query1);
            	rset1=pstmt1.executeQuery();
            
            	if(rset1!=null) {
            		while(rset1.next())
            		{
            			return_string.add(rset1.getString("name"));
            			return_string.add(rset1.getString("address"));
            			return_string.add(rset1.getString("distance_in_miles"));
            		}                
            	}
            	
            	 return Response.created(URI.create("/messages/" + String.valueOf(UUID.randomUUID()))).entity("Name of the closest pharmacy "
            		        +return_string.get(0)+" address "+
            		        return_string.get(1)+" distance in miles "+return_string.get(2)).build();
          }else {
        	    String conn_error = "Connection to the database unsuccesful";
        	    return Response.created(URI.create("/messages/" + String.valueOf(UUID.randomUUID()))).entity(conn_error).build();
          }
        } catch (SQLException e) {
            String query_exception = "Incorrect parameteres passed to the API";
            e.printStackTrace();
            return Response.created(URI.create("/messages/" + String.valueOf(UUID.randomUUID()))).entity(query_exception).build();
        }
	}
}
