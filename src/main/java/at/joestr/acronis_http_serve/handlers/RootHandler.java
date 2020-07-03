/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.acronis_http_serve.handlers;

import at.joestr.acronis_http_serve.Main;
import at.joestr.acronis_http_serve.classes.AcronisFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class RootHandler extends AbstractHandler {

  final String greeting;
  final String body;

  public RootHandler() {
    this("Hello World");
  }

  public RootHandler(String greeting) {
    this(greeting, null);
  }

  public RootHandler(String greeting, String body) {
    this.greeting = greeting;
    this.body = body;
  }

  @Override
  public void handle(String target,
    Request baseRequest,
    HttpServletRequest request,
    HttpServletResponse response) throws IOException, ServletException {

    String path = request.getPathInfo().substring(1, request.getPathInfo().length());
    String[] splittedPath = path.split("/");
    
    if (splittedPath.length != 2) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.setContentType("text/plain; charset=utf-8");
      PrintWriter out = response.getWriter();
      out.println("404 - Not found");
      baseRequest.setHandled(true);
      return;
    }
    
		if (!splittedPath[1].matches("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[34][0-9a-fA-F]{3}-[89ab][0-9a-fA-F]{3}-[0-9a-fA-F]{12}")) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.setContentType("text/plain; charset=utf-8");
      PrintWriter out = response.getWriter();
      out.println("404 - Not found");
      baseRequest.setHandled(true);
      return;
    }
		
    boolean isRoot = false;
    
		if (splittedPath[1].equalsIgnoreCase(new UUID(0L, 0L).toString())) {
			isRoot = true;
		}
    
    if (splittedPath[0].equalsIgnoreCase("contents")) {
      HttpRequest httpRequest = null;
      HttpRequest httpRequest2 = null;
      try {
        httpRequest = HttpRequest.newBuilder()
          .uri(URI.create(
            Main.getInstance().getConfig().getProperty("arconis.serverurl") +
              "/fc/api/v1/sync_and_share_nodes/"
              + (isRoot ? "" : splittedPath[1] + "/")
              + "contents?fields=uuid,name,is_directory,size,file_modification_date,path,parent_uuid,checksum&filter_deleted=active&sort_by=name&sort_direction=asc"
          ))
          .header("Authorization", "Bearer " + Main.getInstance().getCredentials().getValidAccessToken())
          .GET()
          .build();
        if (!isRoot) {
          httpRequest2 = HttpRequest.newBuilder()
            .uri(URI.create(
              Main.getInstance().getConfig().getProperty("arconis.serverurl") +
                "/fc/api/v1/sync_and_share_nodes/"
                + splittedPath[1]
            ))
            .header("Authorization", "Bearer " + Main.getInstance().getCredentials().getValidAccessToken())
            .GET()
            .build();
        }
      } catch (InterruptedException ex) {
        Logger.getLogger(RootHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
      HttpResponse<String> httpResponse = null;
      HttpResponse<String> httpResponse2 = null;
      try {
        httpResponse = Main.getInstance().getClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (!isRoot) {
          httpResponse2 = Main.getInstance().getClient().send(httpRequest2, HttpResponse.BodyHandlers.ofString());
        }
      } catch (InterruptedException ex) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println("500 - Internal server error");
        baseRequest.setHandled(true);
        return;
      }
      
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      response.setContentType("text/html; charset=utf-8");
      PrintWriter out = response.getWriter();
      out.println("<!doctype html><head><title>contents/" + splittedPath[1] + "</title></head>");
      out.println("<body><table><thead><th>name</th><th>date</th><th>size</th></thead><tbody>");
      
      if (!isRoot) {
        AcronisFile file = new Gson().fromJson(httpResponse2.body(), AcronisFile.class);
        out.println("<tr><td><a href=\""+file.getParent_uuid()+"\">..</a></td>");
        out.println("<td>-</td>");
        out.println("<td>-</td></tr>");
      }
      
      Type listType = new TypeToken<List<AcronisFile>>(){}.getType();
      ArrayList<AcronisFile> files = new Gson().fromJson(httpResponse.body(), listType);
      
      files.forEach((f) -> {
        if (f.isIs_directory()) {
          out.println("<tr><td><a href=\""+f.getUuid()+"\">"+f.getName()+"</a></td>");
          out.println("<td>"+f.getFile_modification_date()+"</td>");
          out.println("<td>-</td></tr>");
        } else {
          out.println("<tr><td><a href=\"../../download/"+f.getUuid()+"\">"+f.getName()+"</a></td>");
          out.println("<td>"+f.getFile_modification_date()+"</td>");
          out.println("<td>"+f.getSize()+"</td></tr>");
        }
      });
      
      out.println("</tbody></table></body>");
      baseRequest.setHandled(true);
      return;
    }
    
    if (splittedPath[0].equalsIgnoreCase("download")) {
      HttpClient client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(20))
        .build();
      HttpRequest httpRequest = null;
      try {
        httpRequest = HttpRequest.newBuilder()
          .uri(URI.create(
            Main.getInstance().getConfig().getProperty("arconis.serverurl") +
              "/fc/api/v1/sync_and_share_nodes/"
              + splittedPath[1]
              + "/download"
          ))
          .header("Authorization", "Bearer " + Main.getInstance().getCredentials().getValidAccessToken())
          .GET()
          .build();
      } catch (InterruptedException ex) {
        Logger.getLogger(RootHandler.class.getName()).log(Level.SEVERE, null, ex);
      }
      HttpResponse<InputStream> httpResponse;
      try {
        httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
      } catch (InterruptedException ex) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.println("500 - Internal server error");
        baseRequest.setHandled(true);
        return;
      }
      
      Optional<String> contentDipositionHeader =
        httpResponse.headers().firstValue("content-disposition");
      response.setStatus(HttpServletResponse.SC_OK);
      if (contentDipositionHeader.isPresent()) {
        response.addHeader("content-disposition", contentDipositionHeader.get());
      }
      response.setContentType("application/octet-stream");
      ServletOutputStream out = response.getOutputStream();
      
      httpResponse.body().transferTo(out);
      out.flush();
      
      baseRequest.setHandled(true);
      return;
    }
    
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    response.setContentType("text/plain; charset=utf-8");
    PrintWriter out = response.getWriter();
    out.println("404 - Not found");
    baseRequest.setHandled(true);
  }
}
