/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.acronis_http_serve;

import at.joestr.acronis_http_serve.classes.AcronisCredentials;
import at.joestr.acronis_http_serve.handlers.RootHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.Properties;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;

/**
 *
 * @author Joel
 */
public class Main {

  private static final Main INSTANCE = new Main();
  private final Properties config = new Properties();
  private AcronisCredentials credentials;
  private HttpClient client;
  
  public static void main(String[] args) throws Exception {
    
    File selfJar = new File(
      INSTANCE.getClass()
        .getProtectionDomain()
        .getCodeSource()
        .getLocation()
        .getPath()
    );
    File configFile = new File(selfJar.getParentFile(), "config.properties");
    
    if (!configFile.exists()) {
      InputStream iS = INSTANCE.getClass().getResourceAsStream("config.properties");
      Files.copy(iS, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
    if (!configFile.canRead()) {
      return;
    }
    INSTANCE.config.load(new FileInputStream(configFile));
    
    INSTANCE.credentials = new AcronisCredentials(
      INSTANCE.config.getProperty("arconis.username"),
      INSTANCE.config.getProperty("arconis.password")
    );
    
    INSTANCE.client = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(5))
      .build();
    
    Server server = new Server(
      Integer.parseInt(
        INSTANCE.config.getProperty("jetty.port")
      )
    );
    
    ContextHandler contextHandler = new ContextHandler();
    contextHandler.setContextPath("/content");
    contextHandler.setHandler(new RootHandler());
    
    server.setHandler(contextHandler);
    
    server.start();
    server.join();
  }
  
  public static Main getInstance() {
    return INSTANCE;
  }
  
  public Properties getConfig() {
    return this.config;
  }

  public AcronisCredentials getCredentials() {
    return credentials;
  }
  
  public HttpClient getClient() {
    return client;
  }
}
