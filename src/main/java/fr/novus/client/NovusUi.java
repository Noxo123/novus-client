package fr.novus.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** Local Novus styling. It never changes Minecraft's GuiScale option. */
public final class NovusUi {
    /** Effective Novus design scale. This is rendering/layout only. */
    public static final float DESIGN_SCALE = 3.0F;

    private static final int PANEL = 0xFF11151B;
    private static final int HOVER = 0xFF1A2028;
    private static final int BORDER = 0xFF2B3440;
    private static final int TEXT = 0xFFF4F6F8;
    private static final int MUTED = 0xFF8993A1;
    private static final int ACCENT = 0xFFFF7A00;

    private NovusUi() {}

    public static void background(DrawContext ctx, int width, int height) {
        ctx.fill(0, 0, width, height, 0xFF080A0D);
    }

    public static void panel(DrawContext ctx, int x, int y, int right, int bottom, boolean accent) {
        ctx.fill(x, y, right, bottom, PANEL);
        ctx.fill(x, y, right, y + 1, BORDER);
        ctx.fill(x, bottom - 1, right, bottom, BORDER);
        ctx.fill(x, y, x + 1, bottom, BORDER);
        ctx.fill(right - 1, y, right, bottom, BORDER);
        if (accent) ctx.fill(x, y, x + 4, bottom, ACCENT);
    }

    public static void button(DrawContext ctx, Screen screen, int x, int y, int w, int h,
                              String label, int mouseX, int mouseY, boolean primary) {
        boolean hover = inside(x, y, x + w, y + h, mouseX, mouseY);
        int fill = primary ? (hover ? 0xFFFF8D2A : ACCENT) : (hover ? HOVER : PANEL);
        int border = primary ? ACCENT : BORDER;
        int text = primary ? 0xFF111111 : TEXT;

        ctx.fill(x, y, x + w, y + h, fill);
        ctx.fill(x, y, x + w, y + 2, border);
        ctx.fill(x, y + h - 2, x + w, y + h, border);
        ctx.fill(x, y, x + 2, y + h, border);
        ctx.fill(x + w - 2, y, x + w, y + h, border);

        int tw = screen.getTextRenderer().getWidth(label);
        ctx.drawTextWithShadow(screen.getTextRenderer(), Text.literal(label),
                x + (w - tw) / 2, y + (h - 8) / 2, text);
    }

    public static boolean inside(int left, int top, int right, int bottom, double x, double y) {
        return x >= left && x < right && y >= top && y < bottom;
    }

    public static int text() { return TEXT; }
    public static int muted() { return MUTED; }
    public static int accent() { return ACCENT; }
}
