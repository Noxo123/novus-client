package fr.novus.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Système de design Novus — thème blanc/orange.
 * Ne modifie jamais le GUI Scale Minecraft de l'utilisateur.
 */
public final class NovusUi {

    /** Échelle de rendu Novus. Layout uniquement — le réglage Minecraft est intact. */
    public static final float DESIGN_SCALE = 3.0F;

    // ── Palette principale ────────────────────────────────────────────────────
    /** Fond de page */
    public static final int BG           = 0xFFF8F5F0;
    /** Fond sidebar */
    public static final int SIDEBAR_BG   = 0xFFFFFFFF;
    /** Fond panel/carte */
    public static final int PANEL_BG     = 0xFFFFFFFF;
    /** Fond panel légèrement teinté */
    public static final int PANEL_TINT   = 0xFFFFF7EE;
    /** Fond hover neutre */
    public static final int HOVER_BG     = 0xFFF0EDE8;
    /** Fond item actif */
    public static final int ACTIVE_BG    = 0xFFFFF0E0;
    /** Fond bouton secondaire hover */
    public static final int BTN_SEC_H    = 0xFFEDE9E3;

    // ── Bordures ──────────────────────────────────────────────────────────────
    public static final int BORDER       = 0xFFE2DDD6;
    public static final int BORDER_STRONG= 0xFFCEC9C0;
    public static final int BORDER_ACCENT= 0xFFFF7A00;

    // ── Texte ─────────────────────────────────────────────────────────────────
    public static final int TEXT         = 0xFF1A1714;
    public static final int TEXT_SEC     = 0xFF6B6560;
    public static final int TEXT_MUTED   = 0xFF9E998F;
    public static final int TEXT_ON_ACC  = 0xFFFFFFFF;

    // ── Accent orange ─────────────────────────────────────────────────────────
    public static final int ACCENT       = 0xFFFF7A00;
    public static final int ACCENT_H     = 0xFFE86E00;
    public static final int ACCENT_LIGHT = 0xFFFFF0E0;

    // ── Statuts ───────────────────────────────────────────────────────────────
    public static final int SUCCESS      = 0xFF16A34A;
    public static final int SUCCESS_BG   = 0xFFDCFCE7;
    public static final int WARNING      = 0xFFD97706;
    public static final int WARNING_BG   = 0xFFFEF3C7;
    public static final int DANGER       = 0xFFDC2626;
    public static final int DANGER_BG    = 0xFFFEE2E2;
    public static final int INFO         = 0xFF2563EB;
    public static final int INFO_BG      = 0xFFDBEAFE;

    private NovusUi() {}

    // ── Fond ──────────────────────────────────────────────────────────────────

    public static void background(DrawContext ctx, int width, int height) {
        ctx.fill(0, 0, width, height, BG);
    }

    // ── Panels / cartes ───────────────────────────────────────────────────────

    /** Panel avec bordure fine, optionnellement accentué sur le bord gauche. */
    public static void panel(DrawContext ctx, int x, int y, int x2, int y2, boolean accentBar) {
        ctx.fill(x, y, x2, y2, PANEL_BG);
        border(ctx, x, y, x2, y2, BORDER);
        if (accentBar) ctx.fill(x, y, x + 3, y2, ACCENT);
    }

    /** Panel coloré (fond teinté accent). */
    public static void panelAccent(DrawContext ctx, int x, int y, int x2, int y2) {
        ctx.fill(x, y, x2, y2, ACCENT_LIGHT);
        border(ctx, x, y, x2, y2, BORDER_ACCENT);
        ctx.fill(x, y, x + 3, y2, ACCENT);
    }

    /** Panel pour statut (fond vert/jaune/rouge). */
    public static void panelStatus(DrawContext ctx, int x, int y, int x2, int y2, int bg, int bd) {
        ctx.fill(x, y, x2, y2, bg);
        border(ctx, x, y, x2, y2, bd);
    }

    /** Séparateur horizontal 1px. */
    public static void divider(DrawContext ctx, int x, int y, int x2) {
        ctx.fill(x, y, x2, y + 1, BORDER);
    }

    // ── Boutons ───────────────────────────────────────────────────────────────

    /** Bouton primary (orange) ou secondary (gris clair). */
    public static void button(DrawContext ctx, TextRenderer tr,
                              int x, int y, int w, int h,
                              String label, int mx, int my, boolean primary) {
        boolean hov = inside(x, y, x + w, y + h, mx, my);
        int fill, bd, fg;
        if (primary) {
            fill = hov ? ACCENT_H : ACCENT;
            bd   = hov ? ACCENT_H : ACCENT;
            fg   = TEXT_ON_ACC;
        } else {
            fill = hov ? BTN_SEC_H : PANEL_BG;
            bd   = BORDER_STRONG;
            fg   = TEXT;
        }
        ctx.fill(x, y, x + w, y + h, fill);
        border(ctx, x, y, x + w, y + h, bd);
        int tw = tr.getWidth(label);
        ctx.drawText(tr, Text.literal(label), x + (w - tw) / 2, y + (h - 8) / 2, fg, false);
    }

