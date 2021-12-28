package bujjwole.recommend_liveTV.servlet;

import bujjwole.recommend_liveTV.database.MySQLConnection;
import bujjwole.recommend_liveTV.exception.MySQLException;
import bujjwole.recommend_liveTV.model.Favorite;
import bujjwole.recommend_liveTV.model.Item;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.*;

@WebServlet(name = "FavoriteServlet", value = "/favorite")
public class FavoriteServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = ServletUtils.getUserId(request.getSession(false));
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Map<String, List<Item>> itemMap;
        MySQLConnection connection = null;
        try {
            connection = new MySQLConnection();
            itemMap = connection.getFavoriteItems(userId);
            ServletUtils.writeItemMap(response, itemMap);
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = ServletUtils.getUserId(request.getSession(false));
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Favorite favorite = ServletUtils.readRequest(Favorite.class, request);
        if (favorite == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        MySQLConnection connection = null;
        try {
            connection = new MySQLConnection();
            connection.setFavoriteItem(userId, favorite.getFavoriteItem());
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userId = ServletUtils.getUserId(request.getSession(false));
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Favorite favorite = ServletUtils.readRequest(Favorite.class, request);
        if (favorite == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        MySQLConnection connection = null;
        try {
            connection = new MySQLConnection();
            connection.unsetFavoriteItem(userId, favorite.getFavoriteItem().getId());
        } catch (MySQLException e) {
            throw new ServletException(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
