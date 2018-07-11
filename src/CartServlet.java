
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet(name = "/CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // Create a dataSource which registered in web.xml
    /*@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;*/
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(); // Get a instance of current session on the request
		HashMap<String, Integer> movies = (HashMap<String, Integer>) session.getAttribute("movies");
        String movie_id = request.getParameter("movie_id");
        String quantity = request.getParameter("quantity");

        JsonArray jsonArray = new JsonArray();

		// Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
		if (movies == null) {
			movies = new HashMap<>();
			session.setAttribute("movies", movies);
		}
        

        try {
            
            synchronized(movies) {
            	Context initCtx = new InitialContext();

                Context envCtx = (Context) initCtx.lookup("java:comp/env");
                if (envCtx == null)
                	System.out.println("envCtx is NULL");

                // Look up our data source
                DataSource dataSource = (DataSource) envCtx.lookup("jdbc/moviedb");

                if (dataSource == null)
                    System.out.println("ds is null.");
                
                // Get a connection from dataSource
                Connection dbcon = dataSource.getConnection();
                
        		// Add, Remove, and Update Action
                if(movie_id != null && Integer.parseInt(quantity) == 1 && !movies.containsKey(movie_id)) {
    	            // Insert new movies
    	            movies.put(movie_id, Integer.parseInt(quantity));
    	            
                }else if(movie_id != null && Integer.parseInt(quantity) == 0 && movies.containsKey(movie_id)){
    				// remove movies
    	            movies.remove(movie_id);
                }else if(movie_id != null && Integer.parseInt(quantity) == 1 && movies.containsKey(movie_id)){
    				// remove movies
    	            movies.put(movie_id, Integer.parseInt(quantity) + movies.get(movie_id).intValue());
                }
                
                if(!movies.isEmpty()) {
    	            // Get a set of the entries
    	            Set set = movies.entrySet();
    	            
    	            // Get an iterator
    	            Iterator i = set.iterator();
    	            
    	            // Display elements
    	            while(i.hasNext()) {
    	               Map.Entry movie = (Map.Entry)i.next();
    	               movie_id = movie.getKey().toString();
    	               quantity = movie.getValue().toString();
    	               
    		            String query = "SELECT title FROM movies WHERE id = ?";
    	
    		            // Declare our statement
    		            PreparedStatement statement = dbcon.prepareStatement(query);
    		            
    		            statement.setString(1, movie_id);
    		            
    		            
    		            // Perform the query
    		            ResultSet rs = statement.executeQuery();
    		            
    		            // Iterate through each row of rs
    		            while (rs.next()) {
    		                String movie_title = rs.getString("title");
    	
    		                // Create a JsonObject based on the data we retrieve from rs
    		                JsonObject jsonObject = new JsonObject();
    		                jsonObject.addProperty("status", "success");
    		                jsonObject.addProperty("message", "success");
    		                jsonObject.addProperty("movie_id", movie_id);
    		                jsonObject.addProperty("movie_title", movie_title);
    		                jsonObject.addProperty("movie_quantity", quantity);
    	
    		                jsonArray.add(jsonObject);
    		            }
    		            
    		            rs.close();
    		            statement.close();
    	            }
    	          	            
    	            // write JSON string to output
    	            out.write(jsonArray.toString());
    	            // set response status to 200 (OK)
    	            response.setStatus(200);
    	            
    	            dbcon.close();
                }else {
                	
        			// write error message JSON object to output
        			JsonObject jsonObject = new JsonObject();
        			jsonObject.addProperty("status", "fail");
        			jsonObject.addProperty("errorMessage", "Your Cart Is Empty!!!");
        			
        			jsonArray.add(jsonObject);
        			out.write(jsonArray.toString());
                }

            }
            	            
        } catch (Exception e) {
        	
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("errorMessage", e.getMessage());
			
			jsonArray.add(jsonObject);
			out.write(jsonArray.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);

        }	       
		
		 out.close();
		
	}

}