    /** Bouton danger (rouge contour, fond blanc). */
    public static void buttonDanger(DrawContext ctx, TextRenderer tr,
                                    int x, int y, int w, int h,
                                    String label, int mx, int my) {
        boolean hov = inside(x, y, x + w, y + h, mx, my);
        ctx.fill(x, y, x + w, y + h, hov ? DANGER_BG : PANEL_BG);
        border(ctx, x, y, x + w, y + h, DANGER);
        int tw = tr.getWidth(label);
        ctx.drawText(tr, Text.literal(label), x + (w - tw) / 2, y + (h - 8) / 2, DANGER, false);
    }

    // ── Badges ────────────────────────────────────────────────────────────────

    public static void badge(DrawContext ctx, TextRenderer tr, int x, int y, String label, int bg, int fg) {
        int w = tr.getWidth(label) + 8;
        int h = 11;
        ctx.fill(x, y, x + w, y + h, bg);
        ctx.drawText(tr, Text.literal(label), x + 4, y + 2, fg, false);
    }

    // ── Barre de progression ──────────────────────────────────────────────────

    public static void progressBar(DrawContext ctx, int x, int y, int w, int h, float pct, int color) {
        ctx.fill(x, y, x + w, y + h, HOVER_BG);
        ctx.fill(x, y, x + Math.round(w * pct), y + h, color);
    }

    // ── Toggle ────────────────────────────────────────────────────────────────

    /** Dessine un toggle on/off. Gestion clic dans la screen appelante. */
    public static void toggle(DrawContext ctx, int x, int y, boolean on) {
        int trackW = 22, trackH = 10;
        int trackColor = on ? ACCENT : BORDER_STRONG;
        ctx.fill(x, y, x + trackW, y + trackH, trackColor);
        border(ctx, x, y, x + trackW, y + trackH, on ? ACCENT_H : BORDER_STRONG);
        int knobX = on ? x + trackW - 9 : x + 1;
        ctx.fill(knobX, y + 1, knobX + 8, y + 9, 0xFFFFFFFF);
    }

    // ── Stat card ─────────────────────────────────────────────────────────────

    public static void statCard(DrawContext ctx, TextRenderer tr,
                                int x, int y, int w, int h,
                                String value, String label, int valueColor) {
        ctx.fill(x, y, x + w, y + h, PANEL_BG);
        border(ctx, x, y, x + w, y + h, BORDER);
        ctx.drawText(tr, Text.literal(value), x + 10, y + 8, valueColor, false);
        ctx.drawText(tr, Text.literal(label), x + 10, y + 20, TEXT_MUTED, false);
    }

    // ── Fond row hover ────────────────────────────────────────────────────────

    public static void rowHover(DrawContext ctx, int x, int y, int x2, int y2, int mx, int my) {
        if (inside(x, y, x2, y2, mx, my)) ctx.fill(x, y, x2, y2, HOVER_BG);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static void border(DrawContext ctx, int x, int y, int x2, int y2, int col) {
        ctx.fill(x,      y,      x2,     y  + 1, col);
        ctx.fill(x,      y2 - 1, x2,     y2,     col);
        ctx.fill(x,      y,      x  + 1, y2,     col);
        ctx.fill(x2 - 1, y,      x2,     y2,     col);
    }

    public static boolean inside(int x, int y, int x2, int y2, double mx, double my) {
        return mx >= x && mx < x2 && my >= y && my < y2;
    }

    /** Texte centré horizontalement dans une zone. */
    public static void textCentered(DrawContext ctx, TextRenderer tr, String s, int cx, int y, int color) {
        ctx.drawText(tr, Text.literal(s), cx - tr.getWidth(s) / 2, y, color, false);
    }

    /** Texte aligné à droite. */
    public static void textRight(DrawContext ctx, TextRenderer tr, String s, int rx, int y, int color) {
        ctx.drawText(tr, Text.literal(s), rx - tr.getWidth(s), y, color, false);
    }

    // ── Accesseurs couleur (rétrocompat) ──────────────────────────────────────
    public static int text()   { return TEXT; }
    public static int muted()  { return TEXT_MUTED; }
    public static int accent() { return ACCENT; }
}
