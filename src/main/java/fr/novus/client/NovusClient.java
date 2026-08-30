package fr.novus.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screen.TitleScreen;

/** Entry point for Novus. Replaces the vanilla title screen before it is shown. */
public final class NovusClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen && !(screen instanceof NovusTitleScreen)) {
                client.setScreen(new NovusTitleScreen());
            }
        });
    }
}
