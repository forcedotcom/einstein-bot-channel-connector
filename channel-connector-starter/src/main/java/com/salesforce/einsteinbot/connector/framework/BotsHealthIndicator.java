/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.framework;

import com.google.common.annotations.VisibleForTesting;
import com.salesforce.einsteinbot.sdk.client.ChatbotClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class BotsHealthIndicator implements HealthIndicator {

  @Autowired
  private ChatbotClient chatbotClient;

  @Override
  public Health health() {
    // TODO: Fix After W-10712366
   /* Status healthStatus = chatbotClient.getHealthStatus();

    StatusEnum status = healthStatus.getStatus();
    switch (status) {
      case RED:
        return Health.down().build();
      case GREEN:
        return Health.up().build();
      default:
        return Health.unknown().build();
    }*/
    return Health.up().build();
  }

  @VisibleForTesting
  void setChatbotClient(ChatbotClient chatbotClient) {
    this.chatbotClient = chatbotClient;
  }
}