/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.twilio.connector.controller;

import com.salesforce.einsteinbot.twilio.connector.service.EinsteinBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * TwilioSMSListener - Controller that listens for incoming SMS and dispatch it to Einstein Chatbot
 * Service.
 */
@Controller
@RequestMapping("bot")
public class TwilioSMSListener {

  private static final Logger logger = LoggerFactory.getLogger(TwilioSMSListener.class);

  @Autowired
  private EinsteinBotService einsteinBotService;

  @GetMapping(value = "/twiml", produces = "application/xml")
  @ResponseBody
  public String acceptMessage(
      @RequestParam("From") String from,
      @RequestParam("Body") String body) {

    logger.info("message received: " + body);
    String response = "";

    // async handling, send the sms to einsteinBotService and exit
    //this.einsteinBotService.sendMessage(from,body);

    //sync handling, send the sms to einsteinBotService and wait for its response in TWIML format
    response = this.einsteinBotService.sendMessageSync(from, body);

    return response;
  }

}
