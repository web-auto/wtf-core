/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;


/**
 * Base Http client.
 *
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class BaseHttpClient {
  public static enum HttpMethods {
    OPTIONS,
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    CONNECT
  }

  private String serverURL;
  private URL url;
  private HttpURLConnection httpConnection;

  public BaseHttpClient(String serverURL) {
    this.serverURL = serverURL;
  }

  private void Init() throws IOException {
    url = new URL(this.serverURL);
    URLConnection connection = url.openConnection();
    httpConnection = (HttpURLConnection) connection;

    httpConnection.setDoOutput(true);
    httpConnection.setDoInput(true);
    httpConnection.setRequestMethod(HttpMethods.POST.toString());
  }

  private void addHeaders(Map<String,String> properties) {
    Iterator<Map.Entry<String, String>> iterator = properties.entrySet().iterator();
    while(iterator.hasNext()) {
      Map.Entry<String, String> entry = iterator.next();
      httpConnection.setRequestProperty(entry.getKey(), entry.getValue());
    }
  }

  /*
   * To convert the InputStream to String we use the
   * Reader.read(char[] buffer) method. We iterate until the
   * Reader return -1 which means there's no more data to
   * read. We use the StringWriter class to produce the string.
   */
  public String convertStreamToString(InputStream inputStream) throws IOException {
    if (inputStream != null) {
      Writer writer = new StringWriter();
      char[] buffer = new char[1024];

      try {
        Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        int n;
        while ((n = reader.read(buffer)) != -1) {
          writer.write(buffer, 0, n);
        }
      } finally {
        inputStream.close();
      }
      return writer.toString();
    } else {
      return "";
    }
  }

  public String request(Map<String, String> headers, String requestXML) throws IOException {
    Init();

    addHeaders(headers);

    OutputStream out = httpConnection.getOutputStream();
    Writer wout = new OutputStreamWriter(out);

    wout.write(requestXML);
    wout.flush();
    wout.close();

    String responseXML = convertStreamToString(httpConnection.getInputStream());
    return responseXML.replace("\"", "'");
  }
  
  public String getServerURL() {
    return serverURL;
  }
}
