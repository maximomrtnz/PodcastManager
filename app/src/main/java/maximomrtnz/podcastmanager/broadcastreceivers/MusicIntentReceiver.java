package maximomrtnz.podcastmanager.broadcastreceivers;

/*
 * Copyright (C) 2011 The Android Open Source Project
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

import maximomrtnz.podcastmanager.services.PlayerService;

/**
 * Receives broadcasted intents. In particular, we are interested in the
 * android.media.AUDIO_BECOMING_NOISY and android.intent.action.MEDIA_BUTTON intents, which is
 * broadcast, for example, when the user disconnects the headphones. This class works because we are
 * declaring it in a &lt;receiver&gt; tag in AndroidManifest.xml.
 */
public class MusicIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;
            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    doAction(context,PlayerService.ACTION_TOGGLE_PLAYBACK);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    doAction(context,PlayerService.ACTION_PLAY);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    doAction(context,PlayerService.ACTION_PAUSE);
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    doAction(context,PlayerService.ACTION_STOP);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    doAction(context,PlayerService.ACTION_SKIP_NEXT);
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    // previous song
                    doAction(context,PlayerService.ACTION_SKIP_PREVIOUS);
                    break;
            }
        }
    }

    public void doAction(Context context, String action){
        Intent i = new Intent(context,PlayerService.class);
        i.setAction(action);
        context.startService(i);
    }
}