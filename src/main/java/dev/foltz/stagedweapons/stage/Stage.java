package dev.foltz.stagedweapons.stage;

import dev.foltz.stagedweapons.Util;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public class Stage<T extends IStagedItem<?>> {
    public final String name;
    public final boolean tickWhileUnselected;

    protected Stage(String name, boolean tickWhileUnselected) {
        this.name = name;
        this.tickWhileUnselected = tickWhileUnselected;
    }

    public int duration(ItemStack stack) {
        return 0;
    }

    public float barProgress(ItemStack stack) {
        return -1;
    }

    public int barColor(ItemStack stack) {
        return Util.GREEN;
    }

    public String handleInit(StagedItemView<? extends T> view) {
        return "";
    }

    public String handlePressShoot(StagedItemView<? extends T> view) {
        return "";
    }

    public String handleReleaseShoot(StagedItemView<? extends T> view) {
        return "";
    }

    public String handlePressReload(StagedItemView<? extends T> view) {
        return "";
    }

    public String handleReleaseReload(StagedItemView<? extends T> view) {
        return "";
    }

    public String handleTick(StagedItemView<? extends T> view) {
        return "";
    }

    public String handleLastTick(StagedItemView<? extends T> view) {
        return "";
    }

    public String handleUnselected(StagedItemView<? extends T> view) {
        return "";
    }

    public static <T extends IStagedItem<?>> Builder<T> builder(String name) {
        return new Builder<>(name);
    }

    public static class Builder<T extends IStagedItem<?>> {
        private String name;
        private Function<ItemStack, Integer> duration;
        private boolean tickWhileUnselected;
        private StagedItemEventHandler<T> handleInit;
        private StagedItemEventHandler<T> handlePressShoot;
        private StagedItemEventHandler<T> handleReleaseShoot;
        private StagedItemEventHandler<T> handlePressReload;
        private StagedItemEventHandler<T> handleReleaseReload;
        private StagedItemEventHandler<T> handleTick;
        private StagedItemEventHandler<T> handleLastTick;
        private StagedItemEventHandler<T> handleUnselected;
        private Function<ItemStack, Float> barProgress;
        private Function<ItemStack, Integer> barColor;

        public Builder(String name) {
            this.name = name;
            tickWhileUnselected = false;
        }

        public Builder<T> duration(int maxTicks) {
            return duration(item -> maxTicks);
        }

        public Builder<T> duration(Function<ItemStack, Integer> duration) {
            this.duration = duration;
            return this;
        }

        public Builder<T> tickWhileUnselected() {
            this.tickWhileUnselected = true;
            return this;
        }

        public Builder<T> barProgress(Function<ItemStack, Float> barProgress) {
            this.barProgress = barProgress;
            return this;
        }

        public Builder<T> barProgress(float barProgress) {
            return this.barProgress(stack -> barProgress);
        }

        public Builder<T> barColor(Function<ItemStack, Integer> barColor) {
            this.barColor = barColor;
            return this;
        }

        public Builder<T> barColor(int barColor) {
            return this.barColor(stack -> barColor);
        }

        public Builder<T> onInit(StagedItemEventHandler<? super T> handleInit) {
            this.handleInit = (StagedItemEventHandler<T>) handleInit;
            return this;
        }

        public Builder<T> onInit(String stage) {
            return this.onInit(view -> stage);
        }

        public Builder<T> onPressShoot(StagedItemEventHandler<? super T> handlePressShoot) {
            this.handlePressShoot = (StagedItemEventHandler<T>) handlePressShoot;
            return this;
        }

        public Builder<T> onPressShoot(String stage) {
            return this.onPressShoot(view -> stage);
        }

        public Builder<T> onReleaseShoot(StagedItemEventHandler<? super T> handleReleaseShoot) {
            this.handleReleaseShoot = (StagedItemEventHandler<T>) handleReleaseShoot;
            return this;
        }

        public Builder<T> onReleaseShoot(String stage) {
            return this.onReleaseShoot(view -> stage);
        }

        public Builder<T> onPressReload(StagedItemEventHandler<? super T> handlePressReload) {
            this.handlePressReload = (StagedItemEventHandler<T>) handlePressReload;
            return this;
        }

        public Builder<T> onPressReload(String stage) {
            return this.onPressReload(view -> stage);
        }

        public Builder<T> onReleaseReload(StagedItemEventHandler<? super T> handleReleaseReload) {
            this.handleReleaseReload = (StagedItemEventHandler<T>) handleReleaseReload;
            return this;
        }

        public Builder<T> onReleaseReload(String stage) {
            return this.onReleaseReload(view -> stage);
        }

        public Builder<T> onTick(StagedItemEventHandler<? super T> handleTick) {
            this.handleTick = (StagedItemEventHandler<T>) handleTick;
            return this;
        }

        public Builder<T> onTick(String stage) {
            return this.onTick(view -> stage);
        }

        public Builder<T> onLastTick(StagedItemEventHandler<? super T> handleLastTick) {
            this.handleLastTick = (StagedItemEventHandler<T>) handleLastTick;
            return this;
        }

        public Builder<T> onLastTick(String stage) {
            return this.onLastTick(view -> stage);
        }

        public Builder<T> onUnselected(StagedItemEventHandler<? super T> handleUnselected) {
            this.handleUnselected = (StagedItemEventHandler<T>) handleUnselected;
            return this;
        }

        public Builder<T> onUnselected(String stage) {
            return this.onUnselected(view -> stage);
        }

        public Stage<T> build() {
            return new Stage<>(name, tickWhileUnselected) {

                @Override
                public int duration(ItemStack stack) {
                    return duration == null ? super.duration(stack) : duration.apply(stack);
                }

                @Override
                public float barProgress(ItemStack stack) {
                    return barProgress == null ? super.barProgress(stack) : barProgress.apply(stack);
                }

                @Override
                public int barColor(ItemStack stack) {
                    return barColor == null ? super.barColor(stack) : barColor.apply(stack);
                }

                @Override
                public String handleInit(StagedItemView<? extends T> view) {
                    return handleInit == null ? super.handleInit(view) : handleInit.handleEvent(view);
                }

                @Override
                public String handlePressShoot(StagedItemView<? extends T> view) {
                    return handlePressShoot == null ? super.handlePressShoot(view) : handlePressShoot.handleEvent(view);
                }

                @Override
                public String handleReleaseShoot(StagedItemView<? extends T> view) {
                    return handleReleaseShoot == null ? super.handleReleaseShoot(view) : handleReleaseShoot.handleEvent(view);
                }

                @Override
                public String handlePressReload(StagedItemView<? extends T> view) {
                    return handlePressReload == null ? super.handlePressReload(view) : handlePressReload.handleEvent(view);
                }

                @Override
                public String handleReleaseReload(StagedItemView<? extends T> view) {
                    return handleReleaseReload == null ? super.handleReleaseReload(view) : handleReleaseReload.handleEvent(view);
                }

                @Override
                public String handleTick(StagedItemView<? extends T> view) {
                    return handleTick == null ? super.handleTick(view) : handleTick.handleEvent(view);
                }

                @Override
                public String handleLastTick(StagedItemView<? extends T> view) {
                    return handleLastTick == null ? super.handleLastTick(view) : handleLastTick.handleEvent(view);
                }

                @Override
                public String handleUnselected(StagedItemView<? extends T> view) {
                    return handleUnselected == null ? super.handleUnselected(view) : handleUnselected.handleEvent(view);
                }
            };
        }
    }
}
