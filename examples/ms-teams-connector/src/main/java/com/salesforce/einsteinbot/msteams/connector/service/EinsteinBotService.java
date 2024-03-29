/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.msteams.connector.service;

import static com.salesforce.einsteinbot.msteams.connector.utils.UtilFunctions.getTextMessageFromResponseObject;
import static com.salesforce.einsteinbot.sdk.client.util.RequestFactory.buildBotSendMessageRequest;
import static com.salesforce.einsteinbot.sdk.client.util.RequestFactory.buildTextMessage;
import static com.salesforce.einsteinbot.sdk.util.UtilFunctions.convertObjectToJson;

import com.salesforce.einsteinbot.sdk.client.SessionManagedChatbotClient;
import com.salesforce.einsteinbot.sdk.client.model.BotResponse;
import com.salesforce.einsteinbot.sdk.client.model.BotSendMessageRequest;
import com.salesforce.einsteinbot.sdk.client.model.ExternalSessionId;
import com.salesforce.einsteinbot.sdk.client.model.RequestConfig;
import com.salesforce.einsteinbot.sdk.model.AnyRequestMessage;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service to communicate with the Einstein Chatbots
 */
@Service
public class EinsteinBotService {

  private static final Logger logger = LoggerFactory.getLogger(EinsteinBotService.class);
  @Autowired
  private SessionManagedChatbotClient chatbotClient;
  @Value("${sfdc.einstein.bots.force-config-endpoint}")
  private String forceConfigEndpoint;
  @Value("${sfdc.einstein.bots.orgId}")
  private String orgId;
  @Value("${sfdc.einstein.bots.botId}")
  private String botId;
  private RequestConfig requestConfig;

  @PostConstruct
  private void setup() {
    requestConfig = RequestConfig
        .with()
        .botId(botId)
        .orgId(orgId)
        .forceConfigEndpoint(forceConfigEndpoint)
        .build();
    logger.info("EinsteinBotService setup complete");
  }

  public String sendMessageSync(String teamsSessionId, String botMessage) {
    String response = "";
    String sessionId = getOrCreateSessionId(teamsSessionId);
    ExternalSessionId externalSessionId = new ExternalSessionId(sessionId);
    // create request
    AnyRequestMessage message = buildTextMessage(botMessage);
    try {
      Optional<String> requestId = Optional.of(UUID.randomUUID().toString());
      BotSendMessageRequest botRequest= buildBotSendMessageRequest(message,requestId);
      BotResponse botResponse = chatbotClient.sendMessage(requestConfig,
          externalSessionId,
          botRequest);
      Object jsonResponse = convertObjectToJson(botResponse);
      response = getTextMessageFromResponseObject(botResponse.getResponseEnvelope());
    } catch (Exception e) {
      response = "Sorry for the inconvenience. An error has been encountered while connecting to the bot. A log of the error has been saved.";
      logger.error("Exception occurred in sending botRequest: ", e);
    }
    return response;
  }

  private String getOrCreateSessionId(String sessionId) {
    sessionId = (sessionId == null || sessionId.trim().isEmpty()) ? UUID.randomUUID().toString()
        : sessionId;
    return sessionId;
  }

}
