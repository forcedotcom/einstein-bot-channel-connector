/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.einsteinbot.connector.example.util;

import com.salesforce.einsteinbot.connector.example.model.RequestMessageType;
import com.salesforce.einsteinbot.sdk.model.AnyRequestMessage;
import com.salesforce.einsteinbot.sdk.model.ChoiceMessage;
import com.salesforce.einsteinbot.sdk.model.EndSessionMessage;
import com.salesforce.einsteinbot.sdk.model.ForceConfig;
import com.salesforce.einsteinbot.sdk.model.InitMessage;
import com.salesforce.einsteinbot.sdk.model.RequestEnvelope;
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

  public static RequestEnvelope buildRequestEnvelop(String sessionId,
      String botId,
      String forceConfigEndPoint,
      List<AnyRequestMessage> messages) {
    return new RequestEnvelope()
        .externalSessionKey(sessionId)
        .botId(botId)
        .forceConfig(new ForceConfig().endpoint(forceConfigEndPoint))
        .messages(messages);
  }

  public static AnyRequestMessage buildInitMessage(Optional<String> msg) {
    return new InitMessage()
        .text(msg.orElse(""))
        .type(InitMessage.TypeEnum.INIT)
        .sequenceId(System.currentTimeMillis());
  }

  public static AnyRequestMessage buildTextMessage(String msg) {
    return new TextMessage()
        .text(msg)
        .type(TextMessage.TypeEnum.TEXT)
        .sequenceId(System.currentTimeMillis());
  }

  public static AnyRequestMessage buildEndSessionMessage() {
    return new EndSessionMessage()
        .type(EndSessionMessage.TypeEnum.ENDSESSION)
        .reason(EndSessionMessage.ReasonEnum.USERREQUEST)
        .sequenceId(System.currentTimeMillis());
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
