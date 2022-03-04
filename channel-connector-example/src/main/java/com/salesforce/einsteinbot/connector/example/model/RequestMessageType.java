/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.example.model;

/**
 * RequestMessageType : Enum to represent different Request Message Types
 *
 * @author relango
 */
public enum RequestMessageType {
  MESSAGE("Message"),
  CHOICE_ID("Choice Id"),
  CHOICE_INDEX("Choice Index"),
  TRANSFER_SUCCESS("Transfer Success"),
  TRANSFER_FAILURE("Transfer Failure"),
  END_SESSION("End Session");

  private String displayName;

  RequestMessageType(String displayName) {
    this.displayName = displayName;
  }

  public String getDisplayName() {
    return displayName;
  }

  public static RequestMessageType getDefault() {
    return MESSAGE;
  }
}
