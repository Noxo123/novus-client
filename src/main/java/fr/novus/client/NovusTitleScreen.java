package fr.novus.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Novus main menu.
 *
 * <p>The screen deliberately stays lightweight: no textures, no background
 * resources and no per-frame allocations outside of the small UI strings.
 */
public final class NovusTitleScreen extends Screen {
    private static final int SIDEBAR_WIDTH = 224;
    private static final int CONTENT_MARGIN = 32;
    private static final int CARD_GAP = 12;

    private static final int BACKGROUND = 0xFF0D0F12;
    private static final int SIDEBAR = 0xFF14171C;
    private static final int CARD = 0xFF191D23;
    private static final int CARD_ALT = 0xFF1E232A;
    private static final int BORDER = 0xFF2A3038;
    private static final int TEXT = 0xFFF2F4F7;
    private static final int MUTED = 0xFF8E96A3;
    private static final int ACCENT = 0xFFFF7A00;
    private static final int SUCCESS = 0xFF62D48B;

    private static final String[] NAV_LABELS = {
            "ACCUEIL",
            "MODPACK",
            "SERVEUR",
            "PARAMÈTRES"
    };

    private int selectedPage;
    private ButtonWidget primaryButton;
    private ButtonWidget secondaryButton;
    private ButtonWidget quitButton;

    public NovusTitleScreen() {
        super(Text.literal("Novus Client"));
    }

    @Override
    protected void init() {
        clearChildren();

        int contentX = SIDEBAR_WIDTH + CONTENT_MARGIN;
        int buttonWidth = 220;
        int buttonX = Math.min(contentX, width - buttonWidth - CONTENT_MARGIN);
        int buttonY = height - 74;

        primaryButton = addDrawableChild(ButtonWidget.builder(Text.literal("JOUER"), button -> openServer())
                .dimensions(buttonX, buttonY, buttonWidth, 20)
                .build());

        secondaryButton = addDrawableChild(ButtonWidget.builder(Text.literal("OPTIONS"), button -> openOptions())
                .dimensions(buttonX + buttonWidth + CARD_GAP, buttonY, buttonWidth, 20)
                .build());

        quitButton = addDrawableChild(ButtonWidget.builder(Text.literal("QUITTER"), button -> quitGame())
                .dimensions(20, height - 44, SIDEBAR_WIDTH - 40, 20)
                .build());

        refreshActions();
    }

    private void refreshActions() {
        boolean home = selectedPage == 0;
        boolean server = selectedPage == 2;
        boolean settings = selectedPage == 3;

        primaryButton.visible = home || server;
        secondaryButton.visible = home || settings;

        primaryButton.setMessage(Text.literal(server ? "VOIR LES SERVEURS" : "JOUER"));
        secondaryButton.setMessage(Text.literal(settings ? "OUVRIR LES OPTIONS" : "OPTIONS"));
    }

    private void selectPage(int page) {
        if (page < 0 || page >= NAV_LABELS.length || selectedPage == page) {
            return;
        }

        selectedPage = page;
        refreshActions();
    }

    private void openServer() {
        if (client != null) {
            client.setScreen(new MultiplayerScreen(this));
        }
    }

    private void openOptions() {
        if (client != null) {
            client.setScreen(new OptionsScreen(this, client.options));
        }
    }

