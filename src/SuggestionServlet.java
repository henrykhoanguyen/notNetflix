

import java.io.IOException;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class SuggestionServlet
 */
@WebServlet("/SuggestionServlet")
public class SuggestionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    // Create a dataSource which registered in web.xml
    /*@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;*/
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                System.out.println("envCtx is NULL");

            // Look up our data source
            DataSource dataSource = (DataSource) envCtx.lookup("jdbc/moviedb");

            if (dataSource == null)
                System.out.println("ds is null.");
			
			// setup the response json arrray
			JsonArray jsonArray = new JsonArray();
			
			Connection dbcon = dataSource.getConnection();
			
			// get the query string from parameter
			String query = request.getParameter("query");
			
			// return the empty json array if query is null or empty
			if (query == null || query.trim().isEmpty() || query.length() < 3) {
				response.getWriter().write(jsonArray.toString());
				return;
			}	
			
			// search on marvel heros and DC heros and add the results to JSON Array
			// this example only does a substring match
			// TODO: in project 4, you should do full text search with MySQL to find the matches on movies and stars
			
			/*for (Integer id : marvelHerosMap.keySet()) {
				String heroName = marvelHerosMap.get(id);
				if (heroName.toLowerCase().contains(query.toLowerCase())) {
					jsonArray.add(generateJsonObject(id, heroName, "marvel"));
				}
			}
			
			for (Integer id : dcHerosMap.keySet()) {
				String heroName = dcHerosMap.get(id);
				if (heroName.toLowerCase().contains(query.toLowerCase())) {
					jsonArray.add(generateJsonObject(id, heroName, "dc"));
				}
			}*/
			
			String movieQuery = "SELECT * FROM movies WHERE MATCH(title) AGAINST(?)";
			//int i = 0;
			PreparedStatement statement = null;
			ResultSet rs = null;
						
			statement = dbcon.prepareStatement(movieQuery);
			
			statement.setString(1, query);
			
			rs = statement.executeQuery();
			while(rs.next()) {
				String title = rs.getString("title");
				String id = rs.getString("id");
				jsonArray.add(generateJsonObject(id, title, "MOVIES"));
				//i++;
			}			
			
			response.getWriter().write(jsonArray.toString());
			
			rs.close();
			statement.close();
			dbcon.close();
			return;
		} catch (Exception e) {
			System.out.println(e);
			response.sendError(500, e.getMessage());
		}
	}
	
	/*
	 * Generate the JSON Object from hero and category to be like this format:
	 * {
	 *   "value": "some movie's title / star's name",
	 *   "data": { "category": "movie/star", "id": "tt000001" }
	 * }
	 * 
	 */
	private static JsonObject generateJsonObject(String id, String name, String categoryName) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", name);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("category", categoryName);
		additionalDataJsonObject.addProperty("id", id);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}

}
