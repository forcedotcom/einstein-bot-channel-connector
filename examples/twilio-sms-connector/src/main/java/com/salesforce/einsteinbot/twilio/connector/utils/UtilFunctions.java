/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.twilio.connector.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.salesforce.einsteinbot.sdk.client.model.BotEndSessionRequest;
import com.salesforce.einsteinbot.sdk.client.model.BotRequest;
import com.salesforce.einsteinbot.sdk.client.model.BotSendMessageRequest;
import com.salesforce.einsteinbot.sdk.client.util.RequestEnvelopeInterceptor;
import com.salesforce.einsteinbot.sdk.model.AnyRequestMessage;
import com.salesforce.einsteinbot.sdk.model.AnyResponseMessage;
import com.salesforce.einsteinbot.sdk.model.ChoicesResponseMessage;
import com.salesforce.einsteinbot.sdk.model.ChoicesResponseMessageChoices;
import com.salesforce.einsteinbot.sdk.model.EndSessionReason;
import com.salesforce.einsteinbot.sdk.model.EscalateResponseMessage;
import com.salesforce.einsteinbot.sdk.model.ResponseEnvelope;
import com.salesforce.einsteinbot.sdk.model.SessionEndedResponseMessage;
import com.salesforce.einsteinbot.sdk.model.TextResponseMessage;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * UtilFunctions - Contains static utility functions.
 */
public class UtilFunctions {

  public static final String NEW_LINE = "\n";

  public static ErrorResult convertStackTraceToErrorResponse(Exception e) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    String[] stackTrace = sw.toString().replaceAll("\t", "").split("\n");
    String errorMessage = e.getMessage();
    return new ErrorResult(errorMessage, stackTrace);
  }

  public static BotSendMessageRequest buildBotRequest(AnyRequestMessage message,
      RequestEnvelopeInterceptor requestEnvelopeInterceptor) {
    return BotRequest.withMessage(message)
        .requestEnvelopeInterceptor(requestEnvelopeInterceptor)
        .build();
  }

  public static BotEndSessionRequest buildBotRequest(EndSessionReason endSessionReason,
      RequestEnvelopeInterceptor requestEnvelopeInterceptor) {
    return BotRequest
        .withEndSession(endSessionReason)
        .requestEnvelopeInterceptor(requestEnvelopeInterceptor)
        .build();
  }


  public static String getTextMessageFromResponseObject(ResponseEnvelope responseEnvelope) {

    String response = "Hello World";
    StringBuilder builder = new StringBuilder();
    for (AnyResponseMessage message : responseEnvelope.getMessages()) {
      if (message instanceof TextResponseMessage) {
        TextResponseMessage textResponseMessage = (TextResponseMessage) message;
        builder.append(textResponseMessage.getText()).append(NEW_LINE);
      }
      if (message instanceof ChoicesResponseMessage) {
        // TODO: Explore how to send choices in a tweet other than just text
        ChoicesResponseMessage choicesResponseMessage = (ChoicesResponseMessage) message;
        for (ChoicesResponseMessageChoices choice : choicesResponseMessage.getChoices()) {
          builder.append(choice.getAlias()).append(": ").append(choice.getLabel()).append(NEW_LINE);
        }
      }
      if (message instanceof SessionEndedResponseMessage) {
        builder.append("Goodbye!");
      }
      if (message instanceof EscalateResponseMessage) {
        builder.append("Sorry, I cannot help you but I will ask someone to get back to you.");
      }
    }
    return builder.toString();
  }

  public static Object convertObjectToJson(Object data) {
    try {
      return com.salesforce.einsteinbot.sdk.util.UtilFunctions.convertObjectToJson(data);
    } catch (JsonProcessingException e) {
      return convertStackTraceToErrorResponse(e);
    }
  }
}

class ErrorResult {

  String errorMessage;
  String[] stackTrace;

  public ErrorResult(String errorMessage, String[] stackTrace) {
    this.errorMessage = errorMessage;
    this.stackTrace = stackTrace;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String[] getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(String[] stackTrace) {
    this.stackTrace = stackTrace;
  }
}