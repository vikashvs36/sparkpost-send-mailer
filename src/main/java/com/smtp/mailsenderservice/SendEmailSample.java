package com.smtp.mailsenderservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import com.sparkpost.model.*;
import com.sparkpost.resources.ResourceTemplates;
import org.apache.log4j.Logger;

import com.sparkpost.Client;
import com.sparkpost.exception.SparkPostException;
import com.sparkpost.model.responses.Response;
import com.sparkpost.resources.ResourceTransmissions;
import com.sparkpost.transport.IRestConnection;
import com.sparkpost.transport.RestConnection;
import org.springframework.context.annotation.Configuration;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

@Configuration
public class SendEmailSample extends SparkPostBaseApp {

    static final Logger logger = Logger.getLogger(SendEmailSample.class);

    private Client client;

    public void runApp() throws Exception {
        this.client = this.newConfiguredClient();
        String fromAddress = getFromAddress();
        String[] toRecipients = getTestRecipients();
        String[] ccRecipients = getCCRecipients();

        sendEmail(fromAddress, toRecipients, ccRecipients);
    }

    private void sendEmail(String from, String[] toRecipients, String[] ccRecipients) throws SparkPostException, MessagingException {
        TransmissionWithRecipientArray transmission = new TransmissionWithRecipientArray();

        List<RecipientAttributes> recipientArray = new ArrayList<RecipientAttributes>();
        // Primary 'To' recipients
        for (String to : toRecipients) {
            RecipientAttributes recipientAttribs = new RecipientAttributes();
            AddressAttributes addressAttribs = new AddressAttributes(to);
            recipientAttribs.setAddress(addressAttribs);
            recipientArray.add(recipientAttribs);
        }

        // Secondary 'CC' recipients with the primary recipients listed in 'To:' header
        String toHeader = stringArrayToCSV(toRecipients);
        for (String cc : ccRecipients) {
            RecipientAttributes recipientAttribs = new RecipientAttributes();
            AddressAttributes addressAttribs = new AddressAttributes(cc);
            addressAttribs.setHeaderTo(toHeader);
            recipientAttribs.setAddress(addressAttribs);
            recipientArray.add(recipientAttribs);
        }
        transmission.setRecipientArray(recipientArray);

        // Populate Substitution Data
        Map<String, Object> substitutionData = new HashMap<String, Object>();
        substitutionData.put("yourContent", "You can add substitution data too.");
        transmission.setSubstitutionData(substitutionData);

        // Populate Email Body
        String template = this.getTemplate("richContent.html");
        TemplateContentAttributes contentAttributes = createTemplate(template, from);

        /*// List the CC recipients in the CC header
        Map<String, String> headers = new HashMap<>();
        String ccHeader = stringArrayToCSV(ccRecipients);
        headers.put("CC", ccHeader);
        contentAttributes.setHeaders(headers);*/

        // Add a text attachment
        AttachmentAttributes attachment = new AttachmentAttributes();
        attachment.setName("demoAttachment.txt");
        attachment.setType("text/plain; charset=UTF-8;");
        // This is Base64 of the file contents
        attachment.setData("SGVsbG8gQ2xpZW50LAoKVGhpcyBpcyBkZW1vIG1haWwgdG8gZnJvbSBoZWFsdGhjYXJlIEFQSQ==");
        List<AttachmentAttributes> attachments = new ArrayList<>();
        attachments.add(attachment);
        contentAttributes.setAttachments(attachments);

        transmission.setContentAttributes(contentAttributes);

        // Send the Email
        IRestConnection connection = new RestConnection(this.client, getEndPoint());
        Response response = ResourceTransmissions.create(connection, 0, transmission);
        System.out.println("Transmission Response: " + response);
        logger.debug("Transmission Response: " + response);
    }

    /**
     * Demonstrates how to store an email template in SparkPost
     *
     * @throws SparkPostException
     */
    public TemplateContentAttributes createTemplate(String html, String from) throws SparkPostException {
//        TemplateAttributes template = new TemplateAttributes();
//        template.setName(name);

        // Populate Email Body
        /*TemplateContentAttributes contentAttributes = new TemplateContentAttributes();
        contentAttributes.setFrom(new AddressAttributes(from));
        contentAttributes.setSubject("☰ Your subject content here. {{yourContent}}");
        contentAttributes.setText("Your Text content here.  {{yourContent}}");
        contentAttributes.setHtml("<p>Your <b>HTML</b> content here.  {{yourContent}}</p>");*/

        TemplateContentAttributes content = new TemplateContentAttributes();

        content.setSubject("Template Demo");
//        content.setFrom(new AddressAttributes(client.getFromEmail(), "Admin", null));
        content.setFrom(new AddressAttributes(from));
        content.setHtml(html);
        content.setText("The text part of the email");
//        template.setContent(content);
        return content;
    }

    @Override
    public String toString() {
        return "client[email: " + client.getFromEmail();
    }

}