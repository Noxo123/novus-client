package fr.novus.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.TitleScreen;

/** Novus client entrypoint. Only replaces the vanilla title screen. */
public final class NovusClient implements ClientModInitializer {
    private boolean replacingTitleScreen;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (replacingTitleScreen) return;

            // Never replace a null screen: null is the normal state while
            // the player is inside a world. Doing so creates a screen loop.
            if (client.currentScreen instanceof TitleScreen
                    && !(client.currentScreen instanceof NovusTitleScreen)) {
                replacingTitleScreen = true;
                client.execute(() -> {
                    try {
                        if (client.currentScreen instanceof TitleScreen) {
                            client.setScreen(new NovusTitleScreen());
                        }
                    } finally {
                        replacingTitleScreen = false;
                    }
                });
            }
        });
    }
}
