/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.example.model;

import java.util.StringJoiner;

/**
 * TesterRequest : Model class to hold user input of Chatbot Admin Tester.
 *
 * @author relango
 */
public class TesterRequest {

  private String orgId;
  private String botId;
  private String message = "";
  private RequestMessageType messageType;
  private String sessionId;

  public String getOrgId() {
    return orgId;
  }

  public void setOrgId(String orgId) {
    this.orgId = orgId;
  }

  public String getBotId() {
    return botId;
  }

  public void setBotId(String botId) {
    this.botId = botId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }


  public RequestMessageType getMessageType() {
    return messageType;
  }

  public void setMessageType(RequestMessageType messageType) {
    this.messageType = messageType;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", TesterRequest.class.getSimpleName() + "[", "]")
        .add("orgId='" + orgId + "'")
        .add("botId='" + botId + "'")
        .add("message='" + message + "'")
        .add("messageType='" + messageType + "'")
        .add("sessionId='" + sessionId + "'")
        .toString();
  }
}
