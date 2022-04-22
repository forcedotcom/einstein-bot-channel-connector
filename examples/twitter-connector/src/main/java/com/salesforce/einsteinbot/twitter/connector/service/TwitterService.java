/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.einsteinbot.twitter.connector.service;

import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Bootstrap service to start streaming twitter feed asynchronously
 */
@Service
public class TwitterService {

  private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

  @Autowired
  private TwitterStreamService twitterStreamService;

  public TwitterService() {
  }

  @PostConstruct
  public void setup() {
    logger.info("Setup TwitterService");
    this.twitterStreamService.startStreaming();
    logger.info("Done setting up twitter service");
  }

}
