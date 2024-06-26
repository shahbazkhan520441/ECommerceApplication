package com.jsp.ecommerce.mailservice;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.jsp.ecommerce.utility.MessageData;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailService {
//	service provider
	
	
private final JavaMailSender javaMailSender;
@Async
public void sendMail(MessageData MessageData) throws MessagingException {
	
	MimeMessage  message=javaMailSender.createMimeMessage();//
	
	MimeMessageHelper helper=new MimeMessageHelper(message,  true);
	helper.setFrom("shahbazkhan520441@gmail.com");
	helper.setTo(MessageData.getTo());
	helper.setSubject(MessageData.getSubject());
	helper.setSentDate(MessageData.getSentDate());
	helper.setText(MessageData.getText(),true); //html:true
	
	javaMailSender.send(message);

	
}
}
