

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
import java.text.SimpleDateFormat;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Servlet implementation class CheckOutServlet
 */
@WebServlet(name = "/CheckOutServlet", urlPatterns = "/api/checkout")
public class CheckOutServlet extends HttpServlet {
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
		
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String expiration_date = request.getParameter("expiration_date");
        
        
		// Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
        JsonArray jsonArray = new JsonArray();

        if(firstName.isEmpty() || lastName.isEmpty() || expiration_date.isEmpty()) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			
			jsonObject.addProperty("message", "Please Enter All Information!");
			
			jsonArray.add(jsonObject);
			out.write(jsonArray.toString());
			
        }else {
        	try {
        		
                if(!movies.isEmpty()) {
                	Context initCtx = new InitialContext();

                    Context envCtx = (Context) initCtx.lookup("java:comp/env");
                    if (envCtx == null)
                    	System.out.println("envCtx is NULL");

                    // Look up our data source
                    DataSource dataSource = (DataSource) envCtx.lookup("jdbc/moviedb2");

                    if (dataSource == null)
                        System.out.println("ds is null.");
                    
                	 // Get a connection from dataSource
                    Connection dbcon = dataSource.getConnection();
                	
    	            // Get a set of the entries
    	            Set set = movies.entrySet();
    	            
    	            // Get an iterator
    	            Iterator i = set.iterator();
					   String customer_id = "";
					   
					   // To get today date
					   Date todayDate = new Date( );
					   SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
					   
					   String saleDate = ft.format(todayDate);
					    	 	               
	 	               String customerInfoSQL = "SELECT c.id "
	 	               		+ "FROM creditcards cc, customers c "
	 	               		+ "WHERE cc.id = c.ccId AND cc.firstName = ? AND cc.lastName = ? "
	 	               		+ "AND cc.expiration = ?";
	 	               
	 	               PreparedStatement custInfoStatement = dbcon.prepareStatement(customerInfoSQL);
	 	               
	 	               custInfoStatement.setString(1, firstName);
	 	               custInfoStatement.setString(2, lastName);
	 	               custInfoStatement.setString(3, expiration_date);
	 	               	 	               
	 	               ResultSet custResult = custInfoStatement.executeQuery();
	 	           		
	 	               while(custResult.next()) {
	 	            	   customer_id = custResult.getString("id");
	 	               }
	 	               
	    	            // Display elements
	 	               while(i.hasNext()) {
	    	            	Map.Entry movie = (Map.Entry)i.next();
						   String movie_id = movie.getKey().toString();
						   String quantity = movie.getValue().toString();
						   String movie_title = "";
						   String sale_id = "";
	    	               
	    	               String updateSQL = "INSERT INTO sales (customerId, movieId, saleDate) "
	       	               		+ "VALUES (?, ?, ?)";
	    	               
		   		           // Declare our statement
		   		           PreparedStatement updateStatement = dbcon.prepareStatement(updateSQL);
		   		           
		   		           updateStatement.setString(1, customer_id);
		   		           updateStatement.setString(2, movie_id);
		   		           updateStatement.setString(3, saleDate);
		   		           

		   		           // Perform the query
		   		           updateStatement.executeUpdate();
		   		           
		   		           String query = "SELECT title FROM movies WHERE id = ?";
		   		           
		   		           // Declare our statement
		   		           PreparedStatement statement = dbcon.prepareStatement(query);
		   		           statement.setString(1, movie_id);
		   		        
		   		           ResultSet movieResult = statement.executeQuery();
		   		           
		   		           while(movieResult.next()) {
		   		        	   movie_title = movieResult.getString("title");
		   		           }
		   		           

		   		           String saleSQL = "SELECT id FROM sales WHERE customerId = ? AND movieId = ? AND saleDate = ?";

		   		           // Declare our statement
		   		           PreparedStatement saleStatement = dbcon.prepareStatement(saleSQL);

		   		           saleStatement.setString(1, customer_id);
		   		           saleStatement.setString(2, movie_id);
		   		           saleStatement.setString(3, saleDate);
		   		        		   		        
		   		           ResultSet saleResult = saleStatement.executeQuery();
		   		           while(saleResult.next()) {
		   		        	   sale_id = saleResult.getString("id");
		   		        	   
				   		        //System.out.println("sale_id: "+ sale_id);
				   		           
								JsonObject jsonObject = new JsonObject();
								jsonObject.addProperty("status", "success");
								jsonObject.addProperty("message", "success");
								jsonObject.addProperty("sale_id", sale_id);
								jsonObject.addProperty("movie_title", movie_title);
								jsonObject.addProperty("quantity", quantity);
								
								jsonArray.add(jsonObject);
		   		           }
		   		           
		   		           // Remove movie from cart
		   		           movies.remove(movie_id);
		   		           
		   		           saleResult.close();
		   		           saleStatement.close();
		   		           statement.close();
		   		           movieResult.close();
		   		           updateStatement.close();
						}
    	            
	 	               out.write(jsonArray.toString());
	 	               custResult.close();
	 	               custInfoStatement.close();
	 	               dbcon.close();
                }else {
        			JsonObject jsonObject = new JsonObject();
        			jsonObject.addProperty("status", "fail");
        			
        			jsonObject.addProperty("message", "Where Are You Going, Bro?");
        			
        			jsonArray.add(jsonObject);
        			out.write(jsonArray.toString());
                }
        	}catch (Exception e) {
    			JsonObject jsonObject = new JsonObject();
    			jsonObject.addProperty("status", "fail");
    		    			
    			jsonObject.addProperty("message", "Please Enter Correct Information!");
    			
    			jsonArray.add(jsonObject);
    			out.write(jsonArray.toString());
        	}
        }
        
        out.close();
	}

}
