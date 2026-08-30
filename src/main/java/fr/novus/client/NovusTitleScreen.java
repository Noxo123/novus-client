package fr.novus.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Main Novus screen. Everything visible here is custom; vanilla ButtonWidget is intentionally unused.
 * The 3x value is a local render/layout scale only and NEVER modifies Minecraft's GuiScale option.
 */
public final class NovusTitleScreen extends Screen {
    private static final float UI_SCALE = NovusUi.DESIGN_SCALE;
    private static final int SIDEBAR = 214;
    private static final int MARGIN = 28;
    private static final int GAP = 10;
    private static final int SUCCESS = 0xFF63D391;

    private static final String[] NAV = {"ACCUEIL", "MODPACK", "SERVEUR", "PARAMÈTRES"};

    private int page;
    private int playX, playY, playW, playH;
    private int secondaryX, secondaryY, secondaryW, secondaryH;
    private int quitX, quitY, quitW, quitH;
    private List<String> mods = List.of();

    public NovusTitleScreen() {
        super(Text.literal("Novus Client"));
    }

    @Override
    protected void init() {
        loadMods();
        layout();
    }

    private void loadMods() {
        List<String> loaded = new ArrayList<>();
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            String name = mod.getMetadata().getName();
            if (name == null || name.isBlank()) name = mod.getMetadata().getId();
            loaded.add(name);
        });
        loaded.sort(Comparator.comparing(String::toLowerCase));
        mods = List.copyOf(loaded);
    }

    private void layout() {
        // Work in a local Novus coordinate system. Minecraft's own GuiScale setting is untouched.
        int w = Math.max(320, Math.round(width / UI_SCALE));
        int h = Math.max(180, Math.round(height / UI_SCALE));
        int contentLeft = SIDEBAR + MARGIN;
        int contentRight = w - MARGIN;
        int available = Math.max(170, contentRight - contentLeft);
        int actionW = Math.min(112, Math.max(76, (available - GAP) / 2));
        int actionY = h - 30;

        playX = contentLeft;
        playY = actionY;
        playW = actionW;
        playH = 15;

        secondaryX = playX + actionW + GAP;
        secondaryY = actionY;
        secondaryW = actionW;
        secondaryH = 15;

        quitX = 12;
        quitY = h - 23;
        quitW = SIDEBAR - 24;
        quitH = 15;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        layout();
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        NovusUi.background(ctx, width, height);

        int localMouseX = Math.round(mouseX / UI_SCALE);
        int localMouseY = Math.round(mouseY / UI_SCALE);

        ctx.getMatrices().push();
        ctx.getMatrices().scale(UI_SCALE, UI_SCALE, 1.0F);
        drawScaled(ctx, localMouseX, localMouseY);
        ctx.getMatrices().pop();
    }

    private void drawScaled(DrawContext ctx, int mouseX, int mouseY) {
        int w = Math.round(width / UI_SCALE);
        int h = Math.round(height / UI_SCALE);

        ctx.fill(0, 0, w, h, 0xFF080A0D);
        ctx.fill(0, 0, SIDEBAR, h, 0xFF11151B);
        ctx.fill(SIDEBAR - 1, 0, SIDEBAR, h, 0xFF2B3440);

        drawSidebar(ctx, mouseX, mouseY, h);
        drawContent(ctx, mouseX, mouseY, w, h);
        drawFooter(ctx, w, h);
    }

    private void drawSidebar(DrawContext ctx, int mouseX, int mouseY, int h) {
        ctx.drawTextWithShadow(textRenderer, Text.literal("NOVUS"), 20, 18, NovusUi.text());
        ctx.drawText(textRenderer, Text.literal("CLIENT"), 21, 30, NovusUi.accent(), false);
        ctx.drawText(textRenderer, Text.literal("FABRIC 1.20.1"), 20, 43, NovusUi.muted(), false);

        int startY = 63;
        for (int i = 0; i < NAV.length; i++) {
            int top = startY + i * 35;
            boolean active = page == i;
            boolean hover = NovusUi.inside(10, top - 4, SIDEBAR - 10, top + 24, mouseX, mouseY);

            if (active || hover) {
                ctx.fill(10, top - 4, SIDEBAR - 10, top + 24,
                        active ? 0xFF222A35 : 0xFF1A2028);
            }
            if (active) ctx.fill(10, top - 4, 13, top + 24, NovusUi.accent());

            ctx.drawText(textRenderer, Text.literal("0" + (i + 1)), 18, top + 5,
                    active ? NovusUi.accent() : NovusUi.muted(), false);
            ctx.drawText(textRenderer, Text.literal(NAV[i]), 42, top + 5,
                    active ? NovusUi.text() : NovusUi.muted(), false);
        }

        NovusUi.button(ctx, this, quitX, quitY, quitW, quitH, "QUITTER", mouseX, mouseY, false);
    }

    private void drawContent(DrawContext ctx, int mouseX, int mouseY, int w, int h) {
        int x = SIDEBAR + MARGIN;
        int right = w - MARGIN;
        int top = 18;

        ctx.drawTextWithShadow(textRenderer, Text.literal(title()), x, top, NovusUi.text());
        ctx.drawText(textRenderer, Text.literal(subtitle()), x, top + 15, NovusUi.muted(), false);

        if (right <= x + 30) return;

        switch (page) {
            case 1 -> drawModpack(ctx, x, right, top + 43);
            case 2 -> drawServer(ctx, x, right, top + 43);
            case 3 -> drawSettings(ctx, x, right, top + 43);
            default -> drawHome(ctx, x, right, top + 43);
        }

        drawActions(ctx, mouseX, mouseY);
    }

    private void drawHome(DrawContext ctx, int x, int right, int top) {
        int bottom = Math.max(top + 65, Math.min(top + 112, Math.round(height / UI_SCALE) - 40));
        NovusUi.panel(ctx, x, top, right, bottom, true);

        ctx.drawTextWithShadow(textRenderer, Text.literal("BIENVENUE SUR NOVUS"), x + 18, top + 18, NovusUi.accent());
        ctx.drawTextWithShadow(textRenderer, Text.literal("Minecraft, sans le menu vanilla."), x + 18, top + 37, NovusUi.text());
        ctx.drawText(textRenderer, Text.literal("Un client simple, propre et rapide."), x + 18, top + 55, NovusUi.muted(), false);
        ctx.drawText(textRenderer, Text.literal("●  CLIENT PRÊT"), x + 18, bottom - 16, SUCCESS, false);
    }

    private void drawModpack(DrawContext ctx, int x, int right, int top) {
        int bottom = top + 112;
        NovusUi.panel(ctx, x, top, right, bottom, true);
        ctx.drawTextWithShadow(textRenderer, Text.literal("MODPACK"), x + 18, top + 18, NovusUi.accent());
        ctx.drawText(textRenderer, Text.literal("Mods chargés : " + mods.size()), x + 18, top + 37, NovusUi.text(), false);

        int y = top + 55;
        int max = Math.min(mods.size(), 4);
        for (int i = 0; i < max; i++) {
            ctx.drawText(textRenderer, Text.literal("• " + mods.get(i)), x + 18, y + i * 12, NovusUi.muted(), false);
        }
        if (mods.size() > max) {
            ctx.drawText(textRenderer, Text.literal("+ " + (mods.size() - max) + " autre(s)"),
                    x + 18, y + max * 12, NovusUi.accent(), false);
        }
    }

    private void drawServer(DrawContext ctx, int x, int right, int top) {
        int bottom = top + 112;
        NovusUi.panel(ctx, x, top, right, bottom, true);
        ctx.drawTextWithShadow(textRenderer, Text.literal("SERVEURS"), x + 18, top + 18, NovusUi.text());
        ctx.drawText(textRenderer, Text.literal("Accès direct au multijoueur Minecraft."), x + 18, top + 38, NovusUi.muted(), false);
        ctx.drawText(textRenderer, Text.literal("●  CONNEXION DISPONIBLE"), x + 18, top + 65, SUCCESS, false);
        ctx.drawText(textRenderer, Text.literal("Le bouton ouvre directement la liste des serveurs."),
                x + 18, top + 87, NovusUi.muted(), false);
    }

    private void drawSettings(DrawContext ctx, int x, int right, int top) {
        int bottom = top + 112;
        NovusUi.panel(ctx, x, top, right, bottom, false);
        ctx.drawTextWithShadow(textRenderer, Text.literal("PARAMÈTRES"), x + 18, top + 18, NovusUi.text());
        ctx.drawText(textRenderer, Text.literal("Vidéo • Audio • Contrôles • Langue"), x + 18, top + 40, NovusUi.muted(), false);
        ctx.drawText(textRenderer, Text.literal("Accessibilité et réglages Minecraft natifs."), x + 18, top + 58, NovusUi.muted(), false);
        ctx.drawText(textRenderer, Text.literal("Ton réglage GUI Scale n'est jamais modifié."), x + 18, top + 84, NovusUi.accent(), false);
    }

    private void drawActions(DrawContext ctx, int mouseX, int mouseY) {
        if (page == 0) {
            NovusUi.button(ctx, this, playX, playY, playW, playH, "JOUER", mouseX, mouseY, true);
            NovusUi.button(ctx, this, secondaryX, secondaryY, secondaryW, secondaryH, "SERVEURS", mouseX, mouseY, false);
        } else if (page == 1) {
            NovusUi.button(ctx, this, playX, playY, playW, playH, "JOUER", mouseX, mouseY, true);
            NovusUi.button(ctx, this, secondaryX, secondaryY, secondaryW, secondaryH, "RECHARGER", mouseX, mouseY, false);
        } else if (page == 2) {
            NovusUi.button(ctx, this, playX, playY, playW, playH, "SERVEURS", mouseX, mouseY, true);
            NovusUi.button(ctx, this, secondaryX, secondaryY, secondaryW, secondaryH, "ACCUEIL", mouseX, mouseY, false);
        } else {
            NovusUi.button(ctx, this, playX, playY, playW, playH, "OPTIONS", mouseX, mouseY, true);
            NovusUi.button(ctx, this, secondaryX, secondaryY, secondaryW, secondaryH, "ACCUEIL", mouseX, mouseY, false);
        }
    }

    private void drawFooter(DrawContext ctx, int w, int h) {
        String left = "NOVUS CLIENT  •  v0.1.0";
        String right = "GUI LOCAL 3x";
        ctx.drawText(textRenderer, Text.literal(left), SIDEBAR + MARGIN, h - 12, NovusUi.muted(), false);
        ctx.drawText(textRenderer, Text.literal(right), w - MARGIN - textRenderer.getWidth(right), h - 12, NovusUi.muted(), false);
    }

    private String title() {
        return NAV[page];
    }

    private String subtitle() {
        return switch (page) {
            case 1 -> "Gestion et aperçu de ton environnement moddé.";
            case 2 -> "Rejoins une partie sans repasser par le titre vanilla.";
            case 3 -> "Les réglages Minecraft restent accessibles depuis Novus.";
            default -> "Une interface Minecraft entièrement Novus.";
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);

        double x = mouseX / UI_SCALE;
        double y = mouseY / UI_SCALE;

        int navY = 63;
        for (int i = 0; i < NAV.length; i++) {
            int top = navY + i * 35;
            if (NovusUi.inside(10, top - 4, SIDEBAR - 10, top + 24, x, y)) {
                page = i;
                return true;
            }
        }

        if (NovusUi.inside(quitX, quitY, quitX + quitW, quitY + quitH, x, y)) {
            if (client != null) client.scheduleStop();
            return true;
        }

        if (NovusUi.inside(playX, playY, playX + playW, playY + playH, x, y)) {
            switch (page) {
                case 0, 1 -> openSingleplayer();
                case 2 -> openMultiplayer();
                case 3 -> openOptions();
                default -> { }
            }
            return true;
        }

        if (NovusUi.inside(secondaryX, secondaryY, secondaryX + secondaryW, secondaryY + secondaryH, x, y)) {
            switch (page) {
                case 0 -> openMultiplayer();
                case 1 -> loadMods();
                case 2, 3 -> page = 0;
                default -> { }
            }
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void openSingleplayer() {
        if (client != null) client.setScreen(new SelectWorldScreen(this));
    }

    private void openMultiplayer() {
        if (client != null) client.setScreen(new MultiplayerScreen(this));
    }

    private void openOptions() {
        if (client != null) client.setScreen(new OptionsScreen(this, client.options));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
