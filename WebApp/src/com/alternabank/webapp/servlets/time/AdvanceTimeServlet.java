package com.alternabank.webapp.servlets.time;

import com.alternabank.engine.time.TimeManager;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdvanceTimeServlet", urlPatterns = "/advance-time")
public class AdvanceTimeServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        boolean isAdmin = WebAppUtils.isAdmin(req);
        if(isAdmin) {
            TimeManager timeManager = WebAppUtils.getTimeManager(getServletContext());
            resp.setStatus(HttpServletResponse.SC_OK);
            synchronized (getServletContext()) {
                timeManager.advanceTime();
            }
        }
        else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
