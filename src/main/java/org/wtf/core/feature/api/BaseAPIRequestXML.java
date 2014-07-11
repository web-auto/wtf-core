/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.api;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;


/**
 * Base API request XML class.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class BaseAPIRequestXML {

  public static String loadXMLToString(String filePath) {
    String xmlFilePath = BaseAPIRequestXML.class.getResource(filePath).getFile();
    StringBuffer fileData = new StringBuffer(1000);
    BufferedReader reader = null;

    xmlFilePath = URLDecoder.decode(xmlFilePath); // encode symbols has to be decoded.

    try {
      reader = new BufferedReader(new FileReader(xmlFilePath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    char[] buf = new char[1024];
    int numRead = 0;
    try {
      while((numRead=reader.read(buf)) != -1){
        String readData = String.valueOf(buf, 0, numRead);
        fileData.append(readData);
        buf = new char[1024];
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return fileData.toString();
  }
}
