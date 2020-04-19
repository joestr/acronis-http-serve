/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.acronis_http_serve;

import at.joestr.acronis_http_serve.handlers.RootHandler;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author Joel
 */
public class Main {
    public static void main(String[] args) throws Exception
    {
        int port = 8081;
        Server server = new Server(port);
        server.setHandler(new RootHandler());
        server.start();
        server.join();
    }
}
