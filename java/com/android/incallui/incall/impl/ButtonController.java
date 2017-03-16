/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.incallui.incall.impl;

import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.telecom.CallAudioState;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.dialer.common.Assert;
import com.android.incallui.incall.impl.CheckableLabeledButton.OnCheckedChangeListener;
import com.android.incallui.incall.protocol.InCallButtonIds;
import com.android.incallui.incall.protocol.InCallButtonUiDelegate;
import com.android.incallui.incall.protocol.InCallScreenDelegate;

/** Manages a single button. */
interface ButtonController {

  boolean isEnabled();

  void setEnabled(boolean isEnabled);

  boolean isAllowed();

  void setAllowed(boolean isAllowed);

  void setChecked(boolean isChecked);

  @InCallButtonIds
  int getInCallButtonId();

  void setButton(CheckableLabeledButton button);

  final class Controllers {

    private static void resetButton(CheckableLabeledButton button) {
      if (button != null) {
        button.setOnCheckedChangeListener(null);
        button.setOnClickListener(null);
      }
    }
  }

  abstract class CheckableButtonController implements ButtonController, OnCheckedChangeListener {

    @NonNull protected final InCallButtonUiDelegate delegate;
    @InCallButtonIds protected final int buttonId;
    @StringRes protected final int checkedDescription;
    @StringRes protected final int uncheckedDescription;
    protected boolean isEnabled;
    protected boolean isAllowed;
    protected boolean isChecked;
    protected CheckableLabeledButton button;

    protected CheckableButtonController(
        @NonNull InCallButtonUiDelegate delegate,
        @InCallButtonIds int buttonId,
        @StringRes int checkedContentDescription,
        @StringRes int uncheckedContentDescription) {
      Assert.isNotNull(delegate);
      this.delegate = delegate;
      this.buttonId = buttonId;
      this.checkedDescription = checkedContentDescription;
      this.uncheckedDescription = uncheckedContentDescription;
    }

    @Override
    public boolean isEnabled() {
      return isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
      this.isEnabled = isEnabled;
      if (button != null) {
        button.setEnabled(isEnabled);
      }
    }

    @Override
    public boolean isAllowed() {
      return isAllowed;
    }

    @Override
    public void setAllowed(boolean isAllowed) {
      this.isAllowed = isAllowed;
      if (button != null) {
        button.setVisibility(isAllowed ? View.VISIBLE : View.INVISIBLE);
      }
    }

    @Override
    public void setChecked(boolean isChecked) {
      this.isChecked = isChecked;
      if (button != null) {
        button.setChecked(isChecked);
      }
    }

    @Override
    @InCallButtonIds
    public int getInCallButtonId() {
      return buttonId;
    }

    @Override
    @CallSuper
    public void setButton(CheckableLabeledButton button) {
      Controllers.resetButton(this.button);

      this.button = button;
      if (button != null) {
        button.setEnabled(isEnabled);
        button.setVisibility(isAllowed ? View.VISIBLE : View.INVISIBLE);
        button.setChecked(isChecked);
        button.setOnClickListener(null);
        button.setOnCheckedChangeListener(this);
        button.setContentDescription(
            button.getContext().getText(isChecked ? checkedDescription : uncheckedDescription));
        button.setShouldShowMoreIndicator(false);
      }
    }

    @Override
    public void onCheckedChanged(CheckableLabeledButton checkableLabeledButton, boolean isChecked) {
      button.setContentDescription(
          button.getContext().getText(isChecked ? checkedDescription : uncheckedDescription));
      doCheckedChanged(isChecked);
    }

    protected abstract void doCheckedChanged(boolean isChecked);
  }

  abstract class SimpleCheckableButtonController extends CheckableButtonController {

    @StringRes private final int label;
    @DrawableRes private final int icon;

    protected SimpleCheckableButtonController(
        @NonNull InCallButtonUiDelegate delegate,
        @InCallButtonIds int buttonId,
        @StringRes int checkedContentDescription,
        @StringRes int uncheckedContentDescription,
        @StringRes int label,
        @DrawableRes int icon) {
      super(
          delegate,
          buttonId,
          checkedContentDescription == 0 ? label : checkedContentDescription,
          uncheckedContentDescription == 0 ? label : uncheckedContentDescription);
      this.label = label;
      this.icon = icon;
    }

