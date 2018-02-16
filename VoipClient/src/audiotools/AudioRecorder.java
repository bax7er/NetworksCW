package audiotools;

/**
 *
 * @author scamp
 */
import audiotools.AudioPlayer.AudioPreset;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder
{
  private TargetDataLine targetDataLine;
  private AudioInputStream linearStream;
  private AudioFormat linearFormat;
  private int counter = 0;
  

  protected ArrayList<byte[]> cached;
  

  public AudioRecorder(AudioPreset a)
    throws javax.sound.sampled.LineUnavailableException
  {
    cached = new ArrayList();
    linearFormat = a.preset;
    javax.sound.sampled.DataLine.Info info = new javax.sound.sampled.DataLine.Info(TargetDataLine.class, linearFormat);
    targetDataLine = ((TargetDataLine)AudioSystem.getLine(info));
    targetDataLine.open(linearFormat);
    targetDataLine.start();
    linearStream = new AudioInputStream(targetDataLine);
  }
  


  public void close()
  {
    targetDataLine.stop();
    targetDataLine.close();
  }
  






  public byte[] getBlock()
    throws IOException
  {
    byte[] voiceData = new byte['Ȁ'];
    linearStream.read(voiceData, 0, voiceData.length);
    counter += 1;
    if (counter < 938) {
      cached.add(voiceData);
    } else if (counter == 938) {
      writeFile();
    }
    return voiceData;
  }
  
  private void writeFile() throws IOException {
    Thread thr = new Thread(new Writer());
    thr.start();
  }
  
  private class Writer implements Runnable {
    private Writer() {}
    
    public void run() {
      try { byte[] fullArray = new byte[cached.size() * 512];
        for (int i = 0; i < cached.size(); i++) {
          System.arraycopy(cached.get(i), 0, fullArray, i * 512, 512);
        }
        String filename = "input.wav";
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

