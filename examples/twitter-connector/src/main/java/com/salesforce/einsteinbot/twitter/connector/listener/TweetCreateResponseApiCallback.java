/*
 * Copyright (c) 2022, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.einsteinbot.twitter.connector.listener;

import com.twitter.clientlib.ApiCallback;
import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.model.TweetCreateResponse;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Callback class for async create tweet calls
 */
public class TweetCreateResponseApiCallback implements ApiCallback<TweetCreateResponse> {

  private static final Logger logger = LoggerFactory.getLogger(
      TweetCreateResponseApiCallback.class);

  @Override
  public void onFailure(ApiException e, int i, Map<String, List<String>> map) {
    logger.error("Failed to post tweet: {}", map);
    logger.error("Failed to post tweet. Response code: {}, Response body: {}",
        e.getCode(),
        e.getResponseBody());
  }

  @Override
  public void onSuccess(TweetCreateResponse tweetCreateResponse, int i,
      Map<String, List<String>> map) {
    logger.info("Successfully replied to tweet");
  }

  @Override
  public void onUploadProgress(long l, long l1, boolean b) {
    logger.debug("Upload progress: {} - {}. Success: {}", l, l1);
  }

  @Override
  public void onDownloadProgress(long l, long l1, boolean b) {
    logger.debug("Upload progress: {} - {}. Success: {}", l, l1, b);
  }
}
