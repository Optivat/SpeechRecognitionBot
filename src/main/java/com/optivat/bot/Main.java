package com.optivat.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.IOException;



public class Main {
    public static void main(String[] args) {
        JDABuilder builder = JDABuilder.createDefault("OTA4ODEyMjM5Njk0MzUyNDY0.Gz4vJn.rXNly9zfS5j76IdDvW7yRqWxzo6kfnqrgxodvQ");
        // Disable parts of the cache
        builder.enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.watching("TV"));
        builder.addEventListeners(new RecognitionListener());
        jda = builder.build();

        jda.updateCommands().addCommands(
                Commands.slash("join", "Ban a user from the server")
                        .setDescription("Maes bot join a specific voice channel.")
        ).queue();

    }

    public static JDA jda;
}