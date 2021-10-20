package com.smtp.mailsenderservice;

import com.sparkpost.exception.SparkPostException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MailSenderServiceApplication implements CommandLineRunner {

	@Autowired
	private SendEmailSample sendEmailSample;

	public static void main(String[] args) throws SparkPostException, IOException, Exception {
		SpringApplication.run(MailSenderServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Sending Email...");
		sendEmailSample.runApp();
		System.out.println("Done");
	}
}
