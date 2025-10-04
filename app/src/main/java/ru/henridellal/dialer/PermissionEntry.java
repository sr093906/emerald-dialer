package ru.henridellal.dialer;

public class PermissionEntry {
    public final int resId;
    public final int descriptionId;
    public PermissionEntry(int resId, int descriptionId) {
        this.resId = resId;
        this.descriptionId = descriptionId;
    }
}
