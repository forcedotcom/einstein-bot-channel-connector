/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.example.util;

import com.salesforce.einsteinbot.connector.example.model.RequestMessageType;
import com.salesforce.einsteinbot.sdk.client.model.BotEndSessionRequest;
import com.salesforce.einsteinbot.sdk.client.model.BotRequest;
import com.salesforce.einsteinbot.sdk.client.model.BotSendMessageRequest;
import com.salesforce.einsteinbot.sdk.client.util.RequestEnvelopeInterceptor;
import com.salesforce.einsteinbot.sdk.client.util.RequestFactory;
import com.salesforce.einsteinbot.sdk.model.AnyRequestMessage;
import com.salesforce.einsteinbot.sdk.model.ChoiceMessage;
import com.salesforce.einsteinbot.sdk.model.EndSessionMessage;
import com.salesforce.einsteinbot.sdk.model.EndSessionReason;
import com.salesforce.einsteinbot.sdk.model.ForceConfig;
import com.salesforce.einsteinbot.sdk.model.TextMessage;
import com.salesforce.einsteinbot.sdk.model.TransferFailedRequestMessage;
import com.salesforce.einsteinbot.sdk.model.TransferSucceededRequestMessage;
import java.util.List;
import java.util.Optional;

/**
 * RequestMessageFactory - Provides factory methods to build Chatbot Request Messages
 *
 * @author relango
 */
public class RequestMessageFactory {

  public static BotSendMessageRequest buildBotRequest(AnyRequestMessage message, RequestEnvelopeInterceptor requestEnvelopeInterceptor) {
    return BotRequest.withMessage(message)
        .requestEnvelopeInterceptor(requestEnvelopeInterceptor)
        .build();
  }

  public static BotEndSessionRequest buildBotRequest(EndSessionReason endSessionReason, RequestEnvelopeInterceptor requestEnvelopeInterceptor) {
    return  BotRequest
        .withEndSession(endSessionReason)
        .requestEnvelopeInterceptor(requestEnvelopeInterceptor)
        .build();
  }

  public static AnyRequestMessage buildTransferSuccessMessage() {
    return new TransferSucceededRequestMessage()
        .type(TransferSucceededRequestMessage.TypeEnum.TRANSFERSUCCEEDED)
        .sequenceId(System.currentTimeMillis());
  }

  public static AnyRequestMessage buildTransferFailedMessage() {
    return new TransferFailedRequestMessage()
        .type(TransferFailedRequestMessage.TypeEnum.TRANSFERFAILED)
        .reason(TransferFailedRequestMessage.ReasonEnum.NOAGENTAVAILABLE)
        .sequenceId(System.currentTimeMillis());
  }

  public static AnyRequestMessage buildChoiceMessage(RequestMessageType messageType,
      String choiceIdOrIndex) {
    ChoiceMessage msg = new ChoiceMessage()
        .type(ChoiceMessage.TypeEnum.CHOICE)
        .sequenceId(System.currentTimeMillis());

    if (messageType.equals(RequestMessageType.CHOICE_ID)) {
      msg.choiceId(choiceIdOrIndex);
    }
    if (messageType.equals(RequestMessageType.CHOICE_INDEX)) {
      msg.choiceIndex(Integer.valueOf(choiceIdOrIndex));
    }
    return msg;
  }
}