package muratoda.silencetoggle;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;


public class MainActivity extends Activity {
    private static final int ZEN_MODE_ALL = 0;
    private static final int ZEN_MODE_PRIORITY = 1;
    private static final int ZEN_MODE_NONE = 2;
    private static final int ZEN_MODE_UNKNOWN = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        final int currentRingerMode = audioManager.getRingerMode();
        switch(currentRingerMode) {
            // Lollipop is buggy and returns RINGER_MODE_NORMAL even when in silent,
            // so we check the zen mode instead.
            // This code will still work when the bug gets fixed.
            case RINGER_MODE_NORMAL:
            case RINGER_MODE_SILENT:
                if (ZEN_MODE_ALL == getZenMode()) {
                    audioManager.setRingerMode(RINGER_MODE_VIBRATE);
                } else {
                    audioManager.setRingerMode(RINGER_MODE_NORMAL);
                    audioManager.setStreamVolume(
                            AudioManager.STREAM_RING,
                            audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                            AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE
                    );
                }
                break;
            case RINGER_MODE_VIBRATE:
                audioManager.setRingerMode(RINGER_MODE_SILENT);
                break;
        }
        finish();
    }

    private int getZenMode() {
        try {
            return Settings.Global.getInt(getApplicationContext().getContentResolver(), "zen_mode");
        } catch (Settings.SettingNotFoundException e) {
            return ZEN_MODE_UNKNOWN;
        }
    }

}
