package com.smtp.mailsenderservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.sparkpost.model.AddressAttributes;
import com.sparkpost.model.TemplateAttributes;
import com.sparkpost.model.TemplateContentAttributes;
import com.sparkpost.model.responses.Response;
import com.sparkpost.resources.ResourceTemplates;
import com.sparkpost.transport.IRestConnection;
import com.sparkpost.transport.RestConnection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.sparkpost.Client;
import com.sparkpost.exception.SparkPostException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkPostBaseApp {

    public static final String SAMPLE_TEMPLATE_NAME = "_TMP_TEMPLATE_TEST";

    public static final String SAMPLE_RECIPIENT_LIST_NAME = "_TMP_RECIPLIST_TEST";

    private static final String CONFIG_FILE = "config.properties";

    static final Logger logger = Logger.getLogger(SparkPostBaseApp.class);

    @Value("${SPARKPOST_API_KEY}")
    private String SPARKPOST_API_KEY;

    @Value("${SPARKPOST_SENDER_EMAIL}")
    private String SPARKPOST_SENDER_EMAIL;

    @Value("${SPARKPOST_BASE_URL}")
    private String SPARKPOST_BASE_URL;

    @Value("${SPARKPOST_FROM}")
    private String SPARKPOST_FROM;

    @Value("${SPARKPOST_RECIPIENTS}")
    private String SPARKPOST_RECIPIENTS;

    @Value("${SPARKPOST_CC_RECIPIENTS}")
    private String SPARKPOST_CC_RECIPIENTS;

    protected Client newConfiguredClient() throws SparkPostException, IOException {
        Client client = new Client(SPARKPOST_API_KEY);
        if (StringUtils.isEmpty(client.getAuthKey())) {
            throw new SparkPostException("SPARKPOST_API_KEY must be defined in " + CONFIG_FILE + ".");
        }
        client.setFromEmail(SPARKPOST_SENDER_EMAIL);
        if (StringUtils.isEmpty(client.getFromEmail())) {
            throw new SparkPostException("SPARKPOST_SENDER_EMAIL must be defined in " + CONFIG_FILE + ".");
        }
        return client;
    }

    protected String getProperty(String name, String defaultValue) {
        if(name.equalsIgnoreCase("SPARKPOST_RECIPIENTS")) {
            return SPARKPOST_RECIPIENTS;
        } else if(name.equalsIgnoreCase("SPARKPOST_CC_RECIPIENTS")) {
            return SPARKPOST_CC_RECIPIENTS;
        }
        return SPARKPOST_RECIPIENTS;
    }

    public String getEndPoint() {
        return SPARKPOST_BASE_URL;
    }

   public String getFromAddress() {
        String fromAddress = SPARKPOST_FROM;
        if (StringUtils.isEmpty(fromAddress)) {
            throw new IllegalStateException("This sample requires you to fill in `SPARKPOST_FROM` in config.properties.");
        }
        return fromAddress;
    }

    public String getTemplate(String name) {
        try {
            String template = FileUtils.readFileToString(new File("samples/" + name), "UTF-8");
            return template;
        } catch (IOException e) {
            System.err.println("Failed to load template file. " + e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    public final static String loadJsonFile(String name) {
        try {
            String jsonContent = FileUtils.readFileToString(new File("samples/" + name), "UTF-8");
            return jsonContent;
        } catch (IOException e) {
            System.err.println("Failed to load json file. " + e.getMessage());
            System.exit(-1);
        }
        return null;
    }

    public String[] getTestRecipients() {
        return getRecipientListProperty("SPARKPOST_RECIPIENTS");
    }

    public List<String> getTestRecipientsAsList() {
        return Arrays.asList(getTestRecipients());
    }

    public String[] getCCRecipients() {
        return getRecipientListProperty("SPARKPOST_CC_RECIPIENTS");
    }

    public String[] getBCCRecipients() {
        return getRecipientListProperty("SPARKPOST_BCC_RECIPIENTS");
    }

    public String stringArrayToCSV(String[] lst) {
        StringBuilder result = new StringBuilder();
        for (int idx = 0; idx < lst.length; ++idx) {
            result.append(lst[idx]);
            if (idx < lst.length - 1) {
                result.append(",");
            }
        }
        return result.toString();
    }

    private String[] getRecipientListProperty(String propName) {
        String recipListString = getProperty(propName, null);
        if (StringUtils.isAnyEmpty(recipListString)) {
            throw new IllegalStateException("This sample requires you to fill in `" + propName + "` in config.properties.");
        }
        String[] results = recipListString.split(",");
        return results;
    }

}