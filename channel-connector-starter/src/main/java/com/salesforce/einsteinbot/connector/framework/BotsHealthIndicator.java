/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.framework;

import com.google.common.annotations.VisibleForTesting;
import com.salesforce.einsteinbot.sdk.client.BasicChatbotClient;
import com.salesforce.einsteinbot.sdk.client.ChatbotClient;
import com.salesforce.einsteinbot.sdk.model.Status;
import com.salesforce.einsteinbot.sdk.model.Status.StatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BotsHealthIndicator implements HealthIndicator {

  @Autowired
  private BasicChatbotClient chatbotClient;

  @Override
  public Health health() {
    Status healthStatus = chatbotClient.getHealthStatus();

    StatusEnum status = healthStatus.getStatus();
    switch (status) {
      case DOWN:
        return Health.down().build();
      case UP:
        return Health.up().build();
      default:
        return Health.unknown().build();
    }
  }

  @VisibleForTesting
  void setChatbotClient(BasicChatbotClient chatbotClient) {
    this.chatbotClient = chatbotClient;
  }
}