/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.msteams.connector.service;

import static com.salesforce.einsteinbot.sdk.client.util.RequestFactory.buildTextMessage;
import static com.salesforce.einsteinbot.sdk.util.UtilFunctions.convertObjectToJson;
import static com.salesforce.einsteinbot.msteams.connector.utils.UtilFunctions.buildBotRequest;
import static com.salesforce.einsteinbot.msteams.connector.utils.UtilFunctions.getTextMessageFromResponseObject;

import com.salesforce.einsteinbot.sdk.client.SessionManagedChatbotClient;
import com.salesforce.einsteinbot.sdk.client.model.BotResponse;
import com.salesforce.einsteinbot.sdk.client.model.BotSendMessageRequest;
import com.salesforce.einsteinbot.sdk.client.model.ExternalSessionId;
import com.salesforce.einsteinbot.sdk.client.model.RequestConfig;
import com.salesforce.einsteinbot.sdk.client.util.RequestEnvelopeInterceptor;
import com.salesforce.einsteinbot.sdk.model.AnyRequestMessage;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
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
  @Value("${sfdc.einstein.bots.orgId:}")
  private String orgId;
  @Value("${sfdc.einstein.bots.botId:}")
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
    AtomicReference<Object> requestData = new AtomicReference<>();
    RequestEnvelopeInterceptor requestEnvelopeInterceptor =
        (requestEnvelope) -> requestData.set(requestEnvelope);
    try {
      BotSendMessageRequest botRequest = buildBotRequest(message, requestEnvelopeInterceptor);
      BotResponse botResponse = chatbotClient.sendMessage(requestConfig,
          externalSessionId,
          botRequest);
      Object jsonResponse = convertObjectToJson(botResponse);
      response = getTextMessageFromResponseObject(botResponse.getResponseEnvelope());
    } catch (Exception e) {
      logger.error("Exception occurred ", e);
    }
    return response;
  }

  private String getOrCreateSessionId(String sessionId) {
    sessionId = (sessionId == null || sessionId.trim().isEmpty()) ? UUID.randomUUID().toString()
        : sessionId;
    return sessionId;
  }

}
