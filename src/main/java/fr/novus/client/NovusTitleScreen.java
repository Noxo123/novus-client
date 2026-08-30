package fr.novus.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.text.Text;

/** Clean, fully custom Novus main menu. No vanilla ButtonWidget is used. */
public final class NovusTitleScreen extends Screen {
    private static final int BG = 0xFF090B0F;
    private static final int PANEL = 0xFF11151B;
    private static final int PANEL_HOVER = 0xFF181E27;
    private static final int PANEL_ACTIVE = 0xFF202733;
    private static final int LINE = 0xFF29313C;
    private static final int TEXT = 0xFFF4F6F8;
    private static final int MUTED = 0xFF8993A1;
    private static final int ACCENT = 0xFFFF7A00;
    private static final int SUCCESS = 0xFF65D391;

    private static final int SIDEBAR = 214;
    private static final int MARGIN = 34;
    private static final int GAP = 12;

    private int page;
    private int playX, playY, playW, playH;
    private int optionsX, optionsY, optionsW, optionsH;
    private int quitX, quitY, quitW, quitH;

    public NovusTitleScreen() {
        super(Text.literal("Novus Client"));
    }

    @Override
    protected void init() {
        // No vanilla widgets: every control is drawn and hit-tested by Novus.
        layout();
    }

