/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.example.model;

/**
 * ErrorResult - Class to hold Error Response received from Runtime API in a JSON Serializable
 * format.
 */
public class ErrorResult {

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
