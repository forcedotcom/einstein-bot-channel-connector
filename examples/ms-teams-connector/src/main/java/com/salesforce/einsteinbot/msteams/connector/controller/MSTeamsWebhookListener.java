/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.msteams.connector.controller;

import com.microsoft.bot.schema.Activity;
import com.salesforce.einsteinbot.msteams.connector.service.EinsteinBotService;
import java.io.IOException;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Exposes an endpoint for the outgoing Webhook from MS Teams
 */
@RestController
@RequestMapping("/")
public class MSTeamsWebhookListener {

  private static final Logger logger = LoggerFactory.getLogger(MSTeamsWebhookListener.class);
  @Autowired
  private EinsteinBotService einsteinBotService;

  @PostMapping("/bot")
  public Activity sendToChatbot(@RequestBody Activity activity) throws URISyntaxException, IOException
  {
    String teamsSessionId = activity.getFrom().getId();
    String senderName = activity.getFrom().getName();
    // remove bot handle/mention from the message
    logger.debug("Request received from MS-Teams user {}.",senderName);
    String incomingMessage = activity.getText().replaceAll("(?s)<at>.*?</at>", "").trim();
    String responseMessage = this.einsteinBotService.sendMessageSync(teamsSessionId, incomingMessage);

    responseMessage = "<at>@"+senderName+"</at> "+ responseMessage;
    Activity responseActivity = activity.createReply(responseMessage);
    return responseActivity;
  }

}
