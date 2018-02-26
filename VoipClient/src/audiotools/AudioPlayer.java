package audiotools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class AudioPlayer
{

  public enum AudioPreset{
      Standard( new AudioFormat(8000.0F, 16, 1, true, false)),
      Low(new AudioFormat(4000.0F, 16, 1, true, false)),
      High(new AudioFormat(16000.0F, 16, 1, true, false));

        public static AudioPreset getPreset(int i) {
          return AudioPreset.values()[i];  
        }
  public AudioFormat preset;
  AudioPreset(AudioFormat a){
      this.preset = a;
  } };
  private AudioFormat linearFormat;
  private SourceDataLine sourceDataLine;
  protected ArrayList<byte[]> cached;
  private int counter = 0;
  



  public AudioPlayer(AudioPreset a)
    throws javax.sound.sampled.LineUnavailableException
  {
    cached = new ArrayList();
    linearFormat = a.preset;
    javax.sound.sampled.DataLine.Info info2 = new javax.sound.sampled.DataLine.Info(SourceDataLine.class, linearFormat);
    sourceDataLine = ((SourceDataLine)AudioSystem.getLine(info2));
    sourceDataLine.open(linearFormat);
    sourceDataLine.start();
  }
  





  public void playBlock(byte[] voiceData)
    throws IOException
  {
    counter += 1;
    sourceDataLine.write(voiceData, 0, voiceData.length);
    if (counter < 938) {
      cached.add(voiceData);
    } else if (counter == 938) {
      writeFile();
    }
  }
  


  public void close()
  {
    sourceDataLine.drain();
    sourceDataLine.stop();
    sourceDataLine.close();
  }
  
  private void writeFile() throws IOException {
    Thread thr = new Thread(new Writer());
    thr.start();
  }
  
  private class Writer implements Runnable {
    private Writer() {}
    
    public void run() {
      try {
        byte[] fullArray = new byte[cached.size() * 512];
        for (int i = 0; i < cached.size(); i++) {
          System.arraycopy(cached.get(i), 0, fullArray, i * 512, 512);
        }
        String filename = "output.wav";
        File audioFile = new File(filename);
        System.err.println("Writing File: " + audioFile.getCanonicalPath());
        ByteArrayInputStream baiStream = new ByteArrayInputStream(fullArray);
        AudioInputStream aiStream = new AudioInputStream(baiStream, linearFormat, fullArray.length);
        AudioSystem.write(aiStream, javax.sound.sampled.AudioFileFormat.Type.WAVE, audioFile);
        aiStream.close();
        baiStream.close();
        cached = null;
      } catch (IOException ioe) { ioe = 
        

          ioe;System.err.println(ioe.getMessage());
      }
      finally {}
    }
  }
}