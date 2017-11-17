package com.javapatterns.state.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.*;

public class helloServlet extends HttpServlet
{

    public helloServlet()
    {
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("hello world ! ");
        out.println("now = " + new Date());
        out.close();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.close();
    }

    public String getServletInfo()
    {
        return "ServletInfo";
    }

    public long getLastModified(HttpServletRequest request)
    {
        return -1L;
    }
}
