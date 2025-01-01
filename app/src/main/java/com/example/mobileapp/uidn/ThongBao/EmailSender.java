package com.example.mobileapp.uidn.ThongBao;

import android.util.Log;

import java.util.Properties;
import java.util.concurrent.Executors;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {

    public static void sendEmail(String recipientEmail, String subject, String content) {
//        final String senderEmail = "zabbixserver.gr14@gmail.com";
//        final String senderPassword = "skaahusadfxzycic";
//
//        Properties props = new Properties();
//        props.put("mail.smtp.auth", "true");
//        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.smtp.host", "smtp.gmail.com");
//        props.put("mail.smtp.port", "587");
//
//        Session session = Session.getInstance(props, new Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(senderEmail, senderPassword);
//            }
//        });
//
//        try {
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(senderEmail));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
//            message.setSubject(subject);
//            message.setText(content);
//
//            Transport.send(message);
//            Log.d("EmailSender", "Email sent successfully to " + recipientEmail);
//        } catch (MessagingException e) {
//            Log.e("EmailSender", "Failed to send email", e);
//        }

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Thiết lập thông tin email
                Properties props = new Properties();
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("zabbixserver.gr14@gmail.com", "skaahusadfxzycic");
                    }
                });

                // Tạo email
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("zabbixserver.gr14@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject(subject);
                message.setText(content);

                // Gửi email
                Transport.send(message);

                Log.d("EmailSender", "Email sent successfully");
            } catch (Exception e) {
                Log.e("EmailSender", "Failed to send email", e);
            }
        });
    }
    }