    @Override
    @CallSuper
    public void setButton(CheckableLabeledButton button) {
      super.setButton(button);
      if (button != null) {
        button.setLabelText(label);
        button.setIconDrawable(icon);
      }
    }
  }

  abstract class NonCheckableButtonController implements ButtonController, OnClickListener {

    protected final InCallButtonUiDelegate delegate;
    @InCallButtonIds protected final int buttonId;
    @StringRes protected final int contentDescription;
    protected boolean isEnabled;
    protected boolean isAllowed;
    protected CheckableLabeledButton button;

    protected NonCheckableButtonController(
        InCallButtonUiDelegate delegate,
        @InCallButtonIds int buttonId,
        @StringRes int contentDescription) {
      this.delegate = delegate;
      this.buttonId = buttonId;
      this.contentDescription = contentDescription;
    }

    @Override
    public boolean isEnabled() {
      return isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
      this.isEnabled = isEnabled;
      if (button != null) {
        button.setEnabled(isEnabled);
      }
    }

    @Override
    public boolean isAllowed() {
      return isAllowed;
    }

    @Override
    public void setAllowed(boolean isAllowed) {
      this.isAllowed = isAllowed;
      if (button != null) {
        button.setVisibility(isAllowed ? View.VISIBLE : View.INVISIBLE);
      }
    }

    @Override
    public void setChecked(boolean isChecked) {
      Assert.fail();
    }

    @Override
    @InCallButtonIds
    public int getInCallButtonId() {
      return buttonId;
    }

    @Override
    @CallSuper
    public void setButton(CheckableLabeledButton button) {
      Controllers.resetButton(this.button);

      this.button = button;
      if (button != null) {
        button.setEnabled(isEnabled);
        button.setVisibility(isAllowed ? View.VISIBLE : View.INVISIBLE);
        button.setChecked(false);
        button.setOnCheckedChangeListener(null);
        button.setOnClickListener(this);
        button.setContentDescription(button.getContext().getText(contentDescription));
        button.setShouldShowMoreIndicator(false);
      }
    }
  }

  abstract class SimpleNonCheckableButtonController extends NonCheckableButtonController {

    @StringRes private final int label;
    @DrawableRes private final int icon;

    protected SimpleNonCheckableButtonController(
        InCallButtonUiDelegate delegate,
        @InCallButtonIds int buttonId,
        @StringRes int contentDescription,
        @StringRes int label,
        @DrawableRes int icon) {
      super(delegate, buttonId, contentDescription == 0 ? label : contentDescription);
      this.label = label;
      this.icon = icon;
    }

    @Override
    @CallSuper
    public void setButton(CheckableLabeledButton button) {
      super.setButton(button);
      if (button != null) {
        button.setLabelText(label);
        button.setIconDrawable(icon);
      }
    }
  }

  class MuteButtonController extends SimpleCheckableButtonController {

    public MuteButtonController(InCallButtonUiDelegate delegate) {
      super(
          delegate,
          InCallButtonIds.BUTTON_MUTE,
          R.string.incall_content_description_muted,
          R.string.incall_content_description_unmuted,
          R.string.incall_label_mute,
          R.drawable.quantum_ic_mic_off_white_36);
    }

    @Override
    public void doCheckedChanged(boolean isChecked) {
      delegate.muteClicked(isChecked);
    }
  }

