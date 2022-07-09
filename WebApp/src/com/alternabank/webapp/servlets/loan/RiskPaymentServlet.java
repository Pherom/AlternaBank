package com.alternabank.webapp.servlets.loan;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.LoanManager;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RiskPaymentServlet", urlPatterns = "/pay-risk")
public class RiskPaymentServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (WebAppUtils.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            String username = WebAppUtils.getUsername(req);
            if (username != null) {
                String loanID = req.getParameter("loan");
                double paymentTotal;
                try {
                    paymentTotal = Double.parseDouble(req.getParameter("total"));
                    if (loanID != null) {
                        LoanManager loanManager = WebAppUtils.getLoanManager(getServletContext());
                        synchronized (getServletContext()) {
                            Loan loan = loanManager.getLoan(loanID);
                            if (loan != null) {
                                if(loan.getOriginalRequest().getBorrowerName().equals(username)) {
                                    loan.executeRiskPayment(paymentTotal);
                                    resp.setStatus(HttpServletResponse.SC_OK);
                                }
                                else resp.setStatus(HttpServletResponse.SC_CONFLICT);
                            }
                            else resp.setStatus(HttpServletResponse.SC_CONFLICT);
                        }

                    }
                    else resp.setStatus(HttpServletResponse.SC_CONFLICT);
                } catch (NumberFormatException numberFormatException) {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            }
            else resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
