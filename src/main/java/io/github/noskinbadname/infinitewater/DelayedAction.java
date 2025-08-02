package io.github.noskinbadname.infinitewater;

public class DelayedAction {
    private int delay;
    private final Runnable runnable;

    public DelayedAction(int delay, Runnable runnable) {
        this.delay = delay;
        this.runnable = runnable;
        InfiniteWater.delayedActions.add(this);
    }

    protected void onTick() {
        delay--;
        if (delay <= 0) {
            runnable.run();
            InfiniteWater.delayedActions.remove(this);
        }
    }
}
