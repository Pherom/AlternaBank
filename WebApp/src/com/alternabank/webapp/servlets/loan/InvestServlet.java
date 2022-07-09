package com.alternabank.webapp.servlets.loan;

import com.alternabank.dto.loan.request.InvestmentRequest;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "InvestServlet", urlPatterns = "/invest")
public class InvestServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (WebAppUtils.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            String username = WebAppUtils.getUsername(req);
            if (username != null) {
                try {
                    InvestmentRequest investmentRequest = WebAppUtils.GSON_INSTANCE.fromJson(req.getReader(), InvestmentRequest.class);
                    if (investmentRequest.getLenderName().equals(username)) {
                        LoanManager loanManager = WebAppUtils.getLoanManager(getServletContext());
                        synchronized (getServletContext()) {
                            loanManager.postInvestmentRequest(investmentRequest);
                        }
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
                    else resp.setStatus(HttpServletResponse.SC_CONFLICT);
                } catch (Exception e) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            }
            else resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
