package com.optivat.bot;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.CombinedAudio;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ARHCustom implements AudioReceiveHandler {
    private List<byte[]> rescievedBytes = new ArrayList<>();
    private double VOLUME = 1.0;
    private String filePath = "src\\main\\resources\\crazy.wav";

    private String message = "";

    private final AudioFormat format = new AudioFormat(48000.0F, 16, 2, true, true);
    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio){
        try {
            rescievedBytes.add(combinedAudio.getAudioData(VOLUME));

            int size=0;
            for (byte[] bs : rescievedBytes) {
                size+=bs.length;
            }
            byte[] decodedData=new byte[size];
            int i=0;
            for (byte[] bs : rescievedBytes) {
                for (int j = 0; j < bs.length; j++) {
                    decodedData[i++]=bs[j];
                }
            }

            File file = new File(filePath);

            decodedData = PcmToWavUtil.pcmToWav(decodedData, 2, 48000, 16);
            getWavFile(file ,decodedData);
        } catch (OutOfMemoryError e) {
            //close connection
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean canReceiveCombined() {
        return true;
    }
    private void getWavFile(File outFile, byte[] decodedData) throws IOException, InterruptedException {
        AudioInputStream audioInputStream = new AudioInputStream(new ByteArrayInputStream(decodedData), format, decodedData.length);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outFile);
        audioInputStream.close();
        if(outFile.length() > 1000000) {
            if(outFile.exists()) {
                wavToText(outFile);
            }
        }
    }
    private void wavToText(File file) throws IOException {
        Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        //configuration.setDictionaryPath("src\\main\\resources\\0277.dic");
        //configuration.setLanguageModelPath("src\\main\\resources\\0277.lm");
        //configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        //configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
        configuration.setSampleRate(48000);
        configuration.setDictionaryPath("src\\main\\resources\\3060.dic");
        configuration.setLanguageModelPath("src\\main\\resources\\3060.lm");
        configuration.setGrammarPath("src\\main\\resources\\3060.vocab");


        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
        InputStream stream = new FileInputStream(file);

        recognizer.startRecognition(stream);
        SpeechResult result;
        while ((result = recognizer.getResult()) != null) {
            System.out.format("Hypothesis: %s\n", result.getHypothesis());
            if(result.getHypothesis().length() > 2) {
                message+=result.getHypothesis().strip() + " ";
            }
            System.out.println(message.toLowerCase());
            if(message.toLowerCase().contains("mods ban this guy")) {
                int RandomValue = new Random().nextInt(0, RecognitionListener.voiceChannel.getMembers().size());
                Member member = RecognitionListener.voiceChannel.getMembers().get(RandomValue);
                while (true) {
                    if(member.getUser().isBot()) {
                        RandomValue = new Random().nextInt(0, RecognitionListener.voiceChannel.getMembers().size());
                        member = RecognitionListener.voiceChannel.getMembers().get(RandomValue);
                    } else {
                        break;
                    }
                }
                member.getGuild().kickVoiceMember(member).queue();
                message = "";
            }
        }
        rescievedBytes = new ArrayList<>();
        recognizer.stopRecognition();
        stream.close();
        file.delete();
    }
}
