package fr.novus.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class NovusTitleScreen extends Screen {
    private static final int ORANGE = 0xFFFF7A00;
    private static final int DARK = 0xFF242424;
    private static final int LIGHT = 0xFFF7F7F7;

    public NovusTitleScreen() {
        super(Text.literal("Novus"));
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int y = this.height / 2 - 20;
        int w = 240;
        int h = 36;
        int gap = 44;

        addDrawableChild(ButtonWidget.builder(Text.literal("JOUER"), b ->
                client.setScreen(new SelectWorldScreen(this))).dimensions(center - w / 2, y, w, h).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("SERVEUR NOVUS"), b ->
                client.setScreen(new MultiplayerScreen(this))).dimensions(center - w / 2, y + gap, w, h).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("OPTIONS"), b ->
                client.setScreen(new OptionsScreen(this, client.options))).dimensions(center - w / 2, y + gap * 2, w, h).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("QUITTER"), b -> client.scheduleStop()).dimensions(center - w / 2, y + gap * 3, w, h).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, LIGHT);
        context.fill(0, 0, width, 8, ORANGE);

        String title = "NOVUS";
        String subtitle = "MINECRAFT MODDED";
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 155, ORANGE);
        context.drawCenteredTextWithShadow(textRenderer, subtitle, width / 2, height / 2 - 132, DARK);
        context.drawCenteredTextWithShadow(textRenderer, "1.20.1  •  FABRIC  •  CREATE", width / 2, height - 28, DARK);

        super.render(context, mouseX, mouseY, delta);
    }
}
