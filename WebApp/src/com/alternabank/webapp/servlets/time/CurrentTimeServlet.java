package com.alternabank.webapp.servlets.time;

import com.alternabank.dto.time.ServerTime;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.webapp.util.WebAppUtils;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CurrentTimeServlet", urlPatterns = "/current-time")
public class CurrentTimeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.setContentType("application/json");
        TimeManager timeManager = WebAppUtils.getTimeManager(getServletContext());
        resp.setStatus(HttpServletResponse.SC_OK);
        ServerTime currentTime;
        synchronized (getServletContext()) {
            currentTime = new ServerTime(TimeManager.TIME_UNIT_NAME, timeManager.getCurrentTime(), timeManager.isRewindMode());
        }
        String currentTimeJSON = WebAppUtils.GSON_INSTANCE.toJson(currentTime);
        resp.getWriter().print(currentTimeJSON);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
