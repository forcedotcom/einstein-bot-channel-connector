/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.twilio.connector.service;

import com.twilio.Twilio;
import com.twilio.twiml.MessagingResponse;
import com.twilio.type.PhoneNumber;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Class to send SMS using Twilio's SDK
 */
@Service
public class TwilioSMSDispatchService {

  private static final Logger logger = LoggerFactory.getLogger(TwilioSMSDispatchService.class);

  @Value("${twilio.account-sid}")
  private String ACCOUNT_SID;

  @Value("${twilio.auth-key}")
  private String AUTH_TOKEN;


  @PostConstruct
  private void setup() {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    logger.info("Twilio Dispatcher setup complete");
  }

  /**
   * This method generates the TWIML code for sending an sms
   */

  public String getTwilioResponse(String response) {
    com.twilio.twiml.messaging.Message twilioMessage = new com.twilio.twiml.messaging.Message.Builder(
        response)
        .build();
    MessagingResponse twilioResponse = new MessagingResponse.Builder()
        .message(twilioMessage).build();
    return twilioResponse.toXml();
  }

  /**
   * This method sends an SMS with content equal to response to toPhoneNumber
   */
  @Async
  public void sendSMS(String toPhoneNumber, String response) {
    logger.debug("Sending sms to: {}", toPhoneNumber);
    PhoneNumber phoneNumber = new PhoneNumber(toPhoneNumber);
    PhoneNumber fromPhoneNumber = new PhoneNumber("+18506045818");
    com.twilio.rest.api.v2010.account.Message message = com.twilio.rest.api.v2010.account.Message.creator(
            phoneNumber, fromPhoneNumber, response)
        .create();
  }
}
