package fr.novus.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;

public class NovusClient implements ClientModInitializer {
    private boolean opened;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen == null && !opened) {
                client.setScreen(new NovusTitleScreen());
                opened = true;
            }
            if (client.currentScreen instanceof TitleScreen) {
                client.setScreen(new NovusTitleScreen());
            }
        });
    }
}
