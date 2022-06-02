/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.connector.chatbot.service;

import com.salesforce.einsteinbot.sdk.client.model.ExternalSessionId;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SessionIdProvider {
  private static final Logger logger = LoggerFactory.getLogger(SessionIdProvider.class);

  private HashMap<String, ExternalSessionId> sessionIds;

  public SessionIdProvider(){
    this.sessionIds = new HashMap<>();
  }

  private ExternalSessionId createSessionId(){
    return new ExternalSessionId(UUID.randomUUID().toString());
  }

  public void newSessionId(String key){
    this.sessionIds.put(key, createSessionId());
  }

  public ExternalSessionId getSessionId(String key){
    if (!this.sessionIds.containsKey(key)){
      this.sessionIds.put(key, createSessionId());
    }
    logger.debug("External Session id for user {} is {} " , key, this.sessionIds.get(key));
    return this.sessionIds.get(key);
  }

}
