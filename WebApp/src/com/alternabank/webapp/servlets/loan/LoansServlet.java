package com.alternabank.webapp.servlets.loan;

import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.engine.time.TimeManager;
import com.alternabank.webapp.util.WebAppUtils;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "LoansServlet", urlPatterns = "/loans")
public class LoansServlet extends HttpServlet {
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        boolean isAuthorized = WebAppUtils.isAdmin(req) || WebAppUtils.getUsername(req) != null;
        if (isAuthorized) {
            LoanManager loanManager = WebAppUtils.getLoanManager(getServletContext());
            TimeManager timeManager = WebAppUtils.getTimeManager(getServletContext());
            Map<String, LoanDetails> loanDetails;
            synchronized (getServletContext()) {
                if (!timeManager.isRewindMode())
                    loanDetails = loanManager.getLoanDetails();
                else loanDetails = loanManager.getLoanDetails(timeManager.getCurrentTime());
            }
            String loanDetailsJSON = WebAppUtils.GSON_INSTANCE.toJson(loanDetails);
            resp.getWriter().print(loanDetailsJSON);
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
