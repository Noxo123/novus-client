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

/**
 * Écran principal Novus — 6 pages : Accueil, Modpack, Serveurs,
 * Paramètres, Profil, Changelog. Thème blanc/orange.
 */
public final class NovusTitleScreen extends Screen {

    // ── Constantes de layout ────────────────────────────────────────────────
    private static final float SCALE      = NovusUi.DESIGN_SCALE;
    private static final int   SIDEBAR_W  = 200;
    private static final int   TOPBAR_H  = 28;
    private static final int   MARGIN    = 20;
    private static final int   GAP       = 8;
    private static final int   ROW_H     = 14;

    // ── Navigation ──────────────────────────────────────────────────────────
    private static final String[] NAV_LABELS = {
        "Accueil", "Modpack", "Serveurs",
        "Parametres", "Profil", "Changelog"
    };
    private static final String[] NAV_ICONS = { "⌂", "⬡", "◈", "⚙", "◉", "✦" };

    // ── État ────────────────────────────────────────────────────────────────
    private int    page = 0;
    private List<String> mods = List.of();

    // ── Zones de clic (coordonnées design-scaled) ───────────────────────────
    private int btnPrimX, btnPrimY, btnPrimW, btnPrimH;
    private int btnSecX,  btnSecY,  btnSecW,  btnSecH;
    private int btnQuitX, btnQuitY, btnQuitW, btnQuitH;

    // ── Paramètres (toggles) ────────────────────────────────────────────────
    private boolean togFullscreen = false;
    private boolean togVsync      = false;
    private boolean togDebug      = false;
    private boolean togFpsCap     = true;

    public NovusTitleScreen() {
        super(Text.literal("Novus Client"));
    }

    // ── Cycle de vie ────────────────────────────────────────────────────────

    @Override
    protected void init() {
        loadMods();
        layout();
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        layout();
    }

    private void loadMods() {
        List<String> list = new ArrayList<>();
        FabricLoader.getInstance().getAllMods().forEach(mod -> {
            String name = mod.getMetadata().getName();
            list.add(name == null || name.isBlank()
                    ? mod.getMetadata().getId() : name);
        });
        list.sort(Comparator.comparing(String::toLowerCase));
        mods = List.copyOf(list);
    }

    private void layout() {
        int w = sw(), h = sh();
        int contentX = SIDEBAR_W + MARGIN;
        int contentW = w - contentX - MARGIN;
        int actionW  = Math.max(60, Math.min(90, (contentW - GAP) / 2));
        int actionY  = h - 22;

        btnPrimX = contentX;             btnPrimY = actionY;
        btnPrimW = actionW;              btnPrimH = 13;
        btnSecX  = contentX + actionW + GAP; btnSecY = actionY;
        btnSecW  = actionW;              btnSecH  = 13;
        btnQuitX = 10; btnQuitY = h - 18;
        btnQuitW = SIDEBAR_W - 20; btnQuitH = 13;
    }

