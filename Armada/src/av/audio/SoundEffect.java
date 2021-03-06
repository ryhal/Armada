package av.audio; 

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
   
/**
	This enum encapsulates all the sound effects of the game, so as to separate the code for sound effects from the code for the game itself.
*/  

 //1.Define all your sound effect names and the associated wave file.
 //2.To play a specific sound, simply invoke SoundEffect.SOUND_NAME.play().
 //3.You might optionally invoke the static method SoundEffect.init() to pre-load all the
 //sound files, so that the play is not paused while loading the file for the first time.
 //4.You can use the static variable SoundEffect.volume to mute the sound.
 
/** Defines all sound effect names for their associate wav files. */
public enum SoundEffect {
   EXPLODE("explode1.wav")//explode
   , SCREAM("WilhelmScream.wav")// scream
   , LASER("laser.wav")//laser
   , PIRATEF("You Are A Pirate Full.wav")
   , PIRATEH("You Are A Pirate Half.wav")
   , PIRATE1("You_Are_A_Pirate_1.wav")
   , PIRATE2("You_Are_A_Pirate_2.wav")
   , PIRATE3("You_Are_A_Pirate_3.wav")
   , PIRATE4("You_Are_A_Pirate_4.wav")
   //, SONG("U.N. Owen Was Her. (ZUN).wav")
   //All sounds added must be in the format of the above line and come before the ";" line
   ;
   
   //* Nested class for specifying volume. */
   public static enum Volume {
      MUTE, LOW, MEDIUM, HIGH
   }
   
   public static Volume volume = Volume.LOW;
   
   /** Each sound effect has its own clip loaded with its own sound file. */
   private Clip clip;
   
   /** Constructor that creates each element of the enum with its own sound file. */
   SoundEffect(String soundFileName) {
      try {
         // Use URL (instead of File) to read from disk and JAR.
         URL url = this.getClass().getClassLoader().getResource("sound/"+soundFileName);

         // Set up an audio input stream piped from the sound file.
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
         // Get a clip resource.
         clip = AudioSystem.getClip();
         // Open audio clip and load samples from the audio input stream.
         clip.open(audioInputStream);
      } catch (UnsupportedAudioFileException e) {
         e.printStackTrace();
      } catch (IOException e) {
         e.printStackTrace();
      } catch (LineUnavailableException e) {
         e.printStackTrace();
      }
   }
   
   /** Play or replay the sound effect from the beginning by rewinding. */
   public void play() {
      if (volume != Volume.MUTE) {
         if (clip.isRunning())
            clip.stop();   // Stop the player if it is still running
         clip.setFramePosition(0); // rewind to the beginning
         clip.start();     // Start playing
      }
   }
   
   /** Optional static method to pre-load all the sound files. */
   public static void init() {
      values(); // calls the constructor for all the elements
   }
}