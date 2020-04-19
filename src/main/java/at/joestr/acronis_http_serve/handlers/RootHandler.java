/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.acronis_http_serve.handlers;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class RootHandler extends AbstractHandler
{
    final String greeting;
    final String body;

    public RootHandler()
    {
        this("Hello World");
    }

    public RootHandler(String greeting)
    {
        this(greeting, null);
    }

    public RootHandler(String greeting, String body)
    {
        this.greeting = greeting;
        this.body = body;
    }

    @Override
    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response) throws IOException,
        ServletException
    {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();

        out.println("<h1>" + greeting + "</h1>");
        if (body != null)
        {
            out.println(body);
        }

        baseRequest.setHandled(true);
    }
}
