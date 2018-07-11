
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Servlet implementation class BrowseServlet
 */
@WebServlet(name ="BrowseServlet", urlPatterns = "/api/browse")
public class BrowseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // Create a dataSource which registered in web.xml
    /*@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;*/

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json"); // Response mime type

        // get the printwriter for writing response
        PrintWriter out = response.getWriter();
        
        try {
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
    		Statement statement = connection.createStatement();
    		// create string sql
    		String query = "select * from genres;";
    		// execute query
    		ResultSet resultSet = statement.executeQuery(query);
    		
    		// Declare JsonArray
    		JsonArray jsonArr = new JsonArray();
    		
    		while(resultSet.next()) {
    			String gId = resultSet.getString("id");
    			String gName = resultSet.getString("name");
    			
    			// Create a JsonObject based on the data we retrieve from resultSet
                JsonObject jsonObj = new JsonObject();
                jsonObj.addProperty("genre_id", gId);
                jsonObj.addProperty("genre_name", gName);
                
                jsonArr.add(jsonObj);
    		}
    		// write JSON string to output
            out.write(jsonArr.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
    		
    		resultSet.close();
    		statement.close();
    		connection.close();
        }catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
        }
        out.close();
	}

}
