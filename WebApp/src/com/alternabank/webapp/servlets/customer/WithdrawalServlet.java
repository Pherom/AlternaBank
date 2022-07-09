package com.alternabank.webapp.servlets.customer;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "WithdrawalServlet", urlPatterns = "/withdraw")
public class WithdrawalServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (WebAppUtils.isAdmin(req)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
        else {
            String username = WebAppUtils.getUsername(req);
            if (username != null) {
                double total;
                try {
                    total = Double.parseDouble(req.getParameter("total"));
                    CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
                    synchronized (getServletContext()) {
                        customerManager.withdrawFunds(username, total);
                        resp.setStatus(HttpServletResponse.SC_OK);
                    }
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
