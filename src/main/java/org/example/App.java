package org.example;

import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.handlers.SignalHandler;
import org.freedesktop.dbus.messages.DBusSignal;
import org.kde.KWallet;
import org.purejava.KDEWallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static java.lang.System.exit;

public class App {
    public static void main(String[] args) {
        DBusConnection connection = null;
        SignalHandler sh = SignalHandler.getInstance();
        Logger log = LoggerFactory.getLogger(App.class);

        final List<Class<? extends DBusSignal>> signals = Arrays.asList(
                KWallet.applicationDisconnected.class,
                KWallet.folderUpdated.class,
                KWallet.folderListUpdated.class,
                KWallet.walletClosedId.class,
                KWallet.walletClosed.class,
                KWallet.allWalletsClosed.class,
                KWallet.walletDeleted.class,
                KWallet.walletAsyncOpened.class,
                KWallet.walletOpened.class,
                KWallet.walletCreated.class,
                KWallet.walletListDirty.class);

        try {
            connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);
            log.info(connection.toString());

            if (signals != null) {
                sh.connect(connection, signals);
            }

            KWallet service = connection.getRemoteObject("org.kde.kwalletd5",
                    "/modules/kwalletd5", KWallet.class);

            String wallet = "kdewallet";
            int wId = 0;
            String appid = "Tester";
            int handle = service.open(wallet, 0, appid);
            log.info("Handle = " + handle);
            String folder = "Test-Folder";
            boolean created = service.createFolder(handle, folder, appid);
            service.removeFolder(handle, folder, appid);
            service.close(handle, true, appid);
            sh.await(KWallet.walletClosed.class, "/modules/kwalletd5", () -> {
                return null;
            });
            sh.await(KWallet.walletClosedId.class, "/modules/kwalletd5", () -> {
                return null;
            });
            log.info("Wallet closed: {}", sh.getLastHandledSignal(KWallet.walletClosed.class, "/modules/kwalletd5").wallet);
            log.info("Wallet closed: {}", sh.getLastHandledSignal(KWallet.walletClosedId.class, "/modules/kwalletd5").handle);
        } catch (DBusException e) {
            log.error(e.getMessage() + e.getCause());
            exit(-1);
        }
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
