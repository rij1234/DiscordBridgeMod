package com.rij1234.DiscordBridgeMod;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;


public class DiscordReceiver {
    public DiscordApi api = new DiscordApiBuilder()
            .setAllIntentsExcept(Intent.GUILD_PRESENCES)
            .setToken(Config.DiscordBotToken)
            .login().join();

    public void sendUsrMsg(String msg, String username, String wh_url){
        try{
            DiscordWebhook webhook = new DiscordWebhook(wh_url);
            Optional<Server> server = api.getServerById(Config.ServerId);
            Collection<User> users = server.get().getMembersByNickname(username);
            System.out.println(users);
            if(users.isEmpty()){
                webhook.setAvatarUrl("https://www.levistrauss.com/wp-content/uploads/2020/05/Black_Box.png");
            } else {
                webhook.setAvatarUrl(users.iterator().next().getAvatar().getUrl().toString());
            }
            webhook.setContent(msg);
            webhook.setUsername(username);
            webhook.execute(); //Handle exception
        } catch(IOException e){
        }
    }
    public void run(DiscordBridgeMod main) {
        // Log the bot in


        // Add a listener which answers with "Pong!" if someone writes "!ping"
        api.addMessageCreateListener(event -> {
            if(event.getMessageAuthor().isBotUser() || event.getMessageAuthor().isWebhook()) return;
            if (event.getMessageContent().equalsIgnoreCase("!ping")) {
                event.getChannel().sendMessage("Pong!");
            }
            long channel_id = Config.channel_id;
            if(event.getChannel().getId() == channel_id){
                main.sendToAll("<" + event.getMessageAuthor().getName() + ">" + event.getMessageContent());
            }
        });
    }

}