    private void quitGame() {
        if (client != null) {
            client.scheduleStop();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, BACKGROUND);
        drawSidebar(context, mouseX, mouseY);
        drawHeader(context);
        drawPage(context);
        drawFooter(context);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawSidebar(DrawContext context, int mouseX, int mouseY) {
        context.fill(0, 0, SIDEBAR_WIDTH, height, SIDEBAR);
        context.fill(SIDEBAR_WIDTH - 1, 0, SIDEBAR_WIDTH, height, BORDER);

        context.drawTextWithShadow(textRenderer, Text.literal("NOVUS"), 24, 25, TEXT);
        context.drawText(textRenderer, Text.literal("CLIENT"), 25, 42, ACCENT, false);

        int startY = 92;
        for (int i = 0; i < NAV_LABELS.length; i++) {
            int y = startY + i * 48;
            boolean active = selectedPage == i;
            boolean hovered = mouseX >= 16 && mouseX < SIDEBAR_WIDTH - 16
                    && mouseY >= y - 6 && mouseY < y + 30;

            if (active || hovered) {
                context.fill(16, y - 6, SIDEBAR_WIDTH - 16, y + 30,
                        active ? CARD_ALT : 0xFF191D22);
            }
            if (active) {
                context.fill(16, y - 6, 19, y + 30, ACCENT);
            }

            context.drawTextWithShadow(textRenderer, Text.literal(pageNumber(i)), 30, y + 5,
                    active ? ACCENT : MUTED);
            context.drawText(textRenderer, Text.literal(NAV_LABELS[i]), 58, y + 5,
                    active ? TEXT : MUTED, false);
        }
    }

    private void drawHeader(DrawContext context) {
        int x = SIDEBAR_WIDTH + CONTENT_MARGIN;
        context.drawTextWithShadow(textRenderer, Text.literal(pageTitle()), x, 28, TEXT);
        context.drawText(textRenderer, Text.literal(pageSubtitle()), x, 48, MUTED, false);
    }

    private void drawPage(DrawContext context) {
        int x = SIDEBAR_WIDTH + CONTENT_MARGIN;
        int right = width - CONTENT_MARGIN;
        int top = 78;
        int bottom = height - 94;

        if (right <= x || bottom <= top) {
            return;
        }

        switch (selectedPage) {
            case 1 -> drawModpackPage(context, x, right, top);
            case 2 -> drawServerPage(context, x, right, top);
            case 3 -> drawSettingsPage(context, x, right, top);
            default -> drawHomePage(context, x, right, top, bottom);
        }
    }

    private void drawHomePage(DrawContext context, int x, int right, int top, int bottom) {
        int heroBottom = Math.min(top + 178, bottom);
        panel(context, x, top, right, heroBottom, true);

        context.drawTextWithShadow(textRenderer, Text.literal("NOVUS MODDED"), x + 24, top + 24, ACCENT);
        context.drawTextWithShadow(textRenderer, Text.literal("Minecraft, simplement."), x + 24, top + 52, TEXT);
        context.drawText(textRenderer, Text.literal("Une interface claire pour ton expérience Fabric."),
                x + 24, top + 78, MUTED, false);

        context.drawText(textRenderer, Text.literal("FABRIC  •  CREATE  •  1.20.1"),
                x + 24, top + 116, TEXT, false);
        context.drawText(textRenderer, Text.literal("●  CLIENT PRÊT"), x + 24, top + 143, SUCCESS, false);

        int infoTop = heroBottom + 14;
        int infoBottom = Math.min(infoTop + 82, bottom);
        if (infoBottom > infoTop) {
            panel(context, x, infoTop, right, infoBottom, false);
            context.drawTextWithShadow(textRenderer, Text.literal("BIENVENUE"), x + 20, infoTop + 18, TEXT);
            context.drawText(textRenderer, Text.literal("Utilise le menu à gauche pour accéder aux différentes sections."),
                    x + 20, infoTop + 44, MUTED, false);
        }
    }

    private void drawModpackPage(DrawContext context, int x, int right, int top) {
        int width = right - x;
        int cardWidth = Math.max(1, (width - CARD_GAP) / 2);

        panel(context, x, top, x + cardWidth, top + 126, false);
        panel(context, x + cardWidth + CARD_GAP, top, right, top + 126, false);

        context.drawTextWithShadow(textRenderer, Text.literal("VERSION"), x + 20, top + 20, TEXT);
        context.drawText(textRenderer, Text.literal("Minecraft 1.20.1"), x + 20, top + 48, ACCENT, false);
        context.drawText(textRenderer, Text.literal("Fabric Loader 0.17.2+"), x + 20, top + 72, MUTED, false);

        int rightX = x + cardWidth + CARD_GAP + 20;
        context.drawTextWithShadow(textRenderer, Text.literal("MODS"), rightX, top + 20, TEXT);
        context.drawText(textRenderer, Text.literal("Fabric API"), rightX, top + 48, MUTED, false);
        context.drawText(textRenderer, Text.literal("Create"), rightX, top + 72, MUTED, false);

        panel(context, x, top + 140, right, top + 238, false);
        context.drawTextWithShadow(textRenderer, Text.literal("INSTALLATION"), x + 20, top + 160, TEXT);
        context.drawText(textRenderer, Text.literal("Les dépendances sont gérées par Fabric et Gradle."),
                x + 20, top + 188, MUTED, false);
        context.drawText(textRenderer, Text.literal("Aucun launcher secondaire n'est requis."),
                x + 20, top + 212, MUTED, false);
    }

    private void drawServerPage(DrawContext context, int x, int right, int top) {
        panel(context, x, top, right, top + 150, true);
        context.drawTextWithShadow(textRenderer, Text.literal("SERVEUR NOVUS"), x + 24, top + 24, TEXT);
        context.drawText(textRenderer, Text.literal("Accède à la liste multijoueur Minecraft."),
                x + 24, top + 54, MUTED, false);
        context.drawText(textRenderer, Text.literal("Le bouton ci-dessous ouvre directement l'écran des serveurs."),
                x + 24, top + 78, MUTED, false);
        context.drawText(textRenderer, Text.literal("●  MULTIJOUEUR DISPONIBLE"), x + 24, top + 116, SUCCESS, false);
    }

    private void drawSettingsPage(DrawContext context, int x, int right, int top) {
        panel(context, x, top, right, top + 150, false);
        context.drawTextWithShadow(textRenderer, Text.literal("PARAMÈTRES"), x + 24, top + 24, TEXT);
        context.drawText(textRenderer, Text.literal("Les options natives de Minecraft restent accessibles."),
                x + 24, top + 54, MUTED, false);
        context.drawText(textRenderer, Text.literal("Son, vidéo, contrôles, langue et accessibilité."),
                x + 24, top + 78, MUTED, false);
        context.drawText(textRenderer, Text.literal("Configuration locale conservée par Minecraft."),
                x + 24, top + 116, ACCENT, false);
    }

    private void panel(DrawContext context, int left, int top, int right, int bottom, boolean accent) {
        context.fill(left, top, right, bottom, CARD);
        context.fill(left, top, right, top + 1, BORDER);
        context.fill(left, bottom - 1, right, bottom, BORDER);
        context.fill(left, top, left + 1, bottom, BORDER);
        context.fill(right - 1, top, right, bottom, BORDER);
        if (accent) {
            context.fill(left, top, left + 4, bottom, ACCENT);
        }
    }

    private void drawFooter(DrawContext context) {
        int x = SIDEBAR_WIDTH + CONTENT_MARGIN;
        context.drawText(textRenderer, Text.literal("NOVUS CLIENT  •  v0.1.0"), x, height - 32, MUTED, false);
        context.drawText(textRenderer, Text.literal("Fabric 1.20.1"), width - CONTENT_MARGIN - 72, height - 32, MUTED, false);
    }

    private String pageTitle() {
        return NAV_LABELS[selectedPage];
    }

    private String pageSubtitle() {
        return switch (selectedPage) {
            case 1 -> "Ton environnement moddé, sans surcharge.";
            case 2 -> "Tout ce qu'il faut pour rejoindre une partie.";
            case 3 -> "Les réglages Minecraft au même endroit.";
            default -> "Une expérience Minecraft modded simple, rapide et propre.";
        };
    }

    private String pageNumber(int page) {
        return String.format("%02d", page + 1);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= 16 && mouseX < SIDEBAR_WIDTH - 16) {
            int startY = 92;
            for (int i = 0; i < NAV_LABELS.length; i++) {
                int y = startY + i * 48;
                if (mouseY >= y - 6 && mouseY < y + 30) {
                    selectPage(i);
                    return true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
