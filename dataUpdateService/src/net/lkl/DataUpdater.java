package net.lkl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONArray;

@Path("/lkl")
public class DataUpdater {
	
	//@SuppressWarnings("finally")
	@PUT
	@Consumes(MediaType.TEXT_PLAIN)
	public Response UpdateStudentActivity(String studentDetails)
	{
		String username="";
		String machine="";
		int active=0;
		String[] details=studentDetails.split("@");
		username=details[0];
		machine=details[1];

		if(details[2].equals("active"))
		{
			active=1;
		}
			
		InitialContext ctx=null;
		DataSource ds=null;
		Connection connection=null;
		PreparedStatement statement=null;
		String message="status for student "+username+" updated ";
		
		try
		{
			ctx=new InitialContext();		
			ds=(DataSource)ctx.lookup("lkl-resource");

			connection=ds.getConnection();		
			String query="insert into useractivity values (?,?,systimestamp,?)";
			statement=connection.prepareStatement(query);
			statement.setString(1,username);
			statement.setString(2,machine);
			statement.setInt(3,active);
			
			int rows=statement.executeUpdate();
			
			if(rows==1)
			{
				message+="successfully";
			}
		}
		catch(NamingException e)
		{
			message+="unsuccessfully (NamingException)";			
		}
		catch(SQLException e)
		{
			message+="unsuccessfully (SQLException)";						
		}
		catch(Exception e)
		{
			message+="unsuccessfully (Exception)";			
		}

		System.out.println("** updating: "+username);
		Response response=Response.ok(message,MediaType.TEXT_PLAIN).build();
		return response;
	}
	
	@GET
	@Produces("application/json")
	public String inactiveUsers()
	{
		String output="";
		InitialContext ctx=null;
		DataSource ds=null;
		Connection connection=null;
		PreparedStatement statement=null;
		String message="get operation - successful";
		
		try
		{
			ctx=new InitialContext();		
			ds=(DataSource)ctx.lookup("lkl-resource");

			connection=ds.getConnection();		
			String query="select * from inactiveusers";
			statement=connection.prepareStatement(query);
			
			ResultSet rs=statement.executeQuery();
			JSONArray json = ResultSetConverter.convert(rs);
			output = json.toString();
		}
		catch(NamingException e)
		{
			message="unsuccessfully (NamingException)";			
		}
		catch(SQLException e)
		{
			message="unsuccessfully (SQLException)";						
		}
		catch(Exception e)
		{
			message="unsuccessfully (Exception)";			
		}

		System.out.println("** "+message);
		return output;
	}
}
