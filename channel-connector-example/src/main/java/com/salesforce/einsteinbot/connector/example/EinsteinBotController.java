/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.example;

import static com.salesforce.einsteinbot.connector.example.util.RequestMessageFactory.buildChoiceMessage;
import static com.salesforce.einsteinbot.connector.example.util.RequestMessageFactory.buildEndSessionMessage;
import static com.salesforce.einsteinbot.connector.example.util.RequestMessageFactory.buildRequestEnvelop;
import static com.salesforce.einsteinbot.connector.example.util.RequestMessageFactory.buildTextMessage;
import static com.salesforce.einsteinbot.connector.example.util.RequestMessageFactory.buildTransferFailedMessage;
import static com.salesforce.einsteinbot.connector.example.util.RequestMessageFactory.buildTransferSuccessMessage;
import static com.salesforce.einsteinbot.connector.example.util.UtilFunctions.convertStackTraceToErrorResponse;

import com.salesforce.einsteinbot.connector.example.model.RequestMessageType;
import com.salesforce.einsteinbot.connector.example.model.TesterRequest;
import com.salesforce.einsteinbot.connector.example.model.TesterResults;
import com.salesforce.einsteinbot.connector.framework.BotsHealthIndicator;
import com.salesforce.einsteinbot.sdk.client.ChatbotClient;
import com.salesforce.einsteinbot.sdk.client.RequestHeaders;
import com.salesforce.einsteinbot.sdk.model.AnyRequestMessage;
import com.salesforce.einsteinbot.sdk.model.RequestEnvelope;
import com.salesforce.einsteinbot.sdk.model.ResponseEnvelope;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * EinsteinBotController - Example controller to test various Chatbot request messages.
 *
 * TODO: This Controller not authenticated and it is provided only to easily test during development.
 * Please remove it before deploying to production.
 */
@Controller
@RequestMapping("bot")
public class EinsteinBotController {

  private static final Logger logger = LoggerFactory.getLogger(EinsteinBotController.class);

  @Autowired
  private BotsHealthIndicator health;

  @Autowired
  private ChatbotClient chatbotClient;

  @Value("${sfdc.einstein.bots.force-config-endpoint}")
  private String forceConfigEndpoint;

  @Value("${sfdc.einstein.bots.orgId:}")
  private String orgId;

  @Value("${sfdc.einstein.bots.botId:}")
  private String botId;

  private RequestHeaders requestHeaders;

  @PostConstruct
  private void initialize() {
    requestHeaders = RequestHeaders
        .builder().orgId(orgId)
        .build();
  }

  @GetMapping("/")
  public String index() {
    return "index";
  }

  @GetMapping(value = "/tester")
  public String showForm(Model model) {
    model.addAttribute("testerRequest", createDefaultRequest());
    return "bot/tester"; //view
  }

  private TesterRequest createDefaultRequest() {
    TesterRequest request = new TesterRequest();
    request.setOrgId(orgId);
    request.setBotId(botId);
    request.setMessageType(RequestMessageType.getDefault());
    return request;
  }

  @PostMapping("/runTest")
  public String runTest(Model model, @ModelAttribute("testerRequest") TesterRequest testerRequest) {
    logger.info("Running Test for Request : {} ", testerRequest);

    AnyRequestMessage message = buildRequestMessage(testerRequest);
    String sessionId = getOrCreateSessionId(testerRequest.getSessionId());

    RequestEnvelope requestEnvelop = buildRequestEnvelop(sessionId,
        testerRequest.getBotId(), forceConfigEndpoint, Arrays.asList(message));

    TesterResults results;
    try {
      ResponseEnvelope responseEnvelope = chatbotClient
          .sendChatbotRequest(requestEnvelop, requestHeaders);
      results = TesterResults.forSuccess(sessionId, requestEnvelop, responseEnvelope);
    } catch (Exception e) {
      logger.error("Exception occurred ", e);
      results = TesterResults
          .forError(sessionId, requestEnvelop, convertStackTraceToErrorResponse(e));
    }
    model.addAttribute("testerResults", results);

    return "bot/tester";
  }

  private String getOrCreateSessionId(String sessionId) {
    sessionId = (sessionId == null || sessionId.trim().isEmpty()) ? UUID.randomUUID().toString()
        : sessionId;
    return sessionId;
  }

  private AnyRequestMessage buildRequestMessage(TesterRequest testerRequest) {

    RequestMessageType messageType = testerRequest.getMessageType();

    switch (messageType) {
      case CHOICE_ID:
      case CHOICE_INDEX:
        return buildChoiceMessage(messageType, testerRequest.getMessage());
      case END_SESSION:
        return buildEndSessionMessage();
      case TRANSFER_SUCCESS:
        return buildTransferSuccessMessage();
      case TRANSFER_FAILURE:
        return buildTransferFailedMessage();
      default:
        return buildTextMessage(testerRequest.getMessage());
    }
  }

  @GetMapping("/health")
  @ResponseBody
  public Health checkHealth() {
    return health.health();
  }
}
