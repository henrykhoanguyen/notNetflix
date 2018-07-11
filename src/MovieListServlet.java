
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Servlet implementation class MovieListServlet
 */
@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movies-list")
public class MovieListServlet extends HttpServlet {
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
		String sortTitle = request.getParameter("sTitle");
		String sortRating = request.getParameter("sRating");		
		
		String movieTitle = request.getParameter("title");
		String movieYear = request.getParameter("year");
		String movieDirector = request.getParameter("director");
		String movieStar = request.getParameter("starName");
		//System.out.println("HELLO " + movieTitle);
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		
		if(id.equalsIgnoreCase("search")) {
			fromSearch(out, response, id, sortTitle, sortRating, movieTitle, movieYear, movieDirector, movieStar);
			//System.out.println("HELLO ");
		}else {
			fromBrowse(out, response, id, sortTitle, sortRating);
		}
		
		out.close();

	}
	
	
	private void fromSearch(PrintWriter out, HttpServletResponse response, String id, String sortTitle, String sortRating,
			String title, String year, String director, String starName) {
		String contextPath = getServletContext().getRealPath("/");

		String xmlFilePath = contextPath+"log.txt";

		System.out.println("xmlFilePath " + xmlFilePath);
		File myfile = new File(xmlFilePath);
		FileWriter fout = null;
		
		// Time an event in a program to nanosecond precision
		long startTime = System.nanoTime();
		JsonArray jsonArray = new JsonArray();
		try {
			if(!myfile.exists() && !myfile.isDirectory()) {
				System.out.println("hello myfile");
				myfile.createNewFile();
				fout = new FileWriter(myfile);
			}else {
				System.out.println("hello fout");
				fout = new FileWriter(myfile, true);
			}
			
			boolean resultExist = false;
			boolean whereClauseEmpty = true;
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
			String query = "SELECT m.id, m.title, m.year, m.director " + 
							"FROM movies m " + 
							"WHERE ";
			//System.out.println("HELLO 1");
			// Declare our statement
			PreparedStatement statement = null;
			String [] arr = null;
			// append sql statemnt that look for movie title
			if(!title.equalsIgnoreCase("")) {
				arr = title.split(" ");
				
				for(int num = 0; num < arr.length; num++) {
					if(num == 0) {
						query += "match(m.title) against (? IN BOOLEAN MODE) ";
					}else {
						query += "and match(m.title) against (? IN BOOLEAN MODE) ";
					}
					
				}
				
				whereClauseEmpty = false;
			}
			
			// append sql statemnt that look for movie released year
			if(!year.equalsIgnoreCase("") && !whereClauseEmpty) {
				query += "AND m.year like ? ";	
				
			}else if(!year.equalsIgnoreCase("") && whereClauseEmpty) {
				
				query += " m.year like ? ";	
			}
			
			// append sql statemnt that look for movie director
			if(!director.equalsIgnoreCase("") && !whereClauseEmpty) {
				
				query += "AND m.director like ? ";
				
			}else if(!director.equalsIgnoreCase("") && whereClauseEmpty) {
				
				query += " m.director like ? ";
				
			}
			
			// append sql statemnt that look for star name
			if(!starName.equalsIgnoreCase("") && !whereClauseEmpty) {
				
				query += "AND m.id IN (SELECT mov1.id FROM movies mov1, stars s, stars_in_movies sim WHERE mov1.id = sim.movieId AND s.id = sim.starId AND s.name like ?) ";
				
			}else if(!starName.equalsIgnoreCase("") && whereClauseEmpty) {
				
				query += " m.id IN (SELECT mov1.id FROM movies mov1, stars s, stars_in_movies sim WHERE mov1.id = sim.movieId AND s.id = sim.starId AND s.name like ?) ";
				
			}
			
			if(sortRating.equalsIgnoreCase("ASC")) {
				
				query += "AND m.id IN (SELECT mov.id FROM movies mov, ratings r where mov.id = r.movieId ORDER BY r.rating ASC)";
				
			}else if (sortRating.equalsIgnoreCase("DESC")) {
				
				query += "AND m.id IN (SELECT mov.id FROM movies mov, ratings r where mov.id = r.movieId ORDER BY r.rating DESC)";
				
			}
			
			if(sortTitle.equalsIgnoreCase("ASC")){
				
				query += "ORDER BY m.title ASC";
				
			}else if(sortTitle.equalsIgnoreCase("DESC")) {
				
				query += "ORDER BY m.title DESC";
			}
			
			statement = dbcon.prepareStatement(query);
			//System.out.println("HELLO " + statement);
			int counter = 1;
			// setString for sql statemnt that look for movie title
			if(!title.equalsIgnoreCase("")) {
				while(counter <= arr.length) {
					
					statement.setString(counter, "+" + arr[counter-1] + "*");
					counter++;
					
				}
			}
			
			// setString for sql statemnt that look for movie released year
			if(!year.equalsIgnoreCase("")) {
				statement.setString(counter, "%" + year + "%");
				counter++;	
			}
			
			// setString for sql statemnt that look for movie director
			if(!director.equalsIgnoreCase("")) {
				statement.setString(counter, "%" + director + "%");
				counter++;
			}
			//System.out.println("HELLO 33");
			// setString for sql statemnt that look for star name
			if(!starName.equalsIgnoreCase("")) {
				statement.setString(counter, "%" + starName + "%");
				counter++;
			}
			counter = 1;

			long startTime2 = System.nanoTime();
			// execute query
			ResultSet rs = statement.executeQuery();
			long endTime2 = System.nanoTime();
			long elapsedTime2 = endTime2 - startTime2; // elapsed time in nano seconds. Note: print the values in nano seconds 
			System.out.println("TJ " + elapsedTime2);
			//fout.write("TJ " + elapsedTime2 + "\n");
			fout.append("TJ " + elapsedTime2 + "\n");
			
			// Iterate through each row of rs
			while (rs.next()) {
				resultExist = true;
				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				
				String rating = "N/A";
				String genres = "";
				String stars = "";
				//System.out.println("HELLO 3");
				//System.out.println("MOVIE " + movieId);
				
				// Query for Genres and Stars
				String genreSQL = "SELECT g.name "
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
				
				// Prepare Statement
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
				jsonObject.addProperty("status", "success");
				jsonObject.addProperty("message", "success");
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				
				
				if(rResult.next()) {
					rating = rResult.getString("rating");
				}
				jsonObject.addProperty("movie_rating", rating);
				
				// Create an array list of all genres of a movie
				while(gResult.next()) {
					genres += gResult.getString("name");
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
			
			if(!resultExist) {
				// write error message JSON object to output
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("status", "fail");
				jsonObject.addProperty("message", "There Is No Result Of What You Are Searching For!");
				//System.out.println("HELLO 5");
				jsonArray.add(jsonObject);
			}
			
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			statement.close();
			dbcon.close();
			long endTime = System.nanoTime();
			long elapsedTime = endTime - startTime; // elapsed time in nano seconds. Note: print the values in nano seconds 
			System.out.println("TS " + elapsedTime);
			//fout.write("TS " + elapsedTime + "\n");
			fout.append("TS " + elapsedTime + "\n");
			fout.close();
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", e.getMessage());
			
			jsonArray.add(jsonObject);
			out.write(jsonArray.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}
	
	
	private void fromBrowse(PrintWriter out, HttpServletResponse response, String id, String sortTitle, String sortRating) {
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
			String genreQuery = "";
			
			// Declare our statement
			PreparedStatement statement = null;
			
			ResultSet rs = null;
			
			// Construct a query with parameter represented by "?"
			if(sortTitle.equalsIgnoreCase("Norm") && sortRating.equalsIgnoreCase("Norm")) {
				
				genreQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
						+ "FROM movies as m, genres_in_movies as gim, ratings as r "
						+ "WHERE gim.genreId = ? AND gim.movieId = m.id AND r.movieId = m.id";
				
			}else if(sortTitle.equalsIgnoreCase("Norm") && sortRating.equalsIgnoreCase("ASC")) {
				
				genreQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
						+ "FROM movies as m, genres_in_movies as gim, ratings as r "
						+ "WHERE gim.genreId = ? AND gim.movieId = m.id AND r.movieId = m.id "
						+ "ORDER BY r.rating ASC";
				
			}else if(sortTitle.equalsIgnoreCase("Norm") && sortRating.equalsIgnoreCase("DESC")) {
				
				genreQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
						+ "FROM movies as m, genres_in_movies as gim, ratings as r "
						+ "WHERE gim.genreId = ? AND gim.movieId = m.id AND r.movieId = m.id "
						+ "ORDER BY r.rating DESC";
				
			}else if(sortTitle.equalsIgnoreCase("ASC") && sortRating.equalsIgnoreCase("Norm")){
				
				genreQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
						+ "FROM movies as m, genres_in_movies as gim, ratings as r "
						+ "WHERE gim.genreId = ? AND gim.movieId = m.id AND r.movieId = m.id "
						+ "ORDER BY m.title ASC";
				
			}else if(sortTitle.equalsIgnoreCase("DESC") && sortRating.equalsIgnoreCase("Norm")){
				
				genreQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
						+ "FROM movies as m, genres_in_movies as gim, ratings as r "
						+ "WHERE gim.genreId = ? AND gim.movieId = m.id AND r.movieId = m.id "
						+ "ORDER BY m.title DESC";
				
			}
						
			if(Character.isDigit(id.charAt(0)) && Integer.parseInt(id) < 30) {
				statement = dbcon.prepareStatement(genreQuery);
				//System.out.println(id);
				// Set the parameter represented by "?" in the query to the id we get from url,
				// num 1 indicates the first "?" in the query
				statement.setString(1, id);
				//System.out.println(statement.toString());
				
				// Perform the query
				rs = statement.executeQuery();
			}else {
				int tempId = 0;
				String titleQuery = "";
				
				if(Character.isDigit(id.charAt(0))) {
					tempId = Integer.parseInt(id) - 30;
					
					if(sortTitle.equalsIgnoreCase("Norm") && sortRating.equalsIgnoreCase("Norm")) {
						
						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId ";
						
					}else if(sortTitle.equalsIgnoreCase("Norm") && sortRating.equalsIgnoreCase("ASC")){

						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId "
								+ "ORDER BY r.rating ASC";
						
					}else if(sortTitle.equalsIgnoreCase("Norm") && !sortRating.equalsIgnoreCase("DESC")){

						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId "
								+ "ORDER BY r.rating DESC";
						
					}else if(sortTitle.equalsIgnoreCase("ASC") && sortRating.equalsIgnoreCase("Norm")){
						
						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId "
								+ "ORDER BY m.title ASC";
						
					}else if(sortTitle.equalsIgnoreCase("DESC") && sortRating.equalsIgnoreCase("Norm")){
						
						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId "
								+ "ORDER BY m.title DESC";
						
					}
					statement = dbcon.prepareStatement(titleQuery);
					statement.setString(1, tempId + "%");
					
				}else {
					
					if(sortTitle.equalsIgnoreCase("Norm") && sortRating.equalsIgnoreCase("Norm")) {
						
						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId";
						
					}else if(sortTitle.equalsIgnoreCase("Norm") && sortRating.equalsIgnoreCase("ASC")){

						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId "
								+ "ORDER BY r.rating ASC";
						
					}else if(sortTitle.equalsIgnoreCase("Norm") && sortRating.equalsIgnoreCase("DESC")){

						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId "
								+ "ORDER BY r.rating DESC";
						
					}else if(!sortTitle.equalsIgnoreCase("ASC") && sortRating.equalsIgnoreCase("Norm")){
						
						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId "
								+ "ORDER BY m.title ASC";
						
					}else if(!sortTitle.equalsIgnoreCase("DESC") && sortRating.equalsIgnoreCase("Norm")){
						
						titleQuery = "SELECT m.id, m.title, m.year, m.director, r.rating "
								+ "FROM movies m, ratings r "
								+ "WHERE m.title like ? AND m.id = r.movieId "
								+ "ORDER BY m.title DESC";
						
					}
					statement = dbcon.prepareStatement(titleQuery);
					statement.setString(1, id + "%");

				}
				rs = statement.executeQuery();
			}
			
			//System.out.println("HELLOOO  " + statement);
			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
			while (rs.next()) {

				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String rating = rs.getString("rating");
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
				jsonObject.addProperty("message", "success");
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				jsonObject.addProperty("movie_rating", rating);
				
				// Create an array list of all genres of a movie
				while(gResult.next()) {
					genres += gResult.getString("name");
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
				gStatement.close();
				sStatement.close();
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
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}

}
