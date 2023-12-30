package com.optivat.bot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;

public class RecognitionListener extends ListenerAdapter {
    public static AudioChannelUnion voiceChannel = null;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        // make sure we handle the right command
        switch (e.getName()) {
            case "join":
                e.reply("Joining VC...").queue();
                Member member = Objects.requireNonNull(e.getGuild()).getSelfMember();
                MessageChannel channel = e.getMessageChannel();
                if(!e.getGuild().getSelfMember().hasPermission((GuildChannel) channel, Permission.VOICE_CONNECT)) {
                    // The bot does not have permission to join any voice channel. Don't forget the .queue()!
                    channel.sendMessage("I do not have permissions to join a voice channel!").queue();
                    return;
                }
                // Creates a variable equal to the channel that the user is in.
                GuildVoiceState botVoiceState = member.getVoiceState();
                if(botVoiceState == null) {e.getHook().sendMessage("Null voice state.").queue();return;}
                if(!botVoiceState.inAudioChannel()) {
                    // Gets the audio manager.
                    AudioManager audioManager = e.getGuild().getAudioManager();
                    // When somebody really needs to chill.
                    Member person = e.getMember();
                    assert person != null;
                    if(person.getVoiceState() == null) {e.getHook().sendMessage("You are not in a VC!").queue();return;}
                    if(person.getVoiceState().getChannel() == null) {e.getHook().sendMessage("You are not in a VC!").queue();return;}
                    voiceChannel = e.getMember().getVoiceState().getChannel();
                    audioManager.openAudioConnection(voiceChannel);
                    // Obviously people do not notice someone/something connecting.
                    e.getHook().sendMessage("Connected to the voice channel!").queue();

                    AudioReceiveHandler custom = new ARHCustom();
                    custom.canReceiveCombined();
                    audioManager.setReceivingHandler(custom);
                }
        }
    }
}
