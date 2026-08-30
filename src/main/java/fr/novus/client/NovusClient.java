package fr.novus.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.TitleScreen;

/** Entry point for the Novus client-side experience. */
public final class NovusClient implements ClientModInitializer {
    private boolean switchingScreen;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (switchingScreen || !(client.currentScreen instanceof TitleScreen)) {
                return;
            }

            if (client.currentScreen instanceof NovusTitleScreen) {
                return;
            }

            switchingScreen = true;
            client.execute(() -> {
                try {
                    if (client.currentScreen instanceof TitleScreen
                            && !(client.currentScreen instanceof NovusTitleScreen)) {
                        client.setScreen(new NovusTitleScreen());
                    }
                } finally {
                    switchingScreen = false;
                }
            });
        });
    }
}
