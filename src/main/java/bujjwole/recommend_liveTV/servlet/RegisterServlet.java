package bujjwole.recommend_liveTV.servlet;

import bujjwole.recommend_liveTV.database.MySQLConnection;
import bujjwole.recommend_liveTV.exception.MySQLException;
import bujjwole.recommend_liveTV.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = ServletUtils.readRequest(User.class, request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean isRegistered = false;
        MySQLConnection connection = null;
        try {
            connection = new MySQLConnection();
            user.setPassword(ServletUtils.encryptPassword(user.getUserId(), user.getPassword()));
            isRegistered = connection.register(user);
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            connection.close();
        }

        if (!isRegistered) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

}
