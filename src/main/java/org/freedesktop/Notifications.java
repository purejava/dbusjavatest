package org.freedesktop;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.Variant;
import org.freedesktop.dbus.exceptions.DBusException;

import java.util.List;
import java.util.Map;

public interface Notifications extends DBusInterface
{
    public static class NotificationClosed extends DBusSignal
    {
        public final UInt32 id;
        public final UInt32 reason;
        public NotificationClosed(String path, UInt32 id, UInt32 reason) throws DBusException
        {
            super(path, id, reason);
            this.id = id;
            this.reason = reason;
        }
    }

    public static class ActionInvoked extends DBusSignal
    {
        public final UInt32 id;
        public final String action_key;
        public ActionInvoked(String path, UInt32 id, String action_key) throws DBusException
        {
            super(path, id, action_key);
            this.id = id;
            this.action_key = action_key;
        }
    }

    public List<String> GetCapabilities();
    public UInt32 Notify(String app_name,
                         UInt32 replaces_id,
                         String app_icon,
                         String summary,
                         String body,
                         List<String> actions,
                         Map<String, Variant<Byte>> hints,
                         int expire_timeout);
    public void CloseNotification(UInt32 id);
    public Quad<String, String, String, String> GetServerInformation();

}