  class SpeakerButtonController
      implements ButtonController, OnCheckedChangeListener, OnClickListener {

    @NonNull private final InCallButtonUiDelegate delegate;
    private boolean isEnabled;
    private boolean isAllowed;
    private boolean isChecked;
    private CheckableLabeledButton button;

    @StringRes private int label = R.string.incall_label_speaker;
    @DrawableRes private int icon = R.drawable.quantum_ic_volume_up_white_36;
    private boolean checkable;
    private CharSequence contentDescription;
    private CharSequence checkedContentDescription;
    private CharSequence uncheckedContentDescription;

    public SpeakerButtonController(@NonNull InCallButtonUiDelegate delegate) {
      this.delegate = delegate;
    }

    @Override
    public boolean isEnabled() {
      return isEnabled;
    }

    @Override
    public void setEnabled(boolean isEnabled) {
      this.isEnabled = isEnabled;
      if (button != null) {
        button.setEnabled(isEnabled && isAllowed);
      }
    }

    @Override
    public boolean isAllowed() {
      return isAllowed;
    }

    @Override
    public void setAllowed(boolean isAllowed) {
      this.isAllowed = isAllowed;
      if (button != null) {
        button.setEnabled(isEnabled && isAllowed);
      }
    }

    @Override
    public void setChecked(boolean isChecked) {
      this.isChecked = isChecked;
      if (button != null) {
        button.setChecked(isChecked);
      }
    }

    @Override
    public int getInCallButtonId() {
      return InCallButtonIds.BUTTON_AUDIO;
    }

    @Override
    public void setButton(CheckableLabeledButton button) {
      this.button = button;
      if (button != null) {
        button.setEnabled(isEnabled && isAllowed);
        button.setVisibility(View.VISIBLE);
        button.setChecked(isChecked);
        button.setOnClickListener(checkable ? null : this);
        button.setOnCheckedChangeListener(checkable ? this : null);
        button.setLabelText(label);
        button.setIconDrawable(icon);
        button.setContentDescription(
            isChecked ? checkedContentDescription : uncheckedContentDescription);
        button.setShouldShowMoreIndicator(!checkable);
      }
    }

    public void setAudioState(CallAudioState audioState) {
      @StringRes int contentDescriptionResId;
      if ((audioState.getSupportedRouteMask() & CallAudioState.ROUTE_BLUETOOTH)
          == CallAudioState.ROUTE_BLUETOOTH) {
        checkable = false;
        isChecked = false;
        label = R.string.incall_label_audio;

        if ((audioState.getRoute() & CallAudioState.ROUTE_BLUETOOTH)
            == CallAudioState.ROUTE_BLUETOOTH) {
          icon = R.drawable.quantum_ic_bluetooth_audio_white_36;
          contentDescriptionResId = R.string.incall_content_description_bluetooth;
        } else if ((audioState.getRoute() & CallAudioState.ROUTE_SPEAKER)
            == CallAudioState.ROUTE_SPEAKER) {
          icon = R.drawable.quantum_ic_volume_up_white_36;
          contentDescriptionResId = R.string.incall_content_description_speaker;
        } else if ((audioState.getRoute() & CallAudioState.ROUTE_WIRED_HEADSET)
            == CallAudioState.ROUTE_WIRED_HEADSET) {
          icon = R.drawable.quantum_ic_headset_white_36;
          contentDescriptionResId = R.string.incall_content_description_headset;
        } else {
          icon = R.drawable.ic_phone_audio_white_36dp;
          contentDescriptionResId = R.string.incall_content_description_earpiece;
        }
      } else {
        checkable = true;
        isChecked = audioState.getRoute() == CallAudioState.ROUTE_SPEAKER;
        label = R.string.incall_label_speaker;
        icon = R.drawable.quantum_ic_volume_up_white_36;
        contentDescriptionResId = R.string.incall_content_description_speaker;
      }

      contentDescription = delegate.getContext().getText(contentDescriptionResId);
      checkedContentDescription =
          TextUtils.concat(
              contentDescription,
              delegate.getContext().getText(R.string.incall_talkback_speaker_on));
      uncheckedContentDescription =
          TextUtils.concat(
              contentDescription,
              delegate.getContext().getText(R.string.incall_talkback_speaker_off));
      setButton(button);
    }

    @Override
    public void onClick(View v) {
      delegate.showAudioRouteSelector();
    }

    @Override
    public void onCheckedChanged(CheckableLabeledButton checkableLabeledButton, boolean isChecked) {
      checkableLabeledButton.setContentDescription(
          isChecked ? checkedContentDescription : uncheckedContentDescription);
      delegate.toggleSpeakerphone();
    }
  }

  class DialpadButtonController extends SimpleCheckableButtonController {

    public DialpadButtonController(@NonNull InCallButtonUiDelegate delegate) {
      super(
          delegate,
          InCallButtonIds.BUTTON_DIALPAD,
          0,
          0,
          R.string.incall_label_dialpad,
          R.drawable.quantum_ic_dialpad_white_36);
    }

