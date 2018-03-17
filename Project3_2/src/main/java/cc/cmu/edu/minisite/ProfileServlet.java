package cc.cmu.edu.minisite;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONArray;

import java.sql.*;

public class ProfileServlet extends HttpServlet {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://ccp3.cwyuuwgo8ukt.us-east-1.rds.amazonaws.com/user";

    //  Database credentials
    static final String USER = "xingches";
    static final String PASS = "1993vivid";

    Connection conn = null;
    Statement stmt = null;

    public ProfileServlet() {
        /*
            Your initialization code goes here
        */
        super();

        try{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
        }catch(Exception e){
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {



        JSONObject result = new JSONObject();
        /*
            PreparedStatement leads to fast execution and helps prevent 
            malicious code injection. In this task, you are required to 
            use PreparedStatement for SQL queries. You can review the P3.1 
            writeup for more details.

        */
        String name = request.getParameter("id");
        String pwd = request.getParameter("pwd");
        String query = "SELECT * FROM username WHERE username=" + name;
//        String query = "SELECT * FROM username WHERE username=" + name + " AND password="+ pwd;

        try{
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            String username = rs.getString("username");
            String profile = rs.getString("avatar");
            result.put("name", username);
            result.put("profile", profile);
            //STEP 6: Clean-up environment
            rs.close();
//            stmt.close();
//            conn.close();
        }catch(SQLException se){
            //Handle errors for JDBC
            se.printStackTrace();
        }catch (Exception exc){
            exc.printStackTrace();
        }finally {
//            try{
//                if(stmt!=null)
//                    stmt.close();
//            }catch(SQLException se2){
//            }// nothing we can do
//            try{
//                if(conn!=null)
//                    conn.close();
//            }catch(SQLException se){
//                se.printStackTrace();
//            }//end finally try
        }


        /*
            Task 1:
            This query simulates the login process of a user 
            and tests whether your backend system is functioning properly. 
            Your web application will receive a pair of UserName and Password, 
            and you need to check your backend database to see if the 
	        UserName and Password is a valid pair. 
            You should construct your response accordingly:

            If YES, send back the userName and Profile Image URL.
            If NOT, set userName as "Unauthorized" and Profile Image URL as "#".
        */
        
        PrintWriter writer = response.getWriter();
        writer.write(String.format("returnRes(%s)", result.toString()));
        writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
