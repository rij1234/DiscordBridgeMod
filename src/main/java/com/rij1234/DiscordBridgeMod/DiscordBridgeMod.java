package com.rij1234.DiscordBridgeMod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.util.Locale;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("discordbridgemod")
public class DiscordBridgeMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    private static String wh_url = Config.WebhookUrl;
    public MinecraftServer server;
    private DiscordReceiver bot = new DiscordReceiver();


    public void sendToAll(String message){
        StringTextComponent text = new StringTextComponent(message);
        server.getPlayerList().broadcastMessage(text, ChatType.SYSTEM, Util.NIL_UUID);
    }
    public DiscordBridgeMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code

        LOGGER.info("HELLO FROM PREINIT");
    }


    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        bot.run(this);
        server = event.getServer();
        LOGGER.info("HELLO from server starting");
    }

    @SubscribeEvent
    public void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event){
        server = event.getPlayer().getServer();
        PlayerEntity player = event.getPlayer();
        ITextComponent display_name = player.getName();
        String name = display_name.getContents();
        try{
            DiscordWebhook webhook = new DiscordWebhook(wh_url);
            webhook.setContent(":green_square:" + name + " has joined the server!");
            webhook.setAvatarUrl("https://www.solidbackgrounds.com/images/1920x1080/1920x1080-bright-green-solid-color-background.jpg");
            webhook.setUsername("Server");
            webhook.execute();
        } catch(IOException e){LOGGER.error(e.getMessage());}
    }

    @SubscribeEvent
    public void PlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event){
        server = event.getPlayer().getServer();
        PlayerEntity player = event.getPlayer();
        ITextComponent display_name = player.getName();
        String name = display_name.getContents();
        try{
            DiscordWebhook webhook = new DiscordWebhook(wh_url);
            webhook.setContent(":red_square:" + name + " has left the server!");
            webhook.setAvatarUrl("https://upload.wikimedia.org/wikipedia/commons/1/10/Red_Color.jpg");
            webhook.setUsername("Server");
            webhook.execute();
        } catch(IOException e){LOGGER.error(e.getMessage());}
    }

    @SubscribeEvent
    public void onPlayerChatEvent(ServerChatEvent event){
        String msg = event.getMessage();
        String username = event.getUsername();
        if(msg.toLowerCase().contains("@here") || msg.toLowerCase().contains("@everyone")){
            return;
        }
        if(username.toLowerCase().startsWith("@here") || username.toLowerCase().contains("@everyone")){
            return;
        }
        bot.sendUsrMsg(msg, username, wh_url);
    }
}
