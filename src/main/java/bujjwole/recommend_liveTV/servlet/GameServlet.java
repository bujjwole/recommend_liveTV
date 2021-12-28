package bujjwole.recommend_liveTV.servlet;

import bujjwole.recommend_liveTV.exception.TwitchException;
import bujjwole.recommend_liveTV.external.TwitchUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

@WebServlet(name = "GameServlet", urlPatterns = {"/game"})
public class GameServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String gameName = request.getParameter("game_name");
        TwitchUser user = new TwitchUser();

        response.setContentType("application/json;charset=UTF-8");
        try {
            if (gameName != null) {
                response.getWriter().print(new ObjectMapper().writeValueAsString(user.searchGame(gameName)));
            } else {
                response.getWriter().print(new ObjectMapper().writeValueAsString(user.getTopGames(20)));
            }
        } catch (TwitchException e) {
            throw new ServletException(e);
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}

