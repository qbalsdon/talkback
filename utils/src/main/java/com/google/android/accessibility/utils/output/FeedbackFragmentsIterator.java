/*
 * Copyright (C) 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.android.accessibility.utils.output;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.accessibility.utils.AccessibilityNodeInfoUtils;
import com.google.android.libraries.accessibility.utils.log.LogUtils;
import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

/** Provides FeedbackFragment iterator usage and record current {@link FeedbackFragment}. */
class FeedbackFragmentsIterator {
  private static final String TAG = "FeedbackFragmentsIterator";

  private Iterator<FeedbackFragment> currentFragmentIterator;

  private String feedBackItemUtteranceId;

  /** It's available when speaking its content and null between speaking each fragment. */
  private final AtomicReference<FeedbackFragment> currentFeedbackFragment = new AtomicReference<>();

  public FeedbackFragmentsIterator(@NonNull Iterator<FeedbackFragment> currentFragmentIterator) {
    this.currentFragmentIterator = currentFragmentIterator;
  }

  /**
   * @return next feedbackFragment from iterator.
   */
  @Nullable
  FeedbackFragment next() {
    FeedbackFragment feedbackFragment = currentFeedbackFragment.get();
    if (feedbackFragment != null) {
      // Has pending Fragment.
      feedbackFragment.updateContentByFragmentStartIndex();
    } else if (currentFragmentIterator.hasNext()) {
      feedbackFragment = currentFragmentIterator.next();
      currentFeedbackFragment.set(feedbackFragment);
    }
    if (feedbackFragment != null) {
      LogUtils.v(TAG, "next --currentFeedbackFragment text = %s.", feedbackFragment.getText());
    }
    return feedbackFragment;
  }

  private void recordUtteranceStartIndex(int utteranceStartIndex) {
    FeedbackFragment feedbackFragment = currentFeedbackFragment.get();
    if (feedbackFragment != null) {
      feedbackFragment.recordFragmentStartIndex(utteranceStartIndex);
    }
  }

  /** Returns the offset of current feedbackFragment text in {@link FeedbackItem}. */
  int getFeedbackItemOffset() {
    FeedbackFragment feedbackFragment = currentFeedbackFragment.get();
    if (feedbackFragment != null) {
      return feedbackFragment.getStartIndexInFeedbackItem();
    }
    return 0;
  }

  /**
   * Records the length of spoken sequence. Call it in {@link
   * SpeechControllerImpl#onFragmentCompleted(String, boolean, boolean, boolean)}.
   */
  void onFragmentCompleted(String utteranceId, boolean success) {
    if (!TextUtils.equals(utteranceId, feedBackItemUtteranceId)) {
      // Assume it's failed.
      LogUtils.w(
          TAG,
          "onFragmentCompleted -- utteranceId = %s,feedBackItemUtteranceId =  %s",
          utteranceId,
          feedBackItemUtteranceId);
      return;
    }
    if (success) {
      currentFeedbackFragment.set(null);
    }
  }

  void setFeedBackItemUtteranceId(String feedBackItemUtteranceId) {
    this.feedBackItemUtteranceId = feedBackItemUtteranceId;
  }

  /**
   * Records the index of the sequence to start. Call it in {@link
   * SpeechControllerImpl#onFragmentRangeStarted(String, int, int)}.
   */
  void onFragmentRangeStarted(String utteranceId, int start, int end) {
    if (TextUtils.equals(utteranceId, feedBackItemUtteranceId)) {
      recordUtteranceStartIndex(start);
      FeedbackFragment feedbackFragment = currentFeedbackFragment.get();
      LogUtils.v(
          TAG,
          "onFragmentRangeStarted ,  speak word = %s",
          AccessibilityNodeInfoUtils.subsequenceSafe(
              feedbackFragment == null ? null : feedbackFragment.getText(), start, end));
    } else {
      LogUtils.d(
          TAG,
          "onFragmentRangeStarted ,difference utteranceId, expected:%s ,actual:%s",
          feedBackItemUtteranceId,
          utteranceId);
    }
  }

  /**
   * DeepCopy(Clone) FeedbackFragmentIterator
   *
   * @return object copied from FeedbackFragmentIterator.
   */
  @SuppressWarnings({"unchecked"})
  public FeedbackFragmentsIterator deepCopy() {
    ArrayList<FeedbackFragment> list = new ArrayList<>();

    Iterators.addAll(list, currentFragmentIterator);

    FeedbackFragmentsIterator clone = new FeedbackFragmentsIterator(list.iterator());
    clone.currentFeedbackFragment.set(currentFeedbackFragment.get());
    clone.setFeedBackItemUtteranceId(feedBackItemUtteranceId);

    currentFragmentIterator = ((ArrayList<FeedbackFragment>) list.clone()).iterator();

    return clone;
  }
}
