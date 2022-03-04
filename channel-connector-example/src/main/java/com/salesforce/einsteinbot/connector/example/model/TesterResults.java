/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.example.model;

import static com.salesforce.einsteinbot.connector.example.util.UtilFunctions.convertObjectToJson;

/**
 * TesterResults : POJO to hold result of Chatbot Admin Tester.
 *
 * @author relango
 */
public class TesterResults {

  private enum Status {
    SUCCESS, ERROR;
  }

  private final String sessionId;
  private final Status status;
  private final Object request;
  private final Object response;

  private TesterResults(String sessionId, Status status, Object request, Object response) {
    this.sessionId = sessionId;
    this.status = status;
    this.request = convertObjectToJson(request);
    this.response = convertObjectToJson(response);
  }

  public static TesterResults forSuccess(String sessionId, Object request, Object response) {
    return new TesterResults(sessionId, Status.SUCCESS, request, response);
  }

  public static TesterResults forError(String sessionId, Object request, Object response) {
    return new TesterResults(sessionId, Status.ERROR, request, response);
  }

  public String getSessionId() {
    return sessionId;
  }

  public String getStatus() {
    return status.name();
  }

  public Object getResponse() {
    return response;
  }

  public Object getRequest() {
    return request;
  }
}
