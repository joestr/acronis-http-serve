/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.acronis_http_serve.classes;

import at.joestr.acronis_http_serve.Main;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author Joel
 */
public class AcronisCredentials {
  
  private String username;
  private String password;
  private String accessToken;
  private String idToken;
  private LocalDateTime expiry;

  public AcronisCredentials(String username, String password) throws IOException, InterruptedException {
    this.username = username;
    this.password = password;
  }

  public String getValidAccessToken() throws IOException, InterruptedException {
    
    if (this.expiry == null) {
      this.obtainTokens();
    }
    
    if (this.expiry.isBefore(LocalDateTime.now())) {
      this.obtainTokens();
    }
    
    return this.accessToken;
  }
  
  private void obtainTokens() throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create(Main.getInstance().getConfig().getProperty("arconis.serverurl")+"/api/2/idp/token"))
      .header("accept", "application/json")
      .header("content-type", "application/x-www-form-urlencoded")
      .POST(BodyPublishers.ofString(
        "grant_type=password&"
        + "username=" + this.username + "&"
        + "password=" + this.password
      ))
      .build();
    HttpResponse<String> response = Main.getInstance().getClient().send(request, BodyHandlers.ofString());
    
    if (response.statusCode() == 401) {
      throw new RuntimeException("Missing or invalid token");
    }
    
    JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();
    
    this.accessToken = root.get("access_token").getAsString();
    this.expiry = LocalDateTime.ofEpochSecond( root.get("expires_on").getAsLong(), 0, ZoneOffset.UTC);
    this.idToken = root.get("id_token").getAsString();
    //root.get("token_type").getAsString();
  }
}
