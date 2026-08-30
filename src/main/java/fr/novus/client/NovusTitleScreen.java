package fr.novus.client;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Interface principale Novus. L'echelle 3x est uniquement visuelle. */
public final class NovusTitleScreen extends Screen {
    private static final float SCALE = NovusUi.DESIGN_SCALE;
    private static final int SIDEBAR = 200, TOPBAR = 28, MARGIN = 20, GAP = 8;
    private static final String[] NAV = {"Accueil", "Modpack", "Serveurs", "Parametres", "Profil", "Changelog"};
    private static final String[] ICON = {"+", "#", "*", "=", "@", "!"};

    private int page;
    private List<String> mods = List.of();
    private int primaryX, secondaryX, actionY, actionW;
    private int quitY;

    public NovusTitleScreen() { super(Text.literal("Novus Client")); }

    @Override protected void init() { loadMods(); layout(); }
    @Override public void resize(MinecraftClient client, int width, int height) { super.resize(client, width, height); layout(); }

    private int sw() { return Math.max(320, Math.round(width / SCALE)); }
    private int sh() { return Math.max(180, Math.round(height / SCALE)); }

    private void layout() {
        int w = sw(), h = sh();
        int contentX = SIDEBAR + MARGIN;
        actionW = Math.max(60, Math.min(100, (w - contentX - MARGIN - GAP) / 2));
        primaryX = contentX;
        secondaryX = contentX + actionW + GAP;
        actionY = h - 24;
        quitY = h - 19;
    }

    private void loadMods() {
        List<String> list = new ArrayList<>();
        FabricLoader.getInstance().getAllMods().forEach(m -> {
            String n = m.getMetadata().getName();
            list.add(n == null || n.isBlank() ? m.getMetadata().getId() : n);
        });
        list.sort(Comparator.comparing(String::toLowerCase));
        mods = List.copyOf(list);
    }

