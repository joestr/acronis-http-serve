/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.joestr.acronis_http_serve.classes;

import at.joestr.acronis_http_serve.Main;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Holds the necesarry credential information from the "Acronis File Sync & Share" identity
 * endpoint.
 * 
 * @author joestr
 */
public class AcronisCredentials {
  
  private final String username;
  private final String password;
  private String accessToken;
  private String idToken;
  private long expiry = 0;

  public AcronisCredentials(String username, String password) throws IOException, InterruptedException {
    this.username = username;
    this.password = password;
  }

  /**
   * This method returns a valid access token to interact with the "Acronis File Sync & Share" REST
   * API.
   * 
   * @return An access token for usage with the REST API.
   * @throws IOException If an  I/O error occurs during the proccess of obtaining an access token.
   * @throws InterruptedException If the request/response thread gets interrupted.
   */
  public String getValidAccessToken() throws IOException, InterruptedException {
    
    // Get the current time in epoch seconds.
    long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
    
    if (this.expiry == 0) {
      this.obtainTokens();
    } else if (this.expiry <= now) {
      this.obtainTokens();
    }
    
    return this.accessToken;
  }
  
  /**
   * This method obtains all tokens from the authentication endpoint.
   * 
   * @throws IOException If an  I/O error occurs during the proccess of obtaining the tokens.
   * @throws InterruptedException If the request/response thread gets interrupted.
   */
  private void obtainTokens() throws IOException, InterruptedException {
    
    // Build the request 
    HttpRequest request = HttpRequest.newBuilder()
      .uri(
        URI.create(
          Main.getInstance().getConfig().getProperty("arconis.serverurl") + "/api/2/idp/token")
      )
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
    this.expiry = root.get("expires_on").getAsLong();
    this.idToken = root.get("id_token").getAsString();
    //root.get("token_type").getAsString();
  }
}
