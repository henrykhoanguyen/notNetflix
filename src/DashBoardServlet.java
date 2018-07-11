
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
 * Servlet implementation class DashBoardServlet
 */
@WebServlet(name="/DashBoardServlet", urlPatterns = "/api/_dashboard")
public class DashBoardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // Create a dataSource which registered in web.xml
    /*@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;*/
    
    private void addStar(PrintWriter out, String star_name, String star_birth_year) {
    	try {
    		JsonArray jsonArray = new JsonArray();

    		String newId = "";
    		String updateSQL = "";
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
            	System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource dataSource = (DataSource) envCtx.lookup("jdbc/moviedb2");

            if (dataSource == null)
                System.out.println("ds is null.");
    		// create database connection
    		Connection connection = dataSource.getConnection();
    		// declare statement
    		PreparedStatement statement = null;
    		PreparedStatement statement2 = null;
    		
    		String starIdSQL = "SELECT max(id) FROM stars";
    		
    		statement = connection.prepareStatement(starIdSQL);
    		
    		ResultSet rs = statement.executeQuery();
    		
    		while(rs.next()) {
    			newId = rs.getString("max(id)");
    		}
    		
    		Integer id = Integer.parseInt(newId.substring(2)) + 1;
    		
    		newId = "nm" + id.toString();
    		System.out.print("HELLO " + newId);
    		if(star_birth_year.isEmpty()) {
        		updateSQL = "INSERT INTO stars (id, name, birthYear) VALUES (?, ? , NULL)";
        		
        		statement2 = connection.prepareStatement(updateSQL);
        		
        		statement2.setString(1, newId);
        		statement2.setString(2, star_name);
        		
    		}else {
        		updateSQL = "INSERT INTO stars (id, name, birthYear) VALUES (?, ? , ?)";
        		
        		statement2 = connection.prepareStatement(updateSQL);
        		
        		statement2.setString(1, newId);
        		statement2.setString(2, star_name);
        		statement2.setInt(3, Integer.parseInt(star_birth_year));
    		}
    		
    		// Update database with update query
    		statement2.executeUpdate();
    		
			JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "Added New Star Successfully!");
            jsonArray.add(responseJsonObject);
            out.write(jsonArray.toString());
    		
    		//result.close();
    		rs.close();
    		statement.close();
    		statement2.close();
    		connection.close();
    		
    	}catch (Exception e) {
    		JsonArray jsonArray = new JsonArray();

			JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            
            jsonArray.add(responseJsonObject);
            out.write(jsonArray.toString());
    	}
    	
    }
    
    private void addNewMovie(PrintWriter out, String new_movie_title, String new_movie_year, 
    		String new_movie_director, String new_movie_genre, String star_name, String star_birth_year) {
    	try {
    		String newStarId = "";
    		String newMovieId = "";
    		String add_movie_SQL = "";
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
    		PreparedStatement statement2 = null;
    		
    		String getIdSQL = "SELECT max(s.id), max(m.id) FROM stars s, movies m";
    		
    		statement = connection.prepareStatement(getIdSQL);
    		
    		ResultSet rs = statement.executeQuery();
    		
    		while(rs.next()) {
    			newStarId = rs.getString("max(s.id)");
    			newMovieId = rs.getString("max(m.id)");
    		}

    		// Setting new movieId and starId
    		Integer id = Integer.parseInt(newStarId.substring(2)) + 1;
    		newStarId = "nm" + id.toString();
    		
    		id = Integer.parseInt(newMovieId.substring(2)) + 1;
    		newMovieId = "tt" + id.toString();
    		
    		if(!new_movie_genre.isEmpty() && !star_name.isEmpty() && !star_birth_year.isEmpty()) {
    			
    			// Add and connect genres and stars WITH birth year info to movies
        		add_movie_SQL = "CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?)";
        		statement2 = connection.prepareStatement(add_movie_SQL);
        		
        		statement2.setString(1, new_movie_title);
        		statement2.setInt(2, Integer.parseInt(new_movie_year));
        		statement2.setString(3, new_movie_director);
        		
        		statement2.setString(4, new_movie_genre);
        		
        		statement2.setString(5, star_name);
        		statement2.setInt(6, Integer.parseInt(star_birth_year));
        		
        		statement2.setString(7, newStarId);
        		statement2.setString(8, newMovieId);
    		}else if(!new_movie_genre.isEmpty() && !star_name.isEmpty() && star_birth_year.isEmpty()){
    			
    			// Add and connect genres and stars WITHOUT birth year info to movies
        		add_movie_SQL = "CALL add_movie(?, ?, ?, ?, ?, NULL, ?, ?)";
        		statement2 = connection.prepareStatement(add_movie_SQL);
        		
        		statement2.setString(1, new_movie_title);
        		statement2.setInt(2, Integer.parseInt(new_movie_year));
        		statement2.setString(3, new_movie_director);
        		
        		statement2.setString(4, new_movie_genre);
        		
        		statement2.setString(5, star_name);
        		
        		statement2.setString(6, newStarId);
        		statement2.setString(7, newMovieId);
    		}else if(!new_movie_genre.isEmpty() && star_name.isEmpty()){
    			
    			
    			// Add and connect genres to movies
        		add_movie_SQL = "CALL add_movie(?, ?, ?, ?, NULL, NULL, ?, ?)";
        		statement2 = connection.prepareStatement(add_movie_SQL);
        		
        		statement2.setString(1, new_movie_title);
        		statement2.setInt(2, Integer.parseInt(new_movie_year));
        		statement2.setString(3, new_movie_director);
        		
        		statement2.setString(4, new_movie_genre);
        		
        		statement2.setString(5, newStarId);
        		statement2.setString(6, newMovieId);
    		}else if(new_movie_genre.isEmpty() && !star_name.isEmpty() && star_birth_year.isEmpty() ){
    			
    			// Add and connect stars WITHOUT birth year info to movies
        		add_movie_SQL = "CALL add_movie(?, ?, ?, NULL, ?, NULL, ?, ?)";
        		statement2 = connection.prepareStatement(add_movie_SQL);
        		
        		statement2.setString(1, new_movie_title);
        		statement2.setInt(2, Integer.parseInt(new_movie_year));
        		statement2.setString(3, new_movie_director);
        		
        		statement2.setString(4, star_name);
        		
        		statement2.setString(5, newStarId);
        		statement2.setString(6, newMovieId);
    		}else if(new_movie_genre.isEmpty() && !star_name.isEmpty() && !star_birth_year.isEmpty() ){
    			
    			// Add and connect stars WITH birth year info to movies
        		add_movie_SQL = "CALL add_movie(?, ?, ?, NULL, ?, ?, ?, ?)";
        		statement2 = connection.prepareStatement(add_movie_SQL);
        		
        		statement2.setString(1, new_movie_title);
        		statement2.setInt(2, Integer.parseInt(new_movie_year));
        		statement2.setString(3, new_movie_director);
        		
        		statement2.setString(4, star_name);
        		statement2.setInt(5, Integer.parseInt(star_birth_year));
        		
        		statement2.setString(6, newStarId);
        		statement2.setString(7, newMovieId);
        		
    		}
    		
    		ResultSet result = statement2.executeQuery();
    		
    		// Get message from stored procedure.
    		while(result.next()) {
        		JsonObject responseJsonObject = new JsonObject();

    			String add_movie_message = result.getString("answer");

    			// Replace @ with new line to display nice feedback message 
    			add_movie_message = add_movie_message.replace("@", "<br>");
    			
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", add_movie_message);
                
                jsonArray.add(responseJsonObject);
    		}
            out.write(jsonArray.toString());
    		
    		rs.close();
    		result.close();
    		statement.close();
    		statement2.close();
    		connection.close();
    		
    	}catch (Exception e) {
    		JsonArray jsonArray = new JsonArray();
			JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            jsonArray.add(responseJsonObject);
            out.write(jsonArray.toString());
    	}
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json"); // Response mime type
		
		PrintWriter out = response.getWriter();
		
		String new_movie_title = request.getParameter("new_movie_title");
		String new_movie_year = request.getParameter("new_movie_year");
		String new_movie_director = request.getParameter("new_movie_director");
		String new_movie_genre = request.getParameter("new_movie_genre");
		String star_name = request.getParameter("star_name");
		String star_birth_year = request.getParameter("star_birth_year");
		
		if (new_movie_title.isEmpty() && new_movie_year.isEmpty() && new_movie_director.isEmpty() && new_movie_genre.isEmpty() && !star_name.isEmpty()) {
			
			addStar(out, star_name, star_birth_year);
			
		}else if(!new_movie_title.isEmpty() && !new_movie_year.isEmpty() && !new_movie_director.isEmpty() && (!new_movie_genre.isEmpty() || !star_name.isEmpty())){
			
			addNewMovie(out, new_movie_title, new_movie_year, new_movie_director, new_movie_genre, star_name, star_birth_year);
			
		}else {
    		JsonArray jsonArray = new JsonArray();
			JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Please Enter All Required Field");
            
            jsonArray.add(responseJsonObject);
            out.write(jsonArray.toString());
		}
		
		out.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
