/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.framework;

import com.salesforce.einsteinbot.sdk.client.ChatbotClient;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BotsHealthIndicatorTest {

  @Mock
  private ChatbotClient chatbotClient;
  // TODO: Fix After W-10712366
 /* @Test
  public void testChatbotsStatusDown() {
    BotsHealthIndicator cut = new BotsHealthIndicator();
    cut.setChatbotClient(chatbotClient);

    StatusEnum red = StatusEnum.RED;
    Status status = new Status();
    status.setStatus(red);
    when(chatbotClient.getHealthStatus()).thenReturn(status);

    assertEquals(Health.down().build(), cut.health());
  }

  @Test
  public void testChatbotsStatusUp() {
    BotsHealthIndicator cut = new BotsHealthIndicator();
    cut.setChatbotClient(chatbotClient);

    StatusEnum green = StatusEnum.GREEN;
    Status status = new Status();
    status.setStatus(green);
    when(chatbotClient.getHealthStatus()).thenReturn(status);

    assertEquals(Health.up().build(), cut.health());
  }

  @Test
  public void testChatbotsStatusUnknown() {
    BotsHealthIndicator cut = new BotsHealthIndicator();
    cut.setChatbotClient(chatbotClient);

    StatusEnum yellow = StatusEnum.YELLOW;
    Status status = new Status();
    status.setStatus(yellow);
    when(chatbotClient.getHealthStatus()).thenReturn(status);

    assertEquals(Health.unknown().build(), cut.health());
  }*/
}
