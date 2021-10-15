package com.kamenov.ocrdude.utils;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SoundEffectHelper {
    public static void soundEffect() {
        try {
            InputStream is = ClassLoader.getSystemResourceAsStream("soundeffects/camera-shutter-click-03.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            log.warn("Could not play sounds effect ", e);
        }
    }
}
