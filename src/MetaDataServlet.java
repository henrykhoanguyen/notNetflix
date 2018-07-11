
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MetaDataServlet
 */
@WebServlet(name = "/MetaDataServlet", urlPatterns = "/api/metadata")
public class MetaDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    // Create a dataSource which registered in web.xml
    /*@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;*/
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json"); // Response mime type
		
		PrintWriter out = response.getWriter();
    	try {
    		JsonArray jsonArray = new JsonArray();
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
            	System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource dataSource = (DataSource) envCtx.lookup("jdbc/moviedb");

            if (dataSource == null)
                System.out.println("ds is null.");
            
    		// create database connection
    		Connection connection = dataSource.getConnection();
    		// declare statement
    		PreparedStatement statement = null;
    		
    		ResultSet rs = null;
    		
    		String query = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE " + 
    						"FROM INFORMATION_SCHEMA.COLUMNS " + 
    						"WHERE table_schema = 'moviedb'";
    		
    		statement = connection.prepareStatement(query);
    		//System.out.println("HELLO " + statement);
    		rs = statement.executeQuery();
    		String table_name = "";
    		String column_name = "";
    		String data_type = "";
    		JsonObject responseJsonObject = new JsonObject();
    		while(rs.next()) {
        		
        		
        		responseJsonObject.addProperty("status", "metadata");
        		
    			if (table_name.equalsIgnoreCase("")){
    				
    				table_name = rs.getString("table_name");
        			column_name = rs.getString("column_name");
        			data_type = rs.getString("data_type");
        			
    				
    			}else if(table_name.equalsIgnoreCase(rs.getString("table_name"))) {

        			column_name += "<br>" + rs.getString("column_name");
        			data_type += "<br>" + rs.getString("data_type");

        		}else if(!table_name.equalsIgnoreCase(rs.getString("table_name"))){
        			
        			responseJsonObject.addProperty("table_name", table_name);
    				responseJsonObject.addProperty("column_name", column_name);
    				responseJsonObject.addProperty("data_type", data_type);
    				jsonArray.add(responseJsonObject);
    				
        			responseJsonObject = new JsonObject();
        			table_name = rs.getString("table_name");
        			column_name = rs.getString("column_name");
        			data_type = rs.getString("data_type");    				
    				
        		}

    		}
    		out.write(jsonArray.toString());
    		rs.close();
    		statement.close();
    		connection.close();
    	}catch (Exception e) {
    		JsonArray jsonArray = new JsonArray();
			JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            jsonArray.add(responseJsonObject);
            out.write(jsonArray.toString());
    	}
    	
    	out.close();
	}

}
