package raven.chart.geo.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import raven.ui.utils.ShadowRenderer;

/**
 *
 * @author Raven
 */
public class PopupLabel {

    private ModelViewer viewer;
    private BufferedImage buffImage;
    private boolean update;

    public void start() {
        update = false;
    }

    public void update() {
        update = true;
    }

    public boolean isChanged(String country) {
        return viewer == null || !country.equals(viewer.getCountry());
    }

    public void update(Graphics2D g, ModelViewer viewer, Component component) {
        this.viewer = viewer;
        if (viewer != null) {
            Insets insets = new Insets(5, 5, 5, 5);
            Insets shadow = new Insets(3, 3, 7, 7);
            int shadowSize = 5;
            int round = 10;
            int arrow = 8;
            ModelFontSize r_c = getTextSize(g, viewer.getCountry(), component.getFont().deriveFont(Font.BOLD));
            ModelFontSize r_v = getTextSize(g, viewer.getValues(), component.getFont());
            int width = Math.max(r_c.getWidth() + insets.left + insets.right + shadowSize * 2, r_v.getWidth() + insets.left + insets.right + shadowSize * 2);
            int height = r_c.getHeight() + r_v.getHeight() + insets.top + insets.bottom + shadowSize * 2 + arrow;
            buffImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = buffImage.createGraphics();
            Area area = new Area(new RoundRectangle2D.Double(0, 0, width - shadowSize * 2, height - shadowSize * 2 - arrow, round, round));
            area.add(createArrow(arrow, (width - shadowSize) / 2 - arrow, height - shadowSize * 2 - arrow));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(createShadow(shadow.left, shadow.top, shadowSize, area), 0, 0, null);
            g2.setComposite(AlphaComposite.SrcOver.derive(0.65f));
            g2.translate(shadow.left, shadow.top);
            g2.setColor(new Color(255, 255, 255));
            g2.fill(area);
            g2.setColor(new Color(250, 250, 250));
            g2.draw(area);
            g2.setColor(component.getForeground());
            g2.setFont(component.getFont().deriveFont(Font.BOLD));
            g2.setComposite(AlphaComposite.SrcOver.derive(1f));
            g2.drawString(viewer.getCountry(), (float) insets.left, (float) (insets.top + r_c.getAscent()));
            g2.setFont(component.getFont());
            g2.drawString(viewer.getValues(), (float) insets.left, (float) (insets.top + r_v.getHeight() + r_c.getAscent()));
            g2.dispose();
            update = true;
        }
    }

    private Area createArrow(int arrow, int x, int y) {
        Area area = new Area(new RoundRectangle2D.Double(0, 0, arrow, arrow, 3, 3));
        AffineTransform tran = new AffineTransform();
        tran.translate(x, y - arrow / 2);
        tran.rotate(Math.toRadians(45), arrow / 2, arrow / 2);
        area.transform(tran);
        return area;
    }

    public void clearAble() {
        if (!update) {
            buffImage = null;
            viewer = null;
        }
    }

    public boolean isRenderAble() {
        return buffImage != null;
    }

    public void render(Graphics2D g2, int x, int y) {
        if (buffImage != null) {
            x -= buffImage.getWidth() / 2;
            y -= buffImage.getHeight() - 5;
            g2.drawImage(buffImage, x, y, null);
        }
    }

    private BufferedImage createShadow(int x, int y, int shadowSize, Shape shape) {
        BufferedImage img = new ShadowRenderer(shadowSize, 0.2f, Color.BLACK).createShadow(shape);
        Graphics2D ng2 = img.createGraphics();
        ng2.setComposite(AlphaComposite.Clear);
        ng2.setColor(Color.red);
        ng2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ng2.translate(x, y);
        ng2.fill(shape);
        ng2.dispose();
        return img;
    }

    private ModelFontSize getTextSize(Graphics2D g2, String text, Font font) {
        FontMetrics f = g2.getFontMetrics(font);
        Rectangle2D r2 = f.getStringBounds(text, g2);
        int ascent = f.getAscent();
        return new ModelFontSize((int) r2.getWidth(), (int) r2.getHeight(), ascent);
    }
}
