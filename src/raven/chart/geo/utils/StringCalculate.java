package raven.chart.geo.utils;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class StringCalculate<T, U> implements BiConsumer<T, U> {

    public StringCalculate(Graphics2D graphics2D) {
        this.graphics2D = graphics2D;
    }
    private final Graphics2D graphics2D;
    private int width = 0;

    protected abstract String calculate(T t, U u);

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public void accept(T t, U u) {
        String st = calculate(t, u);
        FontMetrics fm = graphics2D.getFontMetrics();
        Rectangle2D r2 = fm.getStringBounds(st, graphics2D);
        width = Math.max((int) r2.getWidth(), width);
    }

    public static int calculateMap(Map map, StringCalculate b) {
        map.forEach(b);
        return b.getWidth();
    }
}
