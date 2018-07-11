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
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleServlet", urlPatterns = "/api/single")
public class SingleServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;

	// Create a dataSource which registered in web.xml
	/*@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;*/

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("application/json"); // Response mime type

		// Retrieve parameter id from url request.
		String id = request.getParameter("id");
		
		// Output stream to STDOUT
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
            
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			
			JsonArray jsonArray = new JsonArray();
			ResultSet rs = null;
			PreparedStatement statement = null;
			
			if(id.substring(0, 2).equalsIgnoreCase("nm")) {
				
				// Construct a query with parameter represented by "?"
				String query = "SELECT * from stars as s, stars_in_movies as sim, movies as m where m.id = sim.movieId and sim.starId = s.id and s.id = ?";

				// Declare our statement
				statement = dbcon.prepareStatement(query);

				// Set the parameter represented by "?" in the query to the id we get from url,
				// num 1 indicates the first "?" in the query
				statement.setString(1, id);

				// Perform the query
				rs = statement.executeQuery();

				
				// Iterate through each row of rs
				while (rs.next()) {

					String starId = rs.getString("starId");
					String starName = rs.getString("name");
					String starDob = rs.getString("birthYear");

					String movieId = rs.getString("movieId");
					String movieTitle = rs.getString("title");
					String movieYear = rs.getString("year");
					String movieDirector = rs.getString("director");
					//System.out.println("hello! " + starId);
					// Create a JsonObject based on the data we retrieve from rs

					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("star_id", starId);
					jsonObject.addProperty("star_name", starName);
					jsonObject.addProperty("star_dob", starDob);
					jsonObject.addProperty("movie_id", movieId);
					jsonObject.addProperty("movie_title", movieTitle);
					jsonObject.addProperty("movie_year", movieYear);
					jsonObject.addProperty("movie_director", movieDirector);

					jsonArray.add(jsonObject);
				}
			}else if (id.substring(0, 2).equalsIgnoreCase("tt")) {
				
				// Construct a query with parameter represented by "?"
				String query = "SELECT * FROM movies WHERE id = ? ";
				
				// Declare our statement
				statement = dbcon.prepareStatement(query);
				
				// Set the parameter represented by "?" in the query to the id we get from url,
				// num 1 indicates the first "?" in the query
				statement.setString(1, id);
				
				// Perform the query
				rs = statement.executeQuery();
				
				while(rs.next()) {
					String movieId = rs.getString("id");
					String movieTitle = rs.getString("title");
					String movieYear = rs.getString("year");
					String movieDirector = rs.getString("director");
					String movieRating = "N/A";
					String genres = "";
					String stars = "";
					
					// Query for Genres and Stars
					String genreSQL = "SELECT g.name, g.id "
							+ "FROM genres g, movies m, genres_in_movies gim "
							+ "WHERE m.id = ? AND m.id = gim.movieId AND g.id = gim.genreID";
					
					String starSQL = "SELECT s.name, s.id "
							+ "FROM stars s, movies m, stars_in_movies sim "
							+ "WHERE m.id = ? AND m.id = sim.movieId AND s.id = sim.starId";
					
					String ratingSQL = "SELECT r.rating "
							+ "FROM movies m, ratings r "
							+ "WHERE m.id = ? AND m.id = r.movieId ";
					
					// Declare temporary 2 prep statements and 2 result sets
					// for genres and stars in each movie.
					PreparedStatement gStatement, sStatement, rStatement;
					ResultSet gResult, sResult, rResult;
					
					gStatement = dbcon.prepareStatement(genreSQL);
					sStatement = dbcon.prepareStatement(starSQL);
					rStatement = dbcon.prepareStatement(ratingSQL);
					
					// Replace ? with movie id
					gStatement.setString(1, movieId);
					sStatement.setString(1, movieId);
					rStatement.setString(1, movieId);
					
					// execute genres and stars SQL statement
					gResult = gStatement.executeQuery();
					sResult = sStatement.executeQuery();
					rResult = rStatement.executeQuery();
					
					// Create a JsonObject based on the data we retrieve from rs
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("movie_id", movieId);
					jsonObject.addProperty("movie_title", movieTitle);
					jsonObject.addProperty("movie_year", movieYear);
					jsonObject.addProperty("movie_director", movieDirector);
					
					if (rResult.next()) {
						movieRating = rResult.getString("rating");
					}
					jsonObject.addProperty("movie_rating", movieRating);
					
					// Create an array list of all genres of a movie
					while(gResult.next()) {
						genres += "<a href=\"movieList.html?id=" + gResult.getString("id") 
						+ "&perPage=10&pgNum=1&sTitle=Norm&sRating=Norm\"><b>" + gResult.getString("name") + "</b></a>";
						if(!gResult.isLast()) {
							genres += ", ";
						}
					}
					
					// Create an array list of all stars of a movie
					while(sResult.next()) {
						stars += "<a href=\"single.html?id=" + sResult.getString("id") 
						+ "\"><b>" + sResult.getString("name") + "</b></a>";
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
					rResult.close();
					gStatement.close();
					sStatement.close();
					rStatement.close();
				}
			}
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
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
