package me.s4rtox.mmhunt.util;

import lombok.NonNull;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.time.Duration;

public class TitleBuilder {
    public static void showTitle(final @NonNull Audience target, String title, String subtitle ){
        final Component mainTitle;
        final Component mainSubtitle;
        if(title.length() != 0){
            mainTitle = Component.text(Colorize.format(title));
        }else{
            mainTitle = Component.empty();
        }
        if(subtitle.length() !=  0){
            mainSubtitle = Component.text(Colorize.format(subtitle));
        }else{
            mainSubtitle = Component.empty();
        }

        // Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
        final Title titulo = Title.title(mainTitle, mainSubtitle);

        // Send the title to your audience
        target.showTitle(titulo);
    }

    public static void showTitle(final @NonNull Audience target, String title, String subtitle, long fadeIn, long stayIn, long fadeOut){
        final Title.Times times = Title.Times.times(Duration.ofMillis(fadeIn * 1000), Duration.ofMillis(stayIn * 1000), Duration.ofMillis(fadeOut * 1000));
        final Component mainTitle;
        final Component mainSubtitle;
        if(title.length() != 0){
            mainTitle = Component.text(Colorize.format(title));
        }else{
            mainTitle = Component.empty();
        }
        if(subtitle.length() != 0){
            mainSubtitle = Component.text(Colorize.format(subtitle));
        }else{
            mainSubtitle = Component.empty();
        }

        // Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
        final Title titulo = Title.title(mainTitle, mainSubtitle, times);

        // Send the title to your audience
        target.showTitle(titulo);
    }
}
