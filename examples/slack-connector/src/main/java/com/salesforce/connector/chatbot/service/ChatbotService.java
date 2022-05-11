package com.salesforce.connector.chatbot.service;


import com.salesforce.einsteinbot.sdk.client.SessionManagedChatbotClient;
import com.salesforce.einsteinbot.sdk.client.model.BotRequest;
import com.salesforce.einsteinbot.sdk.client.model.BotResponse;
import com.salesforce.einsteinbot.sdk.client.model.BotSendMessageRequest;
import com.salesforce.einsteinbot.sdk.client.model.ExternalSessionId;
import com.salesforce.einsteinbot.sdk.client.model.RequestConfig;
import com.salesforce.einsteinbot.sdk.model.TextMessage;
import com.salesforce.connector.chatbot.util.Constants;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatbotService {

  @Autowired
  private SessionManagedChatbotClient sessionManagedChatbotClient;

  @Value("${sfdc.einstein.bots.orgId:}")
  private String orgId;

  @Value("${sfdc.einstein.bots.botId:}")
  private String botId;

  @Value("${sfdc.einstein.bots.force-config-endpoint}")
  private String forceConfigEndpoint;

  private static final Logger logger = LoggerFactory.getLogger(SessionIdProvider.class);

  private RequestConfig requestConfig;

  /*
  Remove slack specific text from message to bots
   */
  public String cleanText(String text){
    String response = text;

    /*
    Changes email address from <mailto:lbailey@example.com|lbailey@example.com>
    to lbailey@example.com
     */
    if (response.contains(Constants.MAIL_TO_START)){
      int start = response.indexOf(Constants.MAIL_TO_START),
          middle = response.indexOf(Constants.PIPE_SEPARATOR),
          end = response.indexOf(Constants.MAIL_TO_END);
      String replaceText = response.substring(start,end + 1);
      String replaceWith = response.substring(middle + 1, end);
      response=response.replace(replaceText,replaceWith);
    }

    return response;
  }

  @PostConstruct
  public void setup() {
    this.requestConfig = RequestConfig.with().
        botId(this.botId).
        orgId(this.orgId).
        forceConfigEndpoint(this.forceConfigEndpoint).
        build();
    logger.info("Setup the request config {}", this.requestConfig);
  }

  public BotResponse send(String message, ExternalSessionId externalSessionKey) {
    /*
     Build the message
     */
    TextMessage textMessage = new TextMessage();
    textMessage.setText(this.cleanText(message));
    textMessage.setType(TextMessage.TypeEnum.TEXT);
    textMessage.setSequenceId(System.currentTimeMillis());

    /*
    Build the request
     */
    BotSendMessageRequest botSendMessageRequest = BotRequest.withMessage(textMessage).build();

    /*
    Send the message and get a response from bots api
     */
    logger.info("Request for sessionID {}: {}", externalSessionKey, botSendMessageRequest);
    BotResponse resp = this.sessionManagedChatbotClient.sendMessage(this.requestConfig,
        externalSessionKey, botSendMessageRequest);
    logger.info("Response for sessionID {} : {}", externalSessionKey, resp);
    return resp;
  }

}
