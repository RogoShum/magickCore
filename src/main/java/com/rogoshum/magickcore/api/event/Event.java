package com.rogoshum.magickcore.api.event;

public class Event {
    private boolean isCanceled = false;
    public boolean isCancelable()
    {
        return false;
    }
    public boolean isCanceled()
    {
        return isCanceled;
    }
    public void setCanceled(boolean cancel)
    {
        if (!isCancelable())
        {
            throw new UnsupportedOperationException(
                    "Attempted to call Event#setCanceled() on a non-cancelable event of type: "
                            + this.getClass().getCanonicalName()
            );
        }
        isCanceled = cancel;
    }
}
