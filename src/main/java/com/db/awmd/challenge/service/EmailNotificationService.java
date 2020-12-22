package com.db.awmd.challenge.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("notificationService")
public class EmailNotificationService implements NotificationService {
	
  @Autowired
  JavaMailSender mailSender;
  
  @Autowired
  public EmailNotificationService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  @Async
  public void notifyAboutTransfer(Account account, String transferDescription) {
    //THIS METHOD SHOULD NOT BE CHANGED - ASSUME YOUR COLLEAGUE WILL IMPLEMENT IT
    log.info("Sending notification to owner of {}: {}", account.getAccountId(), transferDescription);
    
    try {
    	
    	MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper transferMail = new MimeMessageHelper(message);
        
        try {
        	transferMail.setTo("bupurashok@gmail.com");
        	transferMail.setText(transferDescription);
        	transferMail.setSubject("Account update confirmation");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Exception while sending email.");
        }

		/*
		 * SimpleMailMessage transferMail = new SimpleMailMessage();
		 * transferMail.setTo("bupuranu@gmail.com");
		 * transferMail.setSubject("Account update confirmation");
		 * transferMail.setText(transferDescription);
		 * transferMail.setFrom("bupurashok@gmail.com");
		 */
	    
	    mailSender.send(message);
    } catch(Exception ex) {
    	ex.printStackTrace();
    	System.out.println("Exception while sending email.");
    }
    
  }  
  
}