    // ── Rendu principal ─────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int lmx = Math.round(mouseX / SCALE);
        int lmy = Math.round(mouseY / SCALE);
        ctx.getMatrices().push();
        ctx.getMatrices().scale(SCALE, SCALE, 1f);
        NovusUi.background(ctx, sw(), sh());
        drawSidebar(ctx, lmx, lmy);
        drawTopbar(ctx, lmx, lmy);
        drawContent(ctx, lmx, lmy);
        drawActions(ctx, lmx, lmy);
        drawFooter(ctx, lmx, lmy);
        ctx.getMatrices().pop();
    }

    // ── Sidebar ─────────────────────────────────────────────────────────────

    private void drawSidebar(DrawContext ctx, int mx, int my) {
        int h = sh();
        // fond sidebar blanc, bordure droite
        ctx.fill(0, 0, SIDEBAR_W, h, NovusUi.SIDEBAR_BG);
        ctx.fill(SIDEBAR_W - 1, 0, SIDEBAR_W, h, NovusUi.BORDER);

        TextRenderer tr = textRenderer;

        // Logo
        ctx.fill(0, 0, SIDEBAR_W, 40, NovusUi.PANEL_BG);
        ctx.fill(0, 39, SIDEBAR_W, 40, NovusUi.BORDER);
        ctx.drawText(tr, Text.literal("NOVUS"), 14, 10, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("CLIENT"), 14, 21, NovusUi.TEXT_SEC, false);
        ctx.drawText(tr, Text.literal("Fabric 1.20.1"), 14, 32, NovusUi.TEXT_MUTED, false);

        // Navigation
        int ny = 50;
        for (int i = 0; i < NAV_LABELS.length; i++) {
            int itemY = ny + i * 26;
            boolean active = page == i;
            boolean hover  = !active && NovusUi.inside(0, itemY - 2, SIDEBAR_W - 1, itemY + 20, mx, my);

            if (active) {
                ctx.fill(0, itemY - 2, SIDEBAR_W - 1, itemY + 20, NovusUi.ACTIVE_BG);
                ctx.fill(0, itemY - 2, 3, itemY + 20, NovusUi.ACCENT);
            } else if (hover) {
                ctx.fill(0, itemY - 2, SIDEBAR_W - 1, itemY + 20, NovusUi.HOVER_BG);
            }

            int iconColor = active ? NovusUi.ACCENT  : NovusUi.TEXT_MUTED;
            int lblColor  = active ? NovusUi.TEXT     : NovusUi.TEXT_SEC;
            ctx.drawText(tr, Text.literal(NAV_ICONS[i]), 12, itemY + 4, iconColor, false);
            ctx.drawText(tr, Text.literal(NAV_LABELS[i]), 26, itemY + 4, lblColor, false);
        }

        // Divider avant quitter
        ctx.fill(10, h - 30, SIDEBAR_W - 10, h - 29, NovusUi.BORDER);
        NovusUi.button(ctx, tr, btnQuitX, btnQuitY, btnQuitW, btnQuitH,
                "Quitter", mx, my, false);
    }

    // ── Topbar ──────────────────────────────────────────────────────────────

    private void drawTopbar(DrawContext ctx, int mx, int my) {
        int w = sw();
        ctx.fill(SIDEBAR_W, 0, w, TOPBAR_H, NovusUi.PANEL_BG);
        ctx.fill(SIDEBAR_W, TOPBAR_H - 1, w, TOPBAR_H, NovusUi.BORDER);

        TextRenderer tr = textRenderer;
        int tx = SIDEBAR_W + MARGIN;
        ctx.drawText(tr, Text.literal(NAV_LABELS[page]), tx, 6, NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal(subtitle()), tx + textRenderer.getWidth(NAV_LABELS[page]) + 10,
                7, NovusUi.TEXT_MUTED, false);

        // Pill statut
        String pill = "● Pret";
        int pw = textRenderer.getWidth(pill) + 10;
        int px = w - MARGIN - pw;
        ctx.fill(px, 7, px + pw, 18, NovusUi.SUCCESS_BG);
        ctx.drawText(tr, Text.literal(pill), px + 5, 10, NovusUi.SUCCESS, false);
    }

    // ── Dispatch contenu ────────────────────────────────────────────────────

    private void drawContent(DrawContext ctx, int mx, int my) {
        int cx = SIDEBAR_W + MARGIN;
        int cy = TOPBAR_H + MARGIN;
        int cw = sw() - cx - MARGIN;
        int ch = sh() - cy - MARGIN - 20; // espace boutons en bas

        switch (page) {
            case 0 -> drawHome(ctx, mx, my, cx, cy, cw, ch);
            case 1 -> drawModpack(ctx, mx, my, cx, cy, cw, ch);
            case 2 -> drawServers(ctx, mx, my, cx, cy, cw, ch);
            case 3 -> drawSettings(ctx, mx, my, cx, cy, cw, ch);
            case 4 -> drawProfile(ctx, mx, my, cx, cy, cw, ch);
            case 5 -> drawChangelog(ctx, mx, my, cx, cy, cw, ch);
        }
    }

    // ── Page 0 : Accueil ────────────────────────────────────────────────────

    private void drawHome(DrawContext ctx, int mx, int my, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;

        // Hero card
        int heroH = Math.min(80, h - 110);
        NovusUi.panelAccent(ctx, x, y, x + w, y + heroH);
        ctx.drawText(tr, Text.literal("NOVUS CLIENT"), x + 12, y + 8, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("Minecraft, sans le menu vanilla."), x + 12, y + 20, NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal("Un client propre, rapide et extensible."), x + 12, y + 31, NovusUi.TEXT_SEC, false);

        // Barre de séparation dans le hero
        ctx.fill(x + 12, y + heroH - 20, x + w - 12, y + heroH - 19, NovusUi.BORDER_ACCENT);
        ctx.drawText(tr, Text.literal("● Client pret"), x + 12, y + heroH - 14, NovusUi.SUCCESS, false);
        NovusUi.textRight(ctx, tr, "Fabric 1.20.1", x + w - 12, y + heroH - 14, NovusUi.TEXT_MUTED);

        // Stat cards — rangée
        int gy = y + heroH + GAP;
        int cw3 = (w - GAP * 2) / 3;
        NovusUi.statCard(ctx, tr, x,             gy, cw3, 36, String.valueOf(mods.size()), "MODS ACTIFS",   NovusUi.TEXT);
        NovusUi.statCard(ctx, tr, x + cw3 + GAP, gy, cw3, 36, "3",  "SERVEURS FAVORIS", NovusUi.TEXT);
        NovusUi.statCard(ctx, tr, x + (cw3 + GAP) * 2, gy, w - (cw3 + GAP) * 2, 36, "42h", "TEMPS DE JEU", NovusUi.ACCENT);

        // Deux panels inférieurs
        int py = gy + 36 + GAP;
        int half = (w - GAP) / 2;
        int panH = Math.max(50, sh() - py - 40);

        // Activité récente
        NovusUi.panel(ctx, x, py, x + half, py + panH, false);
        ctx.drawText(tr, Text.literal("ACTIVITE RECENTE"), x + 10, py + 8, NovusUi.TEXT_MUTED, false);
        NovusUi.divider(ctx, x + 1, py + 17, x + half - 1);
        String[][] acts = {{"⊕ Survie #3", "il y a 2h"}, {"⊞ novus.play.fr", "hier"}, {"↓ Sodium mis a jour", "il y a 3j"}};
        for (int i = 0; i < acts.length && i < 3; i++) {
            int ry = py + 22 + i * 14;
            ctx.drawText(tr, Text.literal(acts[i][0]), x + 10, ry, NovusUi.TEXT_SEC, false);
            NovusUi.textRight(ctx, tr, acts[i][1], x + half - 8, ry, NovusUi.TEXT_MUTED);
        }

        // Performances
        int px2 = x + half + GAP;
        NovusUi.panel(ctx, px2, py, x + w, py + panH, false);
        ctx.drawText(tr, Text.literal("PERFORMANCES"), px2 + 10, py + 8, NovusUi.TEXT_MUTED, false);
        NovusUi.divider(ctx, px2 + 1, py + 17, x + w - 1);
        int barW = w - half - GAP - 20;
        ctx.drawText(tr, Text.literal("RAM"), px2 + 10, py + 24, NovusUi.TEXT_SEC, false);
        NovusUi.textRight(ctx, tr, "2.1 / 4 Go", x + w - 8, py + 24, NovusUi.ACCENT);
        NovusUi.progressBar(ctx, px2 + 10, py + 34, barW, 4, 0.52f, NovusUi.ACCENT);
        ctx.drawText(tr, Text.literal("FPS moyen"), px2 + 10, py + 44, NovusUi.TEXT_SEC, false);
        NovusUi.textRight(ctx, tr, "118 fps", x + w - 8, py + 44, NovusUi.SUCCESS);
        NovusUi.progressBar(ctx, px2 + 10, py + 54, barW, 0.78f, 6, NovusUi.SUCCESS);
    }

    // ── Page 1 : Modpack ────────────────────────────────────────────────────

    private void drawModpack(DrawContext ctx, int mx, int my, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;

        // Stat cards
        int cw2 = (w - GAP) / 2;
        NovusUi.statCard(ctx, tr, x,          y, cw2, 30, String.valueOf(mods.size()), "MODS TOTAL",    NovusUi.TEXT);
        NovusUi.statCard(ctx, tr, x + cw2 + GAP, y, w - cw2 - GAP, 30, String.valueOf(mods.size()), "COMPATIBLES", NovusUi.SUCCESS);

        // Liste des mods
        int listY = y + 30 + GAP;
        int listH = h - 30 - GAP;
        NovusUi.panel(ctx, x, listY, x + w, listY + listH, false);
        ctx.drawText(tr, Text.literal("MODS INSTALLES"), x + 10, listY + 8, NovusUi.TEXT_MUTED, false);
        NovusUi.divider(ctx, x + 1, listY + 17, x + w - 1);

        // Entrées de mods avec alternance de badge
        String[] badgeLabels = {"Core", "Perf", "UI", "Reseau", "Beta"};
        int[][]  badgeColors = {
            {NovusUi.INFO_BG,    NovusUi.INFO},
            {NovusUi.SUCCESS_BG, NovusUi.SUCCESS},
            {NovusUi.WARNING_BG, NovusUi.WARNING},
            {NovusUi.ACCENT_LIGHT, NovusUi.ACCENT},
            {NovusUi.DANGER_BG,  NovusUi.DANGER},
        };

        int maxRows = (listH - 20) / ROW_H;
        int shown   = Math.min(mods.size(), maxRows);
        for (int i = 0; i < shown; i++) {
            int ry = listY + 20 + i * ROW_H;
            NovusUi.rowHover(ctx, x + 1, ry, x + w - 1, ry + ROW_H - 1, mx, my);
            ctx.drawText(tr, Text.literal(mods.get(i)), x + 10, ry + 3, NovusUi.TEXT_SEC, false);
            if (i < 5) {
                int bi = i % badgeLabels.length;
                NovusUi.badge(ctx, tr, x + w - 42, ry + 2, badgeLabels[bi], badgeColors[bi][0], badgeColors[bi][1]);
            }
        }
        if (mods.size() > maxRows) {
            ctx.drawText(tr, Text.literal("+ " + (mods.size() - maxRows) + " autres..."),
                    x + 10, listY + 20 + maxRows * ROW_H + 3, NovusUi.TEXT_MUTED, false);
        }
    }

    // ── Page 2 : Serveurs ───────────────────────────────────────────────────

    private void drawServers(DrawContext ctx, int mx, int my, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;
        int half = (w - GAP) / 2;
        int cardH = Math.max(52, Math.min(70, (h - GAP - 30) / 2));

        // Section favoris label
        ctx.drawText(tr, Text.literal("FAVORIS"), x, y + 2, NovusUi.TEXT_MUTED, false);

        // Serveur 1 — Novus SMP (featured)
        int c1x = x, c1y = y + 12;
        NovusUi.panelAccent(ctx, c1x, c1y, c1x + half, c1y + cardH);
        ctx.drawText(tr, Text.literal("Novus SMP"), c1x + 12, c1y + 8, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("play.novus.fr"), c1x + 12, c1y + 18, NovusUi.TEXT_SEC, false);
        drawPingBars(ctx, c1x + 12, c1y + cardH - 18, 34, NovusUi.SUCCESS);
        ctx.drawText(tr, Text.literal("34ms"), c1x + 30, c1y + cardH - 16, NovusUi.SUCCESS, false);
        ctx.drawText(tr, Text.literal("87 / 200 joueurs"), c1x + 12, c1y + cardH - 6, NovusUi.TEXT_MUTED, false);

        // Serveur 2 — Hypixel
        int c2x = x + half + GAP;
        NovusUi.panel(ctx, c2x, c1y, c2x + w - half - GAP, c1y + cardH, false);
        ctx.drawText(tr, Text.literal("Hypixel"), c2x + 12, c1y + 8, NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal("mc.hypixel.net"), c2x + 12, c1y + 18, NovusUi.TEXT_SEC, false);
        drawPingBars(ctx, c2x + 12, c1y + cardH - 18, 87, NovusUi.WARNING);
        ctx.drawText(tr, Text.literal("87ms"), c2x + 30, c1y + cardH - 16, NovusUi.WARNING, false);
        ctx.drawText(tr, Text.literal("43 781 joueurs"), c2x + 12, c1y + cardH - 6, NovusUi.TEXT_MUTED, false);

        // Ligne 2 de serveurs
        int row2Y = c1y + cardH + GAP;

        // Serveur 3 — VanillaMC
        NovusUi.panel(ctx, x, row2Y, x + half, row2Y + cardH, false);
        ctx.drawText(tr, Text.literal("VanillaMC"), x + 12, row2Y + 8, NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal("vanilla.fr"), x + 12, row2Y + 18, NovusUi.TEXT_SEC, false);
        drawPingBars(ctx, x + 12, row2Y + cardH - 18, 22, NovusUi.SUCCESS);
        ctx.drawText(tr, Text.literal("22ms"), x + 30, row2Y + cardH - 16, NovusUi.SUCCESS, false);
        ctx.drawText(tr, Text.literal("12 / 50 joueurs"), x + 12, row2Y + cardH - 6, NovusUi.TEXT_MUTED, false);

        // Carte Ajouter
        boolean hAddCard = NovusUi.inside(c2x, row2Y, c2x + w - half - GAP, row2Y + cardH, mx, my);
        ctx.fill(c2x, row2Y, c2x + w - half - GAP, row2Y + cardH,
                hAddCard ? NovusUi.HOVER_BG : NovusUi.PANEL_BG);
        ctx.fill(c2x,       row2Y,          c2x + w - half - GAP, row2Y + 1,          NovusUi.BORDER);
        ctx.fill(c2x,       row2Y + cardH - 1, c2x + w - half - GAP, row2Y + cardH,   NovusUi.BORDER);
        ctx.fill(c2x,       row2Y,          c2x + 1,               row2Y + cardH,      NovusUi.BORDER);
        ctx.fill(c2x + w - half - GAP - 1, row2Y, c2x + w - half - GAP, row2Y + cardH, NovusUi.BORDER);
        NovusUi.textCentered(ctx, tr, "+ Ajouter un serveur",
                c2x + (w - half - GAP) / 2, row2Y + cardH / 2 - 4,
                hAddCard ? NovusUi.ACCENT : NovusUi.TEXT_MUTED);

        // Serveurs publics
        int pubY = row2Y + cardH + MARGIN;
        ctx.drawText(tr, Text.literal("SERVEURS PUBLICS"), x, pubY, NovusUi.TEXT_MUTED, false);
        NovusUi.panel(ctx, x, pubY + 10, x + w, pubY + 46, false);
        String[][] pubs = {{"Mineplex", "us.mineplex.com", "204ms"}, {"Cubecraft EU", "eu.cubecraft.net", "61ms"}};
        for (int i = 0; i < pubs.length; i++) {
            int py = pubY + 15 + i * 16;
            NovusUi.rowHover(ctx, x + 1, py - 2, x + w - 1, py + 12, mx, my);
            ctx.drawText(tr, Text.literal(pubs[i][0]), x + 10, py, NovusUi.TEXT_SEC, false);
            ctx.drawText(tr, Text.literal(pubs[i][1]), x + 70, py, NovusUi.TEXT_MUTED, false);
            NovusUi.textRight(ctx, tr, pubs[i][2], x + w - 8, py,
                    i == 0 ? NovusUi.DANGER : NovusUi.WARNING);
        }
    }

    /** Dessine 4 barres de ping colorées selon la latence. */
    private void drawPingBars(DrawContext ctx, int x, int y, int ms, int color) {
        int[] heights = {3, 5, 7, 9};
        int good = ms < 80;
        int med  = ms < 150;
        for (int i = 0; i < 4; i++) {
            boolean lit = good > 0 || (med > 0 && i < 3) || i < 2;
            int barColor = lit ? color : NovusUi.BORDER_STRONG;
            ctx.fill(x + i * 4, y + (9 - heights[i]), x + i * 4 + 3, y + 9, barColor);
        }
    }

    // ── Page 3 : Paramètres ─────────────────────────────────────────────────

    private void drawSettings(DrawContext ctx, int mx, int my, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;
        int half = (w - GAP) / 2;

        // Colonne gauche — Vidéo
        int col1X = x, col2X = x + half + GAP;
        ctx.drawText(tr, Text.literal("VIDEO"), col1X, y + 2, NovusUi.TEXT_MUTED, false);
        NovusUi.panel(ctx, col1X, y + 12, col1X + half, y + 120, false);
        drawSettingRow(ctx, tr, mx, my, col1X, y + 12, half, 0, "Luminosite", "70%", null);
        drawSettingRow(ctx, tr, mx, my, col1X, y + 12, half, 1, "Rendu", "12 chunks", null);
        drawSettingRow(ctx, tr, mx, my, col1X, y + 12, half, 2, "Plein ecran", null, togFullscreen);
        drawSettingRow(ctx, tr, mx, my, col1X, y + 12, half, 3, "Limite FPS", "120 fps", null);
        drawSettingRow(ctx, tr, mx, my, col1X, y + 12, half, 4, "VSync", null, togVsync);

        // Colonne droite — Audio
        ctx.drawText(tr, Text.literal("AUDIO"), col2X, y + 2, NovusUi.TEXT_MUTED, false);
        NovusUi.panel(ctx, col2X, y + 12, col2X + w - half - GAP, y + 72, false);
        drawSettingRow(ctx, tr, mx, my, col2X, y + 12, w - half - GAP, 0, "Volume general", "80%", null);
        drawSettingRow(ctx, tr, mx, my, col2X, y + 12, w - half - GAP, 1, "Musique", "40%", null);
        drawSettingRow(ctx, tr, mx, my, col2X, y + 12, w - half - GAP, 2, "Environnement", "100%", null);

        // Novus
        ctx.drawText(tr, Text.literal("NOVUS"), col2X, y + 76, NovusUi.TEXT_MUTED, false);
        NovusUi.panel(ctx, col2X, y + 86, col2X + w - half - GAP, y + 130, false);
        drawSettingRow(ctx, tr, mx, my, col2X, y + 86, w - half - GAP, 0, "Accent orange", null, true);
        drawSettingRow(ctx, tr, mx, my, col2X, y + 86, w - half - GAP, 1, "Overlay debug", null, togDebug);
        drawSettingRow(ctx, tr, mx, my, col2X, y + 86, w - half - GAP, 2, "Limiter FPS", null, togFpsCap);

        // Controles
        int ctrlY = y + 134;
        ctx.drawText(tr, Text.literal("CONTROLES"), col1X, ctrlY, NovusUi.TEXT_MUTED, false);
        NovusUi.panel(ctx, col1X, ctrlY + 10, x + w, ctrlY + 28, false);
        ctx.drawText(tr, Text.literal("Raccourcis Minecraft natifs — geres par le jeu directement."),
                col1X + 10, ctrlY + 18, NovusUi.TEXT_MUTED, false);
        int btnCtrlX = x + w - 80;
        NovusUi.button(ctx, tr, btnCtrlX, ctrlY + 13, 72, 12, "Ouvrir", mx, my, false);

        // Note GUI Scale
        int noteY = y + h - 16;
        ctx.fill(x, noteY, x + w, noteY + 14, NovusUi.ACCENT_LIGHT);
        ctx.fill(x, noteY, x + 3, noteY + 14, NovusUi.ACCENT);
        ctx.drawText(tr, Text.literal("Ton reglage GUI Scale Minecraft n'est jamais modifie par Novus."),
                x + 8, noteY + 3, NovusUi.ACCENT, false);
    }

    /** Dessine une ligne de paramètre avec valeur texte ou toggle. */
    private void drawSettingRow(DrawContext ctx, TextRenderer tr, int mx, int my,
                                int panX, int panY, int panW, int rowIdx,
                                String label, String valueStr, Boolean toggleState) {
        int rowY = panY + 1 + rowIdx * 19;
        int rowH = 18;
        if (rowIdx > 0) NovusUi.divider(ctx, panX + 1, rowY, panX + panW - 1);
        NovusUi.rowHover(ctx, panX + 1, rowY, panX + panW - 1, rowY + rowH, mx, my);
        ctx.drawText(tr, Text.literal(label), panX + 10, rowY + 5, NovusUi.TEXT_SEC, false);
        if (toggleState != null) {
            NovusUi.toggle(ctx, panX + panW - 32, rowY + 4, toggleState);
        } else if (valueStr != null) {
            NovusUi.textRight(ctx, tr, valueStr, panX + panW - 8, rowY + 5, NovusUi.ACCENT);
        }
    }

    // ── Page 4 : Profil ─────────────────────────────────────────────────────

    private void drawProfile(DrawContext ctx, int mx, int my, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;

        // Header profil
        int headerH = Math.min(60, h / 3);
        NovusUi.panel(ctx, x, y, x + w, y + headerH, false);

        // Avatar (carré coloré — Minecraft ne fournit pas la tête en GUI custom)
        ctx.fill(x + 10, y + 8, x + 42, y + 8 + 40, NovusUi.ACCENT_LIGHT);
        ctx.fill(x + 10, y + 8, x + 42, y + 8 + 40, NovusUi.ACCENT_LIGHT);
        ctx.fill(x + 10, y + 8, x + 12, y + 48, NovusUi.ACCENT);
        ctx.drawText(tr, Text.literal("M"), x + 22, y + 22, NovusUi.ACCENT, false);

        ctx.drawText(tr, Text.literal("Maitre"), x + 50, y + 10, NovusUi.TEXT, false);
        ctx.drawText(tr, Text.literal("uuid: a1b2-c3d4-e5f6-7890"), x + 50, y + 22, NovusUi.TEXT_MUTED, false);

        // Stats inline
        int[] statVals = {42, mods.size(), 3};
        String[] statNames = {"Heures jouees", "Mods actifs", "Serveurs"};
        int[] statColors = {NovusUi.TEXT, NovusUi.ACCENT, NovusUi.INFO};
        int sw3 = (w - 50 - GAP * 2) / 3;
        for (int i = 0; i < 3; i++) {
            int sx = x + 50 + i * (sw3 + GAP);
            ctx.drawText(tr, Text.literal(String.valueOf(statVals[i])), sx, y + 36, statColors[i], false);
            ctx.drawText(tr, Text.literal(statNames[i]), sx, y + 46, NovusUi.TEXT_MUTED, false);
        }

        // Stats panel + Badges
        int gy = y + headerH + GAP;
        int half = (w - GAP) / 2;

        NovusUi.panel(ctx, x, gy, x + half, gy + 70, false);
        ctx.drawText(tr, Text.literal("STATISTIQUES"), x + 10, gy + 8, NovusUi.TEXT_MUTED, false);
        NovusUi.divider(ctx, x + 1, gy + 17, x + half - 1);
        String[][] stats = {{"Sessions ce mois", "18"}, {"Monde le + joue", "Survie #3"}, {"FPS moyen", "118 fps"}};
        for (int i = 0; i < stats.length; i++) {
            int sy = gy + 22 + i * 14;
            ctx.drawText(tr, Text.literal(stats[i][0]), x + 10, sy, NovusUi.TEXT_SEC, false);
            NovusUi.textRight(ctx, tr, stats[i][1], x + half - 8, sy, NovusUi.TEXT);
        }

        int bx = x + half + GAP;
        NovusUi.panel(ctx, bx, gy, bx + w - half - GAP, gy + 70, false);
        ctx.drawText(tr, Text.literal("BADGES"), bx + 10, gy + 8, NovusUi.TEXT_MUTED, false);
        NovusUi.divider(ctx, bx + 1, gy + 17, bx + w - half - GAP - 1);
        String[][] badges = {{"⬟ Builder de l'extreme", "10k blocs poses"}, {"⚡ Speedrunner", "Dragon < 1h"}};
        for (int i = 0; i < badges.length; i++) {
            int by = gy + 22 + i * 22;
            ctx.drawText(tr, Text.literal(badges[i][0]), bx + 10, by, NovusUi.TEXT_SEC, false);
            ctx.drawText(tr, Text.literal(badges[i][1]), bx + 10, by + 10, NovusUi.TEXT_MUTED, false);
        }
        // Badge en cours
        ctx.drawText(tr, Text.literal("◌ Explorateur — 74%"), bx + 10, gy + 22 + 44, NovusUi.TEXT_MUTED, false);
        NovusUi.progressBar(ctx, bx + 10, gy + 22 + 54, w - half - GAP - 20, 4, 0.74f, NovusUi.ACCENT);

        // Historique sessions
        int histY = gy + 70 + GAP;
        ctx.drawText(tr, Text.literal("HISTORIQUE DES SESSIONS"), x, histY, NovusUi.TEXT_MUTED, false);
        NovusUi.panel(ctx, x, histY + 10, x + w, histY + 58, false);
        String[][] sess = {
            {"Survie #3", "Singleplayer · 2h14", "Auj. 14h32"},
            {"novus.play.fr", "Multijoueur · 1h05", "Hier 20h10"},
            {"Creative Test", "Singleplayer · 23min", "Hier 16h40"}
        };
        for (int i = 0; i < sess.length; i++) {
            int sy = histY + 15 + i * 15;
            NovusUi.rowHover(ctx, x + 1, sy - 2, x + w - 1, sy + 12, mx, my);
            ctx.drawText(tr, Text.literal(sess[i][0]), x + 10, sy, NovusUi.TEXT_SEC, false);
            ctx.drawText(tr, Text.literal(sess[i][1]), x + 75, sy, NovusUi.TEXT_MUTED, false);
            NovusUi.textRight(ctx, tr, sess[i][2], x + w - 8, sy, NovusUi.TEXT_MUTED);
        }
    }

    // ── Page 5 : Changelog ──────────────────────────────────────────────────

    private void drawChangelog(DrawContext ctx, int mx, int my, int x, int y, int w, int h) {
        TextRenderer tr = textRenderer;

        // Version actuelle
        NovusUi.panelAccent(ctx, x, y, x + w, y + 10 + 7 * 12 + 8);
        ctx.drawText(tr, Text.literal("v0.1.0"), x + 12, y + 8, NovusUi.ACCENT, false);
        ctx.drawText(tr, Text.literal("30 aout 2026  ·  Version initiale"), x + 50, y + 9, NovusUi.TEXT_MUTED, false);
        NovusUi.badge(ctx, tr, x + w - 52, y + 7, "Actuelle", NovusUi.SUCCESS_BG, NovusUi.SUCCESS);
        NovusUi.divider(ctx, x + 12, y + 19, x + w - 12);

        String[][] entries = {
            {"Nouveau",  NovusUi.INFO_BG    + "," + NovusUi.INFO,    "Ecran titre Novus remplacant l'ecran vanilla"},
            {"Perf",     NovusUi.SUCCESS_BG + "," + NovusUi.SUCCESS,  "Sodium + Iris : meilleures performances"},
            {"Nouveau",  NovusUi.INFO_BG    + "," + NovusUi.INFO,    "Navigation 6 pages : Accueil, Modpack, Serveurs..."},
            {"Refactor", NovusUi.WARNING_BG + "," + NovusUi.WARNING,  "Systeme de design NovusUi blanc/orange"},
            {"Nouveau",  NovusUi.INFO_BG    + "," + NovusUi.INFO,    "Affichage dynamique des mods Fabric charges"},
            {"Fix",      NovusUi.SUCCESS_BG + "," + NovusUi.SUCCESS,  "GUI Scale Minecraft preserve (jamais modifie)"},
        };
        for (int i = 0; i < entries.length; i++) {
            int ey = y + 22 + i * 12;
            String[] col = entries[i][1].split(",");
            NovusUi.badge(ctx, tr, x + 12, ey, entries[i][0], Integer.parseInt(col[0]), Integer.parseInt(col[1]));
            ctx.drawText(tr, Text.literal(entries[i][2]), x + 55, ey + 2, NovusUi.TEXT_SEC, false);
        }

        // A venir
        int nextY = y + 10 + 7 * 12 + 8 + GAP;
        NovusUi.panel(ctx, x, nextY, x + w, nextY + 6 * 12 + 20, false);
        ctx.drawText(tr, Text.literal("v0.2.0"), x + 12, nextY + 8, NovusUi.TEXT_SEC, false);
        ctx.drawText(tr, Text.literal("En developpement"), x + 55, nextY + 9, NovusUi.TEXT_MUTED, false);
        NovusUi.divider(ctx, x + 12, nextY + 18, x + w - 12);
        String[] upcoming = {
            "Profil joueur avec statistiques et badges",
            "Gestion des shaders depuis l'interface Novus",
            "Ping en temps reel pour la liste de serveurs",
            "Theme personnalisable (couleur d'accent)",
            "Integration Discord Rich Presence"
        };
        for (int i = 0; i < upcoming.length; i++) {
            int uy = nextY + 22 + i * 12;
            NovusUi.badge(ctx, tr, x + 12, uy, "Prevu", NovusUi.WARNING_BG, NovusUi.WARNING);
            ctx.drawText(tr, Text.literal(upcoming[i]), x + 55, uy + 2, NovusUi.TEXT_MUTED, false);
        }
    }

    // ── Boutons d'action ────────────────────────────────────────────────────

    private void drawActions(DrawContext ctx, int mx, int my) {
        TextRenderer tr = textRenderer;
        String primary, secondary;
        switch (page) {
            case 1  -> { primary = "Jouer";     secondary = "Recharger"; }
            case 2  -> { primary = "Rejoindre"; secondary = "Accueil"; }
            case 3  -> { primary = "Options";   secondary = "Accueil"; }
            case 4  -> { primary = "Jouer";     secondary = "Compte"; }
            case 5  -> { primary = "Jouer";     secondary = "Accueil"; }
            default -> { primary = "Jouer";     secondary = "Serveurs"; }
        }
        NovusUi.button(ctx, tr, btnPrimX, btnPrimY, btnPrimW, btnPrimH, primary, mx, my, true);
        NovusUi.button(ctx, tr, btnSecX,  btnSecY,  btnSecW,  btnSecH,  secondary, mx, my, false);
    }

    // ── Footer ──────────────────────────────────────────────────────────────

    private void drawFooter(DrawContext ctx, int mx, int my) {
        int w = sw(), h = sh();
        ctx.fill(SIDEBAR_W, h - 12, w, h, NovusUi.PANEL_BG);
        ctx.fill(SIDEBAR_W, h - 13, w, h - 12, NovusUi.BORDER);
        TextRenderer tr = textRenderer;
        ctx.drawText(tr, Text.literal("NOVUS CLIENT  ·  v0.1.0"), SIDEBAR_W + MARGIN, h - 9, NovusUi.TEXT_MUTED, false);
        NovusUi.textRight(ctx, tr, "Scale x3", w - MARGIN, h - 9, NovusUi.TEXT_MUTED);
    }

    // ── Interactions souris ─────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        double x = mouseX / SCALE, y = mouseY / SCALE;

        // Navigation sidebar
        int ny = 50;
        for (int i = 0; i < NAV_LABELS.length; i++) {
            int iy = ny + i * 26;
            if (NovusUi.inside(0, iy - 2, SIDEBAR_W - 1, iy + 20, x, y)) {
                page = i;
                return true;
            }
        }

        // Bouton quitter
        if (NovusUi.inside(btnQuitX, btnQuitY, btnQuitX + btnQuitW, btnQuitY + btnQuitH, x, y)) {
            if (client != null) client.scheduleStop();
            return true;
        }

        // Bouton primaire
        if (NovusUi.inside(btnPrimX, btnPrimY, btnPrimX + btnPrimW, btnPrimY + btnPrimH, x, y)) {
            switch (page) {
                case 0, 1, 4, 5 -> openSingleplayer();
                case 2           -> openMultiplayer();
                case 3           -> openOptions();
            }
            return true;
        }

        // Bouton secondaire
        if (NovusUi.inside(btnSecX, btnSecY, btnSecX + btnSecW, btnSecY + btnSecH, x, y)) {
            switch (page) {
                case 0 -> openMultiplayer();
                case 1 -> loadMods();
                case 2, 3, 5 -> page = 0;
            }
            return true;
        }

        // Toggles paramètres
        if (page == 3) handleSettingsClick(x, y);

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleSettingsClick(double x, double y) {
        int cx = SIDEBAR_W + MARGIN;
        int cy = TOPBAR_H + MARGIN;
        int half = (sw() - cx - MARGIN - GAP) / 2;
        int toggleX = cx + half - 32;

        // Plein ecran (row 2 col gauche)
        int togY2 = cy + 12 + 1 + 2 * 19 + 4;
        if (NovusUi.inside(toggleX, togY2, toggleX + 24, togY2 + 12, x, y)) { togFullscreen = !togFullscreen; return; }
        // VSync (row 4 col gauche)
        int togY4 = cy + 12 + 1 + 4 * 19 + 4;
        if (NovusUi.inside(toggleX, togY4, toggleX + 24, togY4 + 12, x, y)) { togVsync = !togVsync; return; }

        int col2X = cx + half + GAP;
        int togX2 = col2X + (half - GAP) - 32;
        // Overlay debug (row 1 Novus)
        int dnY1 = cy + 86 + 1 + 19 + 4;
        if (NovusUi.inside(togX2, dnY1, togX2 + 24, dnY1 + 12, x, y)) { togDebug = !togDebug; return; }
        // Limiter FPS (row 2 Novus)
        int dnY2 = cy + 86 + 1 + 2 * 19 + 4;
        if (NovusUi.inside(togX2, dnY2, togX2 + 24, dnY2 + 12, x, y)) { togFpsCap = !togFpsCap; }
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private int sw() { return Math.max(320, Math.round(width  / SCALE)); }
    private int sh() { return Math.max(180, Math.round(height / SCALE)); }

    private String subtitle() {
        return switch (page) {
            case 1 -> "Gestion de ton environnement modde.";
            case 2 -> "Rejoins une partie sans passer par le menu vanilla.";
            case 3 -> "Reglages Minecraft accessibles depuis Novus.";
            case 4 -> "Ton compte et tes statistiques.";
            case 5 -> "Notes de version Novus Client.";
            default -> "Une interface Minecraft entierement Novus.";
        };
    }

    private void openSingleplayer() { if (client != null) client.setScreen(new SelectWorldScreen(this)); }
    private void openMultiplayer()  { if (client != null) client.setScreen(new MultiplayerScreen(this)); }
    private void openOptions()      { if (client != null) client.setScreen(new OptionsScreen(this, client.options)); }

    @Override public boolean shouldCloseOnEsc() { return false; }
}
