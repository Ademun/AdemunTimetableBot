package org.ademun.timetablebot.helpers;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpHelper {
  private static final HttpClient client = HttpClient.newHttpClient();

  public static String executeRequest(HttpRequest request) {
    String response;
    try {
      HttpResponse<String> httpResponse =
          client.send(request, HttpResponse.BodyHandlers.ofString());
      response = httpResponse.body();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
    return response;
  }
}
