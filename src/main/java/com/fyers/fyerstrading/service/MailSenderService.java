package com.fyers.fyerstrading.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailSenderService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendEmail(String to, String subject, String body) throws MessagingException {
		sendEmailWithAttachment(to, subject, body, null);
	}

	public void sendEmailWithAttachment(String to, String subject, String body, File attachment)
			throws MessagingException {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body);

		if (attachment != null && attachment.exists()) {
			FileSystemResource fileResource = new FileSystemResource(attachment);
			helper.addAttachment(attachment.getName(), fileResource);
		}

		mailSender.send(message);
		System.out.println("Email sent to: " + to + " | Subject: " + subject);
	}
}
