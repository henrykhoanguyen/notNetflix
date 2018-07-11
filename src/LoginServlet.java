
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

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


/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name = "/LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public String getServletInfo() {
        return "Servlet connects to MySQL database and displays result of a SELECT";
    }
    
    // Create a dataSource which registered in web.xml
    /*@Resource(name = "jdbc/moviedb")
    private DataSource dataSource;*/
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch (Exception e) {
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", "Please Reverify reCAPTCHA!");
            
            out.write(responseJsonObject.toString());
            
            out.close();
            return;
        }
        
        try {
        	
        	String contextPath = getServletContext().getRealPath("/");

    		String xmlFilePath = contextPath+"log.txt";

    		System.out.println("xmlFilePath Login " + xmlFilePath);
    		File myfile = new File(xmlFilePath);
    		if(myfile.exists()) {
    			myfile.delete();
    		}
    		
        	Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource dataSource = (DataSource) envCtx.lookup("jdbc/moviedb");

            if (dataSource == null)
                out.println("ds is null.");
        	
    		// create database connection
    		Connection connection = dataSource.getConnection();
    		
    		// declare statement
    		PreparedStatement statement = null;
    		
    		// prepare query
    		String query = "SELECT password"
    				+ " from customers"
    				+ " where email = ?";
    		
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
    		}
    		
            /* This example only allows username/password to be test/test
            /  in the real project, you should talk to the database to verify username/password
            */
    		//System.out.println("HELLO " + existName + " " + correctPass);
            if (existName && correctPass) {
                // Login success:

                // set this user into the session
                request.getSession().setAttribute("user", new User(username));

                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");

                response.getWriter().write(responseJsonObject.toString());
            } else {
                // Login fail
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                if (!existName) {
                    responseJsonObject.addProperty("message", "User \"" + username + "\" doesn't exist");
                } else if (!correctPass) {
                    responseJsonObject.addProperty("message", "Incorrect password");
                }
                response.getWriter().write(responseJsonObject.toString());
            }
            
    		resultSet.close();
    		statement.close();
    		connection.close();

        }catch(Exception e){
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("status", "fail");
            responseJsonObject.addProperty("message", e.getMessage());
            
            out.write(responseJsonObject.toString());
    		
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
