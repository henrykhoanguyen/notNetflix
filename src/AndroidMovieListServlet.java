

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

/**
 * Servlet implementation class AndroidMovieListServlet
 */
@WebServlet(name = "/AndroidMovieListServlet", urlPatterns = "/api/movie-list-view")
public class AndroidMovieListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Create a dataSource which registered in web.xml
	/*@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;*/
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AndroidMovieListServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		System.out.println("Query " + query);
		JsonArray jsonArray = new JsonArray();
        
		try {
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource dataSource = (DataSource) envCtx.lookup("jdbc/moviedb");

            if (dataSource == null)
                System.out.println("ds is null.");
            
			Connection dbcon = dataSource.getConnection();
			
    		// declare statement
    		PreparedStatement statement = null;
    		
    		String sql = "SELECT * FROM movies WHERE MATCH(title) AGAINST(?)";
    		
    		statement = dbcon.prepareStatement(sql);
    		
    		statement.setString(1, query);
    		
    		ResultSet rs = statement.executeQuery();
    		
    		while(rs.next()) {
				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				
				String genres = "";
				String stars = "";
				
				// Query for Genres and Stars
				String genreSQL = "SELECT g.name "
						+ "FROM genres g, movies m, genres_in_movies gim "
						+ "WHERE m.id = ? AND m.id = gim.movieId AND g.id = gim.genreID";
				
				String starSQL = "SELECT s.name, s.id "
						+ "FROM stars s, movies m, stars_in_movies sim "
						+ "WHERE m.id = ? AND m.id = sim.movieId AND s.id = sim.starId";
				
				// Declare temporary 2 prep statements and 2 result sets
				// for genres and stars in each movie.
				PreparedStatement gStatement, sStatement;
				ResultSet gResult, sResult;
				
				// Prepare Statement
				gStatement = dbcon.prepareStatement(genreSQL);
				sStatement = dbcon.prepareStatement(starSQL);
				
				// Replace ? with movie id
				gStatement.setString(1, movieId);
				sStatement.setString(1, movieId);
				
				// execute genres and stars SQL statement
				gResult = gStatement.executeQuery();
				sResult = sStatement.executeQuery();
				
				// Create a JsonObject based on the data we retrieve from rs
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", "success");
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				
				// Create an array list of all genres of a movie
				while(gResult.next()) {
					genres += gResult.getString("name");
					if(!gResult.isLast()) {
						genres += ", ";
					}
				}
				
				// Create an array list of all stars of a movie
				while(sResult.next()) {
					stars += sResult.getString("name");
					if(!sResult.isLast()) {
						stars += ", ";
					}
				}
				// Add genres as a property to json object
				jsonObject.addProperty("movie_genres", genres);
				// Add stars as a property to json object
				jsonObject.addProperty("movie_stars", stars);
				
				jsonArray.add(jsonObject);
				
				gResult.close();
				sResult.close();
				gStatement.close();
				sStatement.close();
    		}
            // write JSON string to output
            response.getWriter().write(jsonArray.toString());
			rs.close();
			statement.close();
    		dbcon.close();
			
		}catch(Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", e.getMessage());

			jsonArray.add(jsonObject);
            response.getWriter().write(jsonArray.toString());
		}
	}


}
