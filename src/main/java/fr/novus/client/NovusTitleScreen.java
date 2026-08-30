package fr.novus.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Lightweight Novus home screen with a persistent left navigation rail. */
public final class NovusTitleScreen extends Screen {
    private static final int ORANGE = 0xFFFF7A00;
    private static final int BG = 0xFF101216;
    private static final int PANEL = 0xFF181B21;
    private static final int PANEL_2 = 0xFF20242C;
    private static final int TEXT = 0xFFF4F5F7;
    private static final int MUTED = 0xFF969DA8;
    private static final int SIDEBAR = 220;

    private int selected = 0;
    private ButtonWidget serverButton;
    private ButtonWidget settingsButton;

    public NovusTitleScreen() {
        super(Text.literal("Novus"));
    }

    @Override
    protected void init() {
        // No Singleplayer/Multiplayer buttons here. Navigation is handled by
        // the left rail and the selected page's content.
        serverButton = addDrawableChild(ButtonWidget.builder(Text.literal("REJOINDRE NOVUS"), b ->
                client.setScreen(new MultiplayerScreen(this)))
                .dimensions(SIDEBAR + 36, height / 2 + 48, 210, 36).build());

        settingsButton = addDrawableChild(ButtonWidget.builder(Text.literal("OUVRIR LES PARAMÈTRES"), b ->
                client.setScreen(new OptionsScreen(this, client.options)))
                .dimensions(SIDEBAR + 36, height / 2 + 48, 210, 36).build());

        refreshPage();
    }

    private void refreshPage() {
        if (serverButton != null) serverButton.visible = selected == 2;
        if (settingsButton != null) settingsButton.visible = selected == 3;
    }

    private void select(int page) {
        if (selected != page) {
            selected = page;
            refreshPage();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, BG);
        context.fill(0, 0, SIDEBAR, height, PANEL);
        context.fill(SIDEBAR, 0, SIDEBAR + 1, height, 0xFF2A2E36);

        // Novus branding
        context.drawTextWithShadow(textRenderer, Text.literal("NOVUS"), 28, 28, TEXT);
        context.drawText(textRenderer, Text.literal("CLIENT"), 29, 45, ORANGE, false);

        String[] labels = {"ACCUEIL", "MODPACK", "SERVEUR", "PARAMÈTRES"};
        String[] icons = {"⌂", "◆", "◈", "⚙"};
        int navY = 92;
        for (int i = 0; i < labels.length; i++) {
            int y = navY + i * 52;
            boolean active = selected == i;
            if (active) {
                context.fill(16, y - 8, SIDEBAR - 16, y + 32, 0xFF2A2F38);
                context.fill(16, y - 8, 20, y + 32, ORANGE);
            }
            context.drawTextWithShadow(textRenderer, Text.literal(icons[i]), 32, y + 4, active ? ORANGE : MUTED);
            context.drawText(textRenderer, Text.literal(labels[i]), 62, y + 5, active ? TEXT : MUTED, false);
        }

        // Main content
        int x = SIDEBAR + 36;
        int right = width - 36;
        context.drawTextWithShadow(textRenderer, Text.literal(pageTitle()), x, 38, TEXT);
        context.drawText(textRenderer, Text.literal(pageSubtitle()), x, 58, MUTED, false);

        if (selected == 0) renderHome(context, x, right);
        else if (selected == 1) renderModpack(context, x, right);
        else if (selected == 2) renderServer(context, x, right);
        else renderSettings(context, x, right);

        super.render(context, mouseX, mouseY, delta);
    }

    private String pageTitle() {
        return switch (selected) {
            case 1 -> "MODPACK";
            case 2 -> "SERVEUR";
            case 3 -> "PARAMÈTRES";
            default -> "BIENVENUE SUR NOVUS";
        };
    }

    private String pageSubtitle() {
        return switch (selected) {
            case 1 -> "Fabric + Create • version 0.1.1";
            case 2 -> "Ton espace multijoueur Novus";
            case 3 -> "Configure ton expérience de jeu";
            default -> "Une expérience Minecraft modded simple, rapide et propre.";
        };
    }

    private void renderHome(DrawContext c, int x, int right) {
        c.fill(x, 92, right, 270, PANEL);
        c.fill(x, 92, x + 5, 270, ORANGE);
        c.drawTextWithShadow(textRenderer, Text.literal("NOVUS MODDED"), x + 24, 120, ORANGE);
        c.drawTextWithShadow(textRenderer, Text.literal("Fabric + Create"), x + 24, 146, TEXT);
        c.drawText(textRenderer, Text.literal("Minecraft 1.20.1"), x + 24, 169, MUTED, false);
        c.drawText(textRenderer, Text.literal("Les mods sont gérés automatiquement par le launcher."), x + 24, 202, MUTED, false);
        c.drawText(textRenderer, Text.literal("PRÊT À JOUER"), x + 24, 238, 0xFF65D18A, false);
    }

    private void renderModpack(DrawContext c, int x, int right) {
        card(c, x, 92, right, 162, "MODPACK NOVUS", "Fabric  •  Create  •  Minecraft 1.20.1", "Installation et vérification automatiques");
        card(c, x, 178, right, 248, "OPTIMISATION", "Chargement léger et rendu stable", "Novus évite les opérations coûteuses à chaque tick");
    }

    private void renderServer(DrawContext c, int x, int right) {
        card(c, x, 92, right, 210, "SERVEUR NOVUS", "Adresse : serveur Novus", "Le bouton ci-dessous ouvre la liste des serveurs Minecraft.");
    }

    private void renderSettings(DrawContext c, int x, int right) {
        card(c, x, 92, right, 210, "PARAMÈTRES", "Minecraft et interface", "Utilise le bouton ci-dessous pour ouvrir les options natives.");
    }

    private void card(DrawContext c, int x, int top, int right, int bottom, String title, String line, String detail) {
        c.fill(x, top, right, bottom, PANEL);
        c.drawTextWithShadow(textRenderer, Text.literal(title), x + 20, top + 20, TEXT);
        c.drawText(textRenderer, Text.literal(line), x + 20, top + 46, ORANGE, false);
        c.drawText(textRenderer, Text.literal(detail), x + 20, top + 72, MUTED, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && mouseX >= 16 && mouseX < SIDEBAR - 16) {
            int navY = 92;
            for (int i = 0; i < 4; i++) {
                int y = navY + i * 52;
                if (mouseY >= y - 8 && mouseY <= y + 32) {
                    select(i);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
