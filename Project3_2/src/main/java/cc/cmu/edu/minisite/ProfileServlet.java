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

public class ProfileServlet extends HttpServlet {

    public ProfileServlet() {
        /*
            Your initialization code goes here
        */
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
        String query = "SELECT A FROM B WHERE C = ? AND D = ?";



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
