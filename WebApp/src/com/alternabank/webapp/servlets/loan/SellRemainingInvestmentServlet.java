package com.alternabank.webapp.servlets.loan;

import com.alternabank.engine.loan.LoanManager;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "SellRemainingInvestmentServlet", urlPatterns = "/sell-investment")
public class SellRemainingInvestmentServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (WebAppUtils.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            String username = WebAppUtils.getUsername(req);
            if (username != null) {
                String loanIDFromParameter = req.getParameter("loan");
                if (loanIDFromParameter != null && !loanIDFromParameter.isEmpty()) {
                    LoanManager loanManager = WebAppUtils.getLoanManager(getServletContext());
                    boolean success;
                    synchronized (getServletContext()) {
                        success = loanManager.postRemainingInvestmentForSale(username, loanIDFromParameter);
                    }
                    resp.setStatus(success ? HttpServletResponse.SC_OK : HttpServletResponse.SC_CONFLICT);
                } else resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
            else resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
