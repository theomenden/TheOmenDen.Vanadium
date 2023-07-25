package vanadium.configuration;

import me.jellysquid.mods.sodium.client.gui.options.Option;
import me.jellysquid.mods.sodium.client.gui.options.control.Control;
import me.jellysquid.mods.sodium.client.gui.options.control.ControlElement;
import me.jellysquid.mods.sodium.client.util.Dim2i;
import net.minecraft.client.renderer.Rect2i;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.Validate;

public class BlendingRadiusSliderControl implements Control<Integer> {
    private final Option<Integer> option;
    private final int minimum, maximum, interval;
    private final String sliderText;

    public BlendingRadiusSliderControl(Option<Integer> option, int minimum, int maximum, int interval, Text text) {
        Validate.isTrue(maximum > minimum, "The maximum value should be greater than the minimum value");
        Validate.isTrue(interval > 0, "The slider interval must be greater than zero (0)");
        Validate.isTrue(((maximum - minimum) % interval) == 0, "The maximum value must be divisible by the interval");
        Validate.notNull(text, "The slider text must not be null");

        this.option = option;
        this.minimum = minimum;
        this.maximum = maximum;
        this.interval = interval;
        this.sliderText = text.getString();
    }
    @Override
    public Option<Integer> getOption() {
        return this.option;
    }

    @Override
    public ControlElement<Integer> createElement(Dim2i dim2i) {
        return new ControlButton(this.option, dim2i, this.minimum, this.maximum, this.interval, this.sliderText);
    }

    @Override
    public int getMaxWidth() {
        return 125;
    }

    private static class ControlButton extends ControlElement<Integer> {
        private static final int THUMB_WIDTH = 2, TRACK_HEIGHT = 1;

        private final Rect2i sliderBoundaries;
        private final String sliderText;
        private final int minimum, range, interval;

        private double thumbPosition;

        public ControlButton(Option<Integer> option, Dim2i dim, int minimum, int maximum, int interval, String text) {
            super(option, dim);

            this.minimum = minimum;
            this.range = maximum - minimum;
            this.interval = interval;
            this.thumbPosition = this.getThumbPositionAtValue(option.getValue());
            this.sliderText = text;

            this.sliderBoundaries = new Rect2i(dim.getLimitX() - 96, dim.getCenterY() -5, 90, 10);
        }

        @Override
        public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
            super.render(drawContext, mouseX, mouseY, delta);

            if(this.option.isAvailable()
            && this.isHovered()) {
                this.renderSlider(drawContext);
                return;
            }
            this.renderStandaloneValue(drawContext);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if(this.option.isAvailable()
            && button == 0) {
                this.setValueFromMouse(mouseX);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if(this.option.isAvailable()
                    && button == 0
            && this.sliderBoundaries.contains((int)mouseX, (int) mouseY)) {
                this.setValueFromMouse(mouseX);
                return true;
            }
            return false;
        }

        public int getIntValue() {
            return this.minimum + (this.interval * ((int) Math.round(this.getSnappedThumbPosition() / this.interval)));
        }

        public double getSnappedThumbPosition() {
            return this.thumbPosition * this.range;
        }

        public double getThumbPositionAtValue(int value) {
            return (value - this.minimum) * (1.0D / this.range);
        }

        private void setValueFromMouse(double mouseInput) {
            this.setValue(mouseInput - (((double)this.sliderBoundaries.getX())/ (double) this.sliderBoundaries.getWidth()));
        }

        private void setValue(double input) {
            this.thumbPosition = Range.between(0.0D, 1.0D).fit(input);
            int value = this.getIntValue();

            if(this.option.getValue() != value) {
                this.option.setValue(value);
            }
        }

        private void renderStandaloneValue(DrawContext context) {
            int sliderX = this.sliderBoundaries.getX();
            int sliderY = this.sliderBoundaries.getY();
            int sliderWidth = this.sliderBoundaries.getWidth();
            int sliderHeight = this.sliderBoundaries.getHeight();

            String label = this.sliderText.formatted(this.option.getValue());
            int labelWidth = this.font.getWidth(label);

            int xVal = sliderX + sliderWidth - labelWidth;
            int yVal = sliderY + (sliderHeight / 2) -4;

            this.drawString(context, label, xVal, yVal, 0xFFFFFFFF);
        }

        private void renderSlider(DrawContext context) {
            int sliderX = this.sliderBoundaries.getX();
            int sliderY = this.sliderBoundaries.getY();
            int sliderWidth = this.sliderBoundaries.getWidth();
            int sliderHeight = this.sliderBoundaries.getHeight();

            this.thumbPosition = this.getThumbPositionAtValue(option.getValue());
            double valueToFit = ((double) (this.getIntValue() - this.minimum) / this.range) * sliderWidth;
            double thumbOffset = Range.between(0.0D, (double)sliderWidth)
                                      .fit(valueToFit);

            double thumbX = sliderX + thumbOffset - THUMB_WIDTH;
            double trackY = sliderY + (sliderHeight *0.5D) - (((double)TRACK_HEIGHT) * 0.5D);

            this.drawRect(thumbX, sliderY, thumbX + THUMB_WIDTH * 2, sliderY + sliderHeight, 0xFFFFFFFF);
            this.drawRect(sliderX, trackY, sliderX + sliderWidth, trackY + TRACK_HEIGHT, 0xFFFFFFFF);

            String label = String.valueOf(this.getIntValue());
            int labelWidth = this.font.getWidth(label);

            this.drawString(context, label, sliderX - labelWidth - 6, sliderY + (sliderHeight / 2) - 4, 0xFFFFFFFF);
        }
    }
}