    @Override
    public void doCheckedChanged(boolean isChecked) {
      delegate.showDialpadClicked(isChecked);
    }
  }

  class HoldButtonController extends SimpleCheckableButtonController {

    public HoldButtonController(@NonNull InCallButtonUiDelegate delegate) {
      super(
          delegate,
          InCallButtonIds.BUTTON_HOLD,
          R.string.incall_content_description_unhold,
          R.string.incall_content_description_hold,
          R.string.incall_label_hold,
          R.drawable.quantum_ic_pause_white_36);
    }

    @Override
    public void doCheckedChanged(boolean isChecked) {
      delegate.holdClicked(isChecked);
    }
  }

  class AddCallButtonController extends SimpleNonCheckableButtonController {

    public AddCallButtonController(@NonNull InCallButtonUiDelegate delegate) {
      super(
          delegate,
          InCallButtonIds.BUTTON_ADD_CALL,
          0,
          R.string.incall_label_add_call,
          R.drawable.ic_addcall_white);
      Assert.isNotNull(delegate);
    }

    @Override
    public void onClick(View view) {
      delegate.addCallClicked();
    }
  }

  class SwapButtonController extends SimpleNonCheckableButtonController {

    public SwapButtonController(@NonNull InCallButtonUiDelegate delegate) {
      super(
          delegate,
          InCallButtonIds.BUTTON_SWAP,
          R.string.incall_content_description_swap_calls,
          R.string.incall_label_swap,
          R.drawable.quantum_ic_swap_calls_white_36);
      Assert.isNotNull(delegate);
    }

    @Override
    public void onClick(View view) {
      delegate.swapClicked();
    }
  }

  class MergeButtonController extends SimpleNonCheckableButtonController {

    public MergeButtonController(@NonNull InCallButtonUiDelegate delegate) {
      super(
          delegate,
          InCallButtonIds.BUTTON_MERGE,
          R.string.incall_content_description_merge_calls,
          R.string.incall_label_merge,
          R.drawable.quantum_ic_call_merge_white_36);
      Assert.isNotNull(delegate);
    }

    @Override
    public void onClick(View view) {
      delegate.mergeClicked();
    }
  }

  class UpgradeToVideoButtonController extends SimpleNonCheckableButtonController {

    public UpgradeToVideoButtonController(@NonNull InCallButtonUiDelegate delegate) {
      super(
          delegate,
          InCallButtonIds.BUTTON_UPGRADE_TO_VIDEO,
          0,
          R.string.incall_label_videocall,
          R.drawable.quantum_ic_videocam_white_36);
      Assert.isNotNull(delegate);
    }

    @Override
    public void onClick(View view) {
      delegate.changeToVideoClicked();
    }
  }

  class ManageConferenceButtonController extends SimpleNonCheckableButtonController {

    private final InCallScreenDelegate inCallScreenDelegate;

    public ManageConferenceButtonController(@NonNull InCallScreenDelegate inCallScreenDelegate) {
      super(
          null,
          InCallButtonIds.BUTTON_MANAGE_VOICE_CONFERENCE,
          R.string.a11y_description_incall_label_manage_content,
          R.string.incall_label_manage,
          R.drawable.quantum_ic_group_white_36);
      Assert.isNotNull(inCallScreenDelegate);
      this.inCallScreenDelegate = inCallScreenDelegate;
    }

    @Override
    public void onClick(View view) {
      inCallScreenDelegate.onManageConferenceClicked();
    }
  }

  class SwitchToSecondaryButtonController extends SimpleNonCheckableButtonController {

    private final InCallScreenDelegate inCallScreenDelegate;

    public SwitchToSecondaryButtonController(InCallScreenDelegate inCallScreenDelegate) {
      super(
          null,
          InCallButtonIds.BUTTON_SWITCH_TO_SECONDARY,
          R.string.incall_content_description_swap_calls,
          R.string.incall_label_swap,
          R.drawable.quantum_ic_swap_calls_white_36);
      Assert.isNotNull(inCallScreenDelegate);
      this.inCallScreenDelegate = inCallScreenDelegate;
    }

    @Override
    public void onClick(View view) {
      inCallScreenDelegate.onSecondaryInfoClicked();
    }
  }
}