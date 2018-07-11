

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet(name = "/SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String star = request.getParameter("star");
        boolean userInput = false;
		
        
		if(!title.equalsIgnoreCase("") || !year.equalsIgnoreCase("") || !director.equalsIgnoreCase("") || !star.equalsIgnoreCase("")) {
			userInput = true;
		}
		
		if(userInput) {
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "success");
            responseJsonObject.addProperty("message", "success");
            
            responseJsonObject.addProperty("movie_title", title);
            responseJsonObject.addProperty("movie_year", year);
            responseJsonObject.addProperty("movie_director", director);
            responseJsonObject.addProperty("movie_star", star);

            response.getWriter().write(responseJsonObject.toString());
		}else {
			// Login fail
            JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Please Insert Something To Search");
            response.getWriter().write(responseJsonObject.toString());
		}
	}

}
