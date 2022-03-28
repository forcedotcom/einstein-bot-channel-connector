/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.framework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.salesforce.einsteinbot.sdk.client.ChatbotClient;
import com.salesforce.einsteinbot.sdk.model.Status;
import com.salesforce.einsteinbot.sdk.model.Status.StatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;

@ExtendWith(MockitoExtension.class)
public class BotsHealthIndicatorTest {

  @Mock
  private ChatbotClient chatbotClient;

  private BotsHealthIndicator botsHealthIndicator = new BotsHealthIndicator();

  @BeforeEach
  public void setup(){
    botsHealthIndicator = new BotsHealthIndicator();
    botsHealthIndicator.setChatbotClient(chatbotClient);
   }

  @Test
  public void testChatbotsStatusDown() {

    when(chatbotClient.getHealthStatus())
        .thenReturn(new Status()
            .status(StatusEnum.DOWN));

    assertEquals(Health.down().build(), botsHealthIndicator.health());
  }

  @Test
  public void testChatbotsStatusUp() {
    when(chatbotClient.getHealthStatus())
        .thenReturn(new Status()
            .status(StatusEnum.UP));;

    assertEquals(Health.up().build(), botsHealthIndicator.health());
  }

}
