package com.alternabank.webapp.servlets.xml;

import com.alternabank.engine.xml.result.XMLLoadResult;
import com.alternabank.engine.xml.result.XMLLoadStatus;
import com.alternabank.webapp.util.WebAppUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.*;
import java.util.Collection;
import java.util.Scanner;

@WebServlet(name = "LoadXMLFileServlet", urlPatterns = "/load-xml")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 5 * 5)
public class LoadXMLFileServlet extends HttpServlet {

    private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException  {
        resp.setContentType("text/plain;charset=UTF-8");
        String username = WebAppUtils.getUsername(req);
        XMLLoadResult result;
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        else {
            StringBuilder fileContentBuilder = new StringBuilder();
            Collection<Part> parts = req.getParts();
            for (Part part : parts) {
                fileContentBuilder.append(new Scanner(part.getInputStream()).useDelimiter("\\Z").next());
            }
            synchronized (getServletContext()) {
                 result = WebAppUtils.getXMLFileLoader(getServletContext()).loadXML(username, new ByteArrayInputStream(fileContentBuilder.toString().getBytes()));
            }
            if (result.getStatus() == XMLLoadStatus.SUCCESS)
                resp.setStatus(HttpServletResponse.SC_OK);
            else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().print(result.getMessage());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

}
