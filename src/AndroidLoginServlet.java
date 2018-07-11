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

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	// Create a dataSource which registered in web.xml
	/*@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;*/
	
    public AndroidLoginServlet() {
        super();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        System.out.println("username " + username);
        Map<String, String[]> map = request.getParameterMap();
        for (String key: map.keySet()) {
            System.out.println("key " + key);
            System.out.println("value " + map.get(key)[0]);
        }
        
        
        // then verify username / password
        JsonObject loginResult = verifyUsernamePassword(username, password);
        
        if (loginResult.get("status").getAsString().equals("success")) {
            // login success
            request.getSession().setAttribute("user", new User(username));
            response.getWriter().write(loginResult.toString());
        } else {
            response.getWriter().write(loginResult.toString());
        }

    }
    
    public JsonObject verifyUsernamePassword(String username, String password) {
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
    		PreparedStatement statement = null;
    		
    		// prepare query
    		String query = "SELECT password"
    				+ " FROM customers"
    				+ " WHERE email = ?";
    		
    		statement = connection.prepareStatement(query);
    		
    		statement.setString(1, username);
    		
    		// execute query
    		ResultSet resultSet = statement.executeQuery();
    		String encryptedPassword = "";
    		boolean correctPass = false;
    		boolean existName = resultSet.next();
    		if(existName) {
        		encryptedPassword = resultSet.getString("password");
        		correctPass = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
        		
        		resultSet.close();
        		statement.close();
        		connection.close();
    		}
    		
            /* This example only allows username/password to be test/test
            /  in the real project, you should talk to the database to verify username/password
            */
    		//System.out.println("HELLO " + existName + " " + correctPass);
            if (existName && correctPass) {
                // Login success:
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                
                return responseJsonObject;
            } else {
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                if (!existName) {
                    responseJsonObject.addProperty("message", "User " + username + " doesn't exist");
                } else if (!correctPass) {
                    responseJsonObject.addProperty("message", "Incorrect password");
                }
                return responseJsonObject;
            }
            

        }catch(Exception e){
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            
            return responseJsonObject;
    		
        }
        
    }

}