    private void layout() {
        int contentLeft = SIDEBAR + MARGIN;
        int contentRight = width - MARGIN;
        int available = Math.max(240, contentRight - contentLeft);
        int actionW = Math.min(230, Math.max(150, (available - GAP) / 2));
        int actionY = height - 70;

        playX = contentLeft;
        playY = actionY;
        playW = actionW;
        playH = 42;

        optionsX = playX + actionW + GAP;
        optionsY = actionY;
        optionsW = actionW;
        optionsH = 42;

        quitW = SIDEBAR - 32;
        quitX = 16;
        quitY = height - 58;
        quitH = 38;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        layout();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, BG);
        drawSidebar(ctx, mouseX, mouseY);
        drawContent(ctx, mouseX, mouseY);
        drawActions(ctx, mouseX, mouseY);
        drawFooter(ctx);
    }

    private void drawSidebar(DrawContext ctx, int mouseX, int mouseY) {
        ctx.fill(0, 0, SIDEBAR, height, PANEL);
        ctx.fill(SIDEBAR - 1, 0, SIDEBAR, height, LINE);

        ctx.drawTextWithShadow(textRenderer, Text.literal("NOVUS"), 24, 25, TEXT);
        ctx.drawText(textRenderer, Text.literal("CLIENT"), 25, 43, ACCENT, false);
        ctx.drawText(textRenderer, Text.literal("MINECRAFT / FABRIC"), 24, 62, MUTED, false);

        String[] labels = {"ACCUEIL", "MODPACK", "SERVEUR", "PARAMÈTRES"};
        int y = 103;
        for (int i = 0; i < labels.length; i++) {
            int top = y + i * 50;
            boolean active = page == i;
            boolean hover = inside(16, top - 7, SIDEBAR - 16, top + 31, mouseX, mouseY);

            if (active || hover) {
                ctx.fill(16, top - 7, SIDEBAR - 16, top + 31, active ? PANEL_ACTIVE : PANEL_HOVER);
            }
            if (active) {
                ctx.fill(16, top - 7, 19, top + 31, ACCENT);
            }

            ctx.drawText(textRenderer, Text.literal("0" + (i + 1)), 30, top + 5,
                    active ? ACCENT : MUTED, false);
            ctx.drawText(textRenderer, Text.literal(labels[i]), 62, top + 5,
                    active ? TEXT : MUTED, false);
        }

        drawButton(ctx, quitX, quitY, quitW, quitH, "QUITTER", mouseX, mouseY, false);
    }

    private void drawContent(DrawContext ctx, int mouseX, int mouseY) {
        int x = SIDEBAR + MARGIN;
        int right = width - MARGIN;
        int top = 28;
        int bottom = height - 90;

        ctx.drawTextWithShadow(textRenderer, Text.literal(title()), x, top, TEXT);
        ctx.drawText(textRenderer, Text.literal(subtitle()), x, top + 22, MUTED, false);

        if (right <= x || bottom <= top + 50) return;

        switch (page) {
            case 1 -> drawModpack(ctx, x, right, top + 62);
            case 2 -> drawServer(ctx, x, right, top + 62);
            case 3 -> drawSettings(ctx, x, right, top + 62);
            default -> drawHome(ctx, x, right, top + 62);
        }
    }

    private void drawHome(DrawContext ctx, int x, int right, int top) {
        int h = Math.min(190, Math.max(120, height - top - 125));
        panel(ctx, x, top, right, top + h, true);

        ctx.drawTextWithShadow(textRenderer, Text.literal("BIENVENUE SUR NOVUS"), x + 26, top + 27, ACCENT);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Minecraft, sans le menu vanilla."), x + 26, top + 58, TEXT);
        ctx.drawText(textRenderer, Text.literal("Une interface pensée pour aller directement à l'essentiel."),
                x + 26, top + 86, MUTED, false);

        ctx.drawText(textRenderer, Text.literal("●  CLIENT PRÊT"), x + 26, top + h - 35, SUCCESS, false);
        ctx.drawText(textRenderer, Text.literal("FABRIC  •  1.20.1"), right - 118, top + h - 35, MUTED, false);
    }

    private void drawModpack(DrawContext ctx, int x, int right, int top) {
        int mid = x + (right - x - GAP) / 2;
        panel(ctx, x, top, mid, top + 128, false);
        panel(ctx, mid + GAP, top, right, top + 128, false);

        ctx.drawTextWithShadow(textRenderer, Text.literal("VERSION"), x + 22, top + 22, TEXT);
        ctx.drawText(textRenderer, Text.literal("Minecraft 1.20.1"), x + 22, top + 52, ACCENT, false);
        ctx.drawText(textRenderer, Text.literal("Fabric Loader"), x + 22, top + 78, MUTED, false);

        int rx = mid + GAP + 22;
        ctx.drawTextWithShadow(textRenderer, Text.literal("ENVIRONNEMENT"), rx, top + 22, TEXT);
        ctx.drawText(textRenderer, Text.literal("Fabric API"), rx, top + 52, MUTED, false);
        ctx.drawText(textRenderer, Text.literal("Mods prêts à charger"), rx, top + 78, MUTED, false);

        panel(ctx, x, top + 144, right, top + 220, false);
        ctx.drawText(textRenderer, Text.literal("Le client garde la gestion des mods native et stable."),
                x + 22, top + 174, MUTED, false);
    }

    private void drawServer(DrawContext ctx, int x, int right, int top) {
        panel(ctx, x, top, right, top + 154, true);
        ctx.drawTextWithShadow(textRenderer, Text.literal("SERVEURS"), x + 26, top + 27, TEXT);
        ctx.drawText(textRenderer, Text.literal("Ouvre la liste multijoueur Minecraft."),
                x + 26, top + 58, MUTED, false);
        ctx.drawText(textRenderer, Text.literal("Aucun écran vanilla ne reste affiché derrière Novus."),
                x + 26, top + 84, MUTED, false);
        ctx.drawText(textRenderer, Text.literal("●  MULTIJOUEUR"), x + 26, top + 120, SUCCESS, false);
    }

    private void drawSettings(DrawContext ctx, int x, int right, int top) {
        panel(ctx, x, top, right, top + 154, false);
        ctx.drawTextWithShadow(textRenderer, Text.literal("PARAMÈTRES MINECRAFT"), x + 26, top + 27, TEXT);
        ctx.drawText(textRenderer, Text.literal("Vidéo, audio, contrôles, langue et accessibilité."),
                x + 26, top + 59, MUTED, false);
        ctx.drawText(textRenderer, Text.literal("Les options natives restent disponibles sans afficher le titre vanilla."),
                x + 26, top + 86, MUTED, false);
    }

    private void drawActions(DrawContext ctx, int mouseX, int mouseY) {
        boolean showPlay = page == 0 || page == 2;
        boolean showOptions = page == 0 || page == 3;

        if (showPlay) {
            drawButton(ctx, playX, playY, playW, playH,
                    page == 2 ? "OUVRIR LES SERVEURS" : "JOUER", mouseX, mouseY, true);
        }
        if (showOptions) {
            drawButton(ctx, optionsX, optionsY, optionsW, optionsH,
                    page == 3 ? "OUVRIR LES OPTIONS" : "OPTIONS", mouseX, mouseY, false);
        }
    }

    private void drawButton(DrawContext ctx, int x, int y, int w, int h, String label,
                            int mouseX, int mouseY, boolean primary) {
        boolean hover = inside(x, y, x + w, y + h, mouseX, mouseY);
        int fill = primary ? (hover ? 0xFFFF8B26 : ACCENT) : (hover ? PANEL_HOVER : PANEL);
        int border = primary ? ACCENT : LINE;

        ctx.fill(x, y, x + w, y + h, fill);
        ctx.fill(x, y, x + w, y + 1, border);
        ctx.fill(x, y + h - 1, x + w, y + h, border);
        ctx.fill(x, y, x + 1, y + h, border);
        ctx.fill(x + w - 1, y, x + w, y + h, border);

        int textW = textRenderer.getWidth(label);
        int color = primary ? 0xFF111111 : TEXT;
        ctx.drawTextWithShadow(textRenderer, Text.literal(label), x + (w - textW) / 2, y + 14, color);
    }

    private void drawFooter(DrawContext ctx) {
        int x = SIDEBAR + MARGIN;
        ctx.drawText(textRenderer, Text.literal("NOVUS CLIENT  •  v0.1.0"), x, height - 31, MUTED, false);
        String status = "FABRIC 1.20.1";
        ctx.drawText(textRenderer, Text.literal(status), width - MARGIN - textRenderer.getWidth(status), height - 31, MUTED, false);
    }

    private void panel(DrawContext ctx, int left, int top, int right, int bottom, boolean accent) {
        ctx.fill(left, top, right, bottom, PANEL);
        ctx.fill(left, top, right, top + 1, LINE);
        ctx.fill(left, bottom - 1, right, bottom, LINE);
        ctx.fill(left, top, left + 1, bottom, LINE);
        ctx.fill(right - 1, top, right, bottom, LINE);
        if (accent) ctx.fill(left, top, left + 4, bottom, ACCENT);
    }

    private String title() {
        return switch (page) {
            case 1 -> "MODPACK";
            case 2 -> "SERVEUR";
            case 3 -> "PARAMÈTRES";
            default -> "ACCUEIL";
        };
    }

    private String subtitle() {
        return switch (page) {
            case 1 -> "Ton environnement moddé, propre et lisible.";
            case 2 -> "Rejoins directement une partie multijoueur.";
            case 3 -> "Configure Minecraft sans revenir au menu vanilla.";
            default -> "Une interface Minecraft entièrement Novus.";
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        int navY = 103;
        for (int i = 0; i < 4; i++) {
            int top = navY + i * 50;
            if (inside(16, top - 7, SIDEBAR - 16, top + 31, mouseX, mouseY)) {
                page = i;
                return true;
            }
        }

        if (inside(quitX, quitY, quitX + quitW, quitY + quitH, mouseX, mouseY)) {
            if (client != null) client.scheduleStop();
            return true;
        }

        if ((page == 0 || page == 2) && inside(playX, playY, playX + playW, playY + playH, mouseX, mouseY)) {
            openMultiplayer();
            return true;
        }

        if ((page == 0 || page == 3) && inside(optionsX, optionsY, optionsX + optionsW, optionsY + optionsH, mouseX, mouseY)) {
            openOptions();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void openMultiplayer() {
        if (client != null) client.setScreen(new MultiplayerScreen(this));
    }

    private void openOptions() {
        if (client != null) client.setScreen(new OptionsScreen(this, client.options));
    }

    private boolean inside(int left, int top, int right, int bottom, double x, double y) {
        return x >= left && x < right && y >= top && y < bottom;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
