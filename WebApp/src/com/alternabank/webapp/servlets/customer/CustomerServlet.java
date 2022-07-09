package com.alternabank.webapp.servlets.customer;

import com.alternabank.dto.customer.CustomerDetails;
import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.webapp.util.WebAppUtils;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CustomerServlet", urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        if (WebAppUtils.isAdmin(req)) {
            //ADMIN
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        else {
            String username = WebAppUtils.getUsername(req);
            if (username != null) {
                CustomerManager customerManager = WebAppUtils.getCustomerManager(getServletContext());
                CustomerDetails customerDetails;
                synchronized (getServletContext()) {
                    customerDetails = customerManager.getCustomersByName().get(username).toDTO();
                }
                String customerDetailsJSON = WebAppUtils.GSON_INSTANCE.toJson(customerDetails);
                resp.getWriter().print(customerDetailsJSON);
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
}
