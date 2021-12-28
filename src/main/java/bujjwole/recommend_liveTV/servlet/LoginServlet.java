package bujjwole.recommend_liveTV.servlet;


import bujjwole.recommend_liveTV.database.MySQLConnection;
import bujjwole.recommend_liveTV.exception.MySQLException;
import bujjwole.recommend_liveTV.model.LoginRequest;
import bujjwole.recommend_liveTV.model.LoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoginRequest loginRequest = ServletUtils.readRequest(LoginRequest.class, request);
        if (loginRequest == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String username;
        MySQLConnection connection = null;
        try {
            connection = new MySQLConnection();
            String userId = loginRequest.getUserId();
            String password = ServletUtils.encryptPassword(userId, loginRequest.getPassword());
            username = connection.verifyLogin(userId, password);
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            connection.close();
        }

        if (!username.isEmpty()) {
            HttpSession session = request.getSession();
            String userId = loginRequest.getUserId();
            session.setAttribute("user_id", userId);
            session.setMaxInactiveInterval(864000);

            LoginResponse loginResponse = new LoginResponse(userId, username);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(new ObjectMapper().writeValueAsString(loginResponse));
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

}
