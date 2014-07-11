/**
 * Copyright (C) 2014 WTF org.
 */

package org.wtf.core.feature.emailer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Emailer module for sending e-mail using java mail API.
 * 
 * @author venkatesan.sundramurthy@gmail.com (Venkatesan Sundramurthy)
 */
public class Emailer {

  public static List <String> getAddress(String address) {
    List <String> addressList = new ArrayList<String>();
    for (String addressSpace : address.split(" ")) {
      for (String addressSemiColon : addressSpace.split(";")) {
        for (String addressComma : addressSemiColon.split(",")) {
          if (addressComma.length() > 3) {
            if (!addressComma.contains("@")) {
              addressComma += "@ebay.com";
            }
            addressList.add(addressComma);
          }
        }
      }
    }
    return addressList;
  }

  public static void sendEmails(String host, String from, String to, String cc,
      String subject, String body) {
    Properties properties = System.getProperties();
    properties.setProperty("mail.smtp.host", host);
    //properties.setProperty("mail.smtp.host", "mailhost.qa.ebay.com");
    properties.setProperty("mail.smtp.timeout", "10000");

    // Get the default Session object.
    Session session = Session.getDefaultInstance(properties, null);

    try {
      MimeMessage message = new MimeMessage(session);

      
      message.setFrom(new InternetAddress(getAddress(from).get(0)));

      for (String address : getAddress(to)) {
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(address));
      }

      for (String address : getAddress(cc)) {
        message.addRecipient(Message.RecipientType.CC, new InternetAddress(address));
      }

      message.setSubject(subject);
      message.setContent(body, "text/html" );
      try {
        Transport.send(message);
      } catch (Exception e ) {
        //System.out.println("Failed to send email using QA SMTP server, trying corp SMTP..");
        properties.setProperty("mail.smtp.host", "atom.corp.ebay.com");
        Transport.send(message);
      }
      //System.out.println("Sent message successfully....");
   }catch (MessagingException mex) {
      mex.printStackTrace();
   }
  }
}
