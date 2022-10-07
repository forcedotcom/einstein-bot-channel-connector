/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.msteams.connector.controller;

import com.salesforce.einsteinbot.msteams.connector.service.EinsteinBotService;
import com.microsoft.bot.schema.Activity;
import java.io.IOException;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


/**
 * Exposes an endpoint for the outgoing Webhook from MS Teams
 */
@RestController
@RequestMapping("/")
public class MSTeamsWebhookListener {

  private static final Logger logger = LoggerFactory.getLogger(MSTeamsWebhookListener.class);
  private final RestTemplate restTemplate;

  @Autowired
  private EinsteinBotService einsteinBotService;

  public MSTeamsWebhookListener(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  @PostMapping("/bot")
  public Activity sendToDo(@RequestBody Activity activity) throws URISyntaxException, IOException
  {
    String teamsSessionId = activity.getFrom().getId();
    String senderName = activity.getFrom().getName();
    // remove bot handle/mention from the message
    String incomingMessage = activity.getText().replaceAll("(?s)<at>.*?</at>", "").trim();
    String responseMessage = this.einsteinBotService.sendMessageSync(teamsSessionId, incomingMessage);

    responseMessage = "<at>@"+senderName+"</at> "+ responseMessage;
    Activity responseActivity = activity.createReply(responseMessage);
    return responseActivity;
  }

}