    @Override public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int mx = Math.round(mouseX / SCALE), my = Math.round(mouseY / SCALE);
        ctx.getMatrices().push();
        ctx.getMatrices().scale(SCALE, SCALE, 1f);
        int w = sw(), h = sh();
        NovusUi.background(ctx, w, h);
        drawSidebar(ctx, mx, my, h);
        drawTopbar(ctx, w);
        drawPage(ctx, mx, my, w, h);
        drawActions(ctx, mx, my);
        drawFooter(ctx, w, h);
        ctx.getMatrices().pop();
    }

    private void drawSidebar(DrawContext ctx, int mx, int my, int h) {
        TextRenderer tr = textRenderer;
        ctx.fill(0, 0, SIDEBAR, h, NovusUi.SIDEBAR_BG);
        ctx.fill(SIDEBAR - 1, 0, SIDEBAR, h, NovusUi.BORDER);
        ctx.fill(0, 0, SIDEBAR, 40, NovusUi.PANEL_BG);
        ctx.drawText(tr, Text.literal("NOVUS"), 14, 9, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("CLIENT"), 14, 20, NovusUi.TEXT_SEC, false);
        ctx.drawText(tr, Text.literal("Fabric 1.20.1"), 14, 31, NovusUi.TEXT_MUTED, false);
        for (int i = 0; i < NAV.length; i++) {
            int y = 50 + i * 26;
            boolean active = page == i;
            boolean hover = NovusUi.inside(0, y - 2, SIDEBAR - 1, y + 20, mx, my);
            if (active) {
                ctx.fill(0, y - 2, SIDEBAR - 1, y + 20, NovusUi.ACTIVE_BG);
                ctx.fill(0, y - 2, 3, y + 20, NovusUi.ACCENT);
            } else if (hover) ctx.fill(0, y - 2, SIDEBAR - 1, y + 20, NovusUi.HOVER_BG);
            ctx.drawText(tr, Text.literal(ICON[i]), 12, y + 4, active ? NovusUi.ACCENT : NovusUi.TEXT_MUTED, false);
            ctx.drawText(tr, Text.literal(NAV[i]), 28, y + 4, active ? NovusUi.TEXT : NovusUi.TEXT_SEC, false);
        }
        NovusUi.button(ctx, tr, 10, quitY, SIDEBAR - 20, 13, "Quitter", mx, my, false);
    }

    private void drawTopbar(DrawContext ctx, int w) {
        TextRenderer tr = textRenderer;
        ctx.fill(SIDEBAR, 0, w, TOPBAR, NovusUi.PANEL_BG);
        ctx.fill(SIDEBAR, TOPBAR - 1, w, TOPBAR, NovusUi.BORDER);
        ctx.drawText(tr, Text.literal(NAV[page]), SIDEBAR + MARGIN, 7, NovusUi.TEXT, false);
        String status = "Pret";
        int pw = tr.getWidth(status) + 12;
        ctx.fill(w - MARGIN - pw, 7, w - MARGIN, 19, NovusUi.SUCCESS_BG);
        ctx.drawText(tr, Text.literal(status), w - MARGIN - pw + 6, 10, NovusUi.SUCCESS, false);
    }

    private void drawPage(DrawContext ctx, int mx, int my, int w, int h) {
        int x = SIDEBAR + MARGIN, y = TOPBAR + MARGIN, cw = w - x - MARGIN, ch = h - y - 42;
        switch (page) {
            case 1 -> drawModpack(ctx, x, y, cw, ch);
            case 2 -> drawServers(ctx, x, y, cw, ch);
            case 3 -> drawSettings(ctx, x, y, cw, ch);
            case 4 -> drawProfile(ctx, x, y, cw, ch);
            case 5 -> drawChangelog(ctx, x, y, cw, ch);
            default -> drawHome(ctx, x, y, cw, ch);
        }
    }

    private void drawHome(DrawContext ctx, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;
        NovusUi.panelAccent(ctx, x, y, x + w, y + 72);
        ctx.drawText(tr, Text.literal("NOVUS CLIENT"), x + 12, y + 10, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("Minecraft, sans le menu vanilla."), x + 12, y + 23, NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal("Interface propre, rapide et extensible."), x + 12, y + 35, NovusUi.TEXT_SEC, false);
        ctx.drawText(tr, Text.literal("● CLIENT PRET"), x + 12, y + 55, NovusUi.SUCCESS, false);
        int cy = y + 80, cw = (w - GAP) / 2;
        NovusUi.statCard(ctx, tr, x, cy, cw, 38, String.valueOf(mods.size()), "MODS ACTIFS", NovusUi.TEXT);
        NovusUi.statCard(ctx, tr, x + cw + GAP, cy, w - cw - GAP, 38, "3", "SERVEURS FAVORIS", NovusUi.ACCENT);
        NovusUi.panel(ctx, x, cy + 46, x + w, Math.min(y + h, cy + 116), false);
        ctx.drawText(tr, Text.literal("GUI LOCAL 3x"), x + 12, cy + 56, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("Le GUI Scale Minecraft de l'utilisateur n'est jamais modifie."), x + 12, cy + 70, NovusUi.TEXT_SEC, false);
    }

    private void drawModpack(DrawContext ctx, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;
        NovusUi.statCard(ctx, tr, x, y, w, 32, String.valueOf(mods.size()), "MODS CHARGES", NovusUi.ACCENT);
        int ly = y + 40;
        NovusUi.panel(ctx, x, ly, x + w, y + h, false);
        ctx.drawText(tr, Text.literal("MODS INSTALLES"), x + 10, ly + 8, NovusUi.TEXT_MUTED, false);
        int max = Math.min(mods.size(), Math.max(1, (h - 28) / 14));
        for (int i = 0; i < max; i++) ctx.drawText(tr, Text.literal("• " + mods.get(i)), x + 10, ly + 22 + i * 14, NovusUi.TEXT_SEC, false);
    }

    private void drawServers(DrawContext ctx, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;
        int half = (w - GAP) / 2;
        serverCard(ctx, tr, x, y, half, "Novus SMP", "play.novus.fr", "34ms", NovusUi.SUCCESS, true);
        serverCard(ctx, tr, x + half + GAP, y, w - half - GAP, "Hypixel", "mc.hypixel.net", "87ms", NovusUi.WARNING, false);
        serverCard(ctx, tr, x, y + 58, half, "VanillaMC", "vanilla.fr", "22ms", NovusUi.SUCCESS, false);
        NovusUi.panel(ctx, x + half + GAP, y + 58, x + w, y + 116, false);
        NovusUi.textCentered(ctx, tr, "+ Ajouter un serveur", x + half + GAP + (w - half - GAP) / 2, y + 82, NovusUi.ACCENT);
    }

    private void serverCard(DrawContext ctx, TextRenderer tr, int x, int y, int w, String name, String addr, String ping, int color, boolean featured) {
        NovusUi.panel(ctx, x, y, x + w, y + 52, featured);
        ctx.drawText(tr, Text.literal(name), x + 10, y + 8, featured ? NovusUi.ACCENT : NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal(addr), x + 10, y + 20, NovusUi.TEXT_SEC, false);
        ctx.drawText(tr, Text.literal(ping), x + 10, y + 36, color, false);
    }

    private void drawSettings(DrawContext ctx, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;
        NovusUi.panel(ctx, x, y, x + w, y + 130, false);
        ctx.drawText(tr, Text.literal("PARAMETRES MINECRAFT"), x + 10, y + 10, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("Video • Audio • Controles • Langue • Accessibilite"), x + 10, y + 28, NovusUi.TEXT_SEC, false);
        ctx.drawText(tr, Text.literal("Le GUI Scale reste entierement gere par Minecraft."), x + 10, y + 52, NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal("Novus utilise seulement une echelle de rendu locale 3x."), x + 10, y + 66, NovusUi.ACCENT, false);
    }

    private void drawProfile(DrawContext ctx, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;
        NovusUi.panel(ctx, x, y, x + w, y + 80, false);
        ctx.fill(x + 10, y + 10, x + 50, y + 50, NovusUi.ACCENT_LIGHT);
        ctx.drawText(tr, Text.literal("N"), x + 24, y + 23, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("Profil joueur"), x + 60, y + 12, NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal("Mods actifs : " + mods.size()), x + 60, y + 28, NovusUi.TEXT_SEC, false);
        ctx.drawText(tr, Text.literal("Temps de jeu : 42h"), x + 60, y + 42, NovusUi.TEXT_SEC, false);
    }

    private void drawChangelog(DrawContext ctx, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;
        NovusUi.panelAccent(ctx, x, y, x + w, y + 110);
        ctx.drawText(tr, Text.literal("v0.1.0"), x + 12, y + 10, NovusUi.ACCENT, false);
        String[] lines = {"Nouveau menu Novus", "Navigation sidebar", "Affichage dynamique des mods", "GUI Scale utilisateur preserve", "Acces direct aux mondes et serveurs"};
        for (int i = 0; i < lines.length; i++) ctx.drawText(tr, Text.literal("• " + lines[i]), x + 12, y + 28 + i * 14, NovusUi.TEXT_SEC, false);
    }

    private void drawActions(DrawContext ctx, int mx, int my) {
        TextRenderer tr = textRenderer;
        String primary = page == 2 ? "Rejoindre" : page == 3 ? "Options" : "Jouer";
        String secondary = page == 1 ? "Recharger" : page == 2 || page == 3 ? "Accueil" : "Serveurs";
        NovusUi.button(ctx, tr, primaryX, actionY, actionW, 13, primary, mx, my, true);
        NovusUi.button(ctx, tr, secondaryX, actionY, actionW, 13, secondary, mx, my, false);
    }

    private void drawFooter(DrawContext ctx, int w, int h) {
        TextRenderer tr = textRenderer;
        ctx.fill(SIDEBAR, h - 12, w, h, NovusUi.PANEL_BG);
        ctx.drawText(tr, Text.literal("NOVUS CLIENT  •  v0.1.0"), SIDEBAR + MARGIN, h - 9, NovusUi.TEXT_MUTED, false);
        NovusUi.textRight(ctx, tr, "Scale x3", w - MARGIN, h - 9, NovusUi.TEXT_MUTED);
    }

    @Override public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        double x = mouseX / SCALE, y = mouseY / SCALE;
        for (int i = 0; i < NAV.length; i++) {
            int ny = 50 + i * 26;
            if (NovusUi.inside(0, ny - 2, SIDEBAR - 1, ny + 20, x, y)) { page = i; return true; }
        }
        if (NovusUi.inside(10, quitY, SIDEBAR - 10, quitY + 13, x, y)) { if (client != null) client.scheduleStop(); return true; }
        if (NovusUi.inside(primaryX, actionY, primaryX + actionW, actionY + 13, x, y)) {
            if (page == 2) openMultiplayer(); else if (page == 3) openOptions(); else openSingleplayer();
            return true;
        }
        if (NovusUi.inside(secondaryX, actionY, secondaryX + actionW, actionY + 13, x, y)) {
            if (page == 0) openMultiplayer(); else if (page == 1) loadMods(); else page = 0;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void openSingleplayer() { if (client != null) client.setScreen(new SelectWorldScreen(this)); }
    private void openMultiplayer() { if (client != null) client.setScreen(new MultiplayerScreen(this)); }
    private void openOptions() { if (client != null) client.setScreen(new OptionsScreen(this, client.options)); }
    @Override public boolean shouldCloseOnEsc() { return false; }
}
