package com.app.library.base;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

/**
 * Created by chenlong on 16/10/14.
 */

public interface ISwipeBack {
    /**
     * @return the SwipeBackLayout associated with this activity.
     */
    public abstract SwipeBackLayout getSwipeBackLayout();

    public abstract void setSwipeBackEnable(boolean enable);

    /**
     * Scroll out contentView and finish the activity
     */
    public abstract void scrollToFinishActivity();
}
