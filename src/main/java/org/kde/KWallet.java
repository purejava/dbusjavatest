package org.kde;

import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.annotations.DBusMemberName;
import org.freedesktop.dbus.annotations.MethodNoReply;
import org.freedesktop.dbus.exceptions.DBusException;
import org.freedesktop.dbus.interfaces.DBusInterface;
import org.freedesktop.dbus.messages.DBusSignal;

import java.util.List;

public interface KWallet extends DBusInterface {

    public static class walletListDirty extends DBusSignal {
        public walletListDirty(String path) throws DBusException {
            super(path);
        }
    }

    public static class walletCreated extends DBusSignal {
        public final String wallet;

        public walletCreated(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class walletOpened extends DBusSignal {
        public final String wallet;

        public walletOpened(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class walletAsyncOpened extends DBusSignal {
        public final int tId;
        public final int handle;

        public walletAsyncOpened(String path, int tId, int handle) throws DBusException {
            super(path, tId, handle);
            this.tId = tId;
            this.handle = handle;
        }
    }

    public static class walletDeleted extends DBusSignal {
        public final String wallet;

        public walletDeleted(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class walletClosed extends DBusSignal {
        public final String wallet;

        public walletClosed(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class walletClosedId extends DBusSignal {
        public final int handle;

        public walletClosedId(String path, int handle) throws DBusException {
            super(path, handle);
            this.handle = handle;
        }
    }

    public static class allWalletsClosed extends DBusSignal {
        public allWalletsClosed(String path) throws DBusException {
            super(path);
        }
    }

    public class folderListUpdated extends DBusSignal {
        public final String wallet;

        public folderListUpdated(String path, String wallet) throws DBusException {
            super(path, wallet);
            this.wallet = wallet;
        }
    }

    public static class folderUpdated extends DBusSignal {
        public final String a;
        public final String b;

        public folderUpdated(String path, String a, String b) throws DBusException {
            super(path, a, b);
            this.a = a;
            this.b = b;
        }
    }

    public static class applicationDisconnected extends DBusSignal {
        public final String wallet;
        public final String application;

        public applicationDisconnected(String path, String wallet, String application) throws DBusException {
            super(path, wallet, application);
            this.wallet = wallet;
            this.application = application;
        }
    }

    /**
     * Is kwallet installed?
     *
     * @return Indicator, if the kwallet daemon is up and running or not.
     */
    abstract public boolean isEnabled();

    /**
     * Open and unlock the wallet.
     *
     * @param wallet The wallet to be opened and unlocked.
     * @param wId    The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid  The application that accesses the wallet.
     * @return Handle to the wallet or -1, if opening fails.
     */
    abstract public int open(String wallet, long wId, String appid);

    /**
     * Open and unlock the wallet with this path.
     *
     * @param path  Path to the wallet.
     * @param wId   The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid The application that accesses the wallet.
     * @return Always 0.
     */
    abstract public int openPath(String path, long wId, String appid);

    /**
     * Open the wallet asynchronously.
     *
     * @param wallet        The wallet to be opened.
     * @param wId           The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid         The application that accesses the wallet.
     * @param handleSession Handle session with kwalletsessionstore or not.
     * @return Sequential TransactionID.
     */
    abstract public int openAsync(String wallet, long wId, String appid, boolean handleSession);

    /**
     * Open and unlock the wallet with this path asynchronously.
     *
     * @param path          Path to the wallet.
     * @param wId           The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid         The application that accesses the wallet.
     * @param handleSession Handle session with kwalletsessionstore or not.
     * @return Sequential TransactionID.
     */
    abstract public int openPathAsync(String path, long wId, String appid, boolean handleSession);

    /**
     * Close and lock the wallet. The wallet will only be closed if it is open but not in use (rare), or if it is forced closed.
     * <p>
     * If force = true, will close it for all users.  Behave.  This
     * can break applications, and is generally intended for use by
     * the wallet manager app only.
     *
     * @param wallet The wallet to be closed and locked.
     * @param force  Forced closing or not.
     * @return -1 if wallet does not exist, 0 if all references fom applications to the wallet have been removed.
     */
    abstract public int close(String wallet, boolean force);

    /**
     * Close and lock the wallet.
     * <p>
     * If force = true, will close it for all users.  Behave.  This
     * can break applications, and is generally intended for use by
     * the wallet manager app only.
     *
     * @param handle Handle to the wallet to be closed and locked.
     * @param force  Forced closing or not.
     * @param appid  AppID of the app to access the wallet.
     * @return -1 if wallet does not exist, amount of references to the wallet fom other applications.
     */
    abstract public int close(int handle, boolean force, String appid);

    /**
     * Save to disk but leave open.
     *
     * @param handle Handle to the wallet to be saved.
     * @param appid  AppID of the app to access the wallet.
     */
    @MethodNoReply
    abstract public void sync(int handle, String appid);

    /**
     * Physically deletes the wallet from disk.
     *
     * @param wallet The wallet to be deleted.
     * @return -1 if something went wrong, 0 if the wallet could be deleted.
     */
    abstract public int deleteWallet(String wallet);

    /**
     * Is the wallet open and unlocked?
     *
     * @param wallet    The wallet to be tested.
     * @return True, if the wallet is open and unlocked, false otherwise.
     */
    abstract public boolean isOpen(String wallet);

    /**
     * Is the wallet open and unlocked?
     *
     * @param handle    The handle to the wallet to be tested.
     * @return True, if the wallet is open and unlocked, false otherwise.
     */
    abstract public boolean isOpen(int handle);

    /**
     * List the applications that are using the wallet.
     *
     * @param wallet    The wallet to query.
     * @return A list of all application IDs using the wallet.
     */
    abstract public List<String> users(String wallet);

    /**
     * Request to the wallet service to change the password of the wallet.
     *
     * @param wallet    The wallet to change the password of.
     * @param wId       The window id to associate any dialogs with. You can pass 0 if you don't have a window the password dialog should associate with.
     * @param appid     AppID of the app to access the wallet.
     */
    abstract public void changePassword(String wallet, long wId, String appid);

    /**
     * List of all wallets.
     *
     * @return List of wallets available.
     */
    abstract public List<String> wallets();

    /**
     * Obtain the list of all folders contained in the wallet.
     *
     * @param handle    Handle to the wallet to be read from.
     * @param appid     AppID of the app to access the wallet.
     * @return List of folders in that wallet.
     */
    abstract public List<String> folderList(int handle, String appid);

    abstract public boolean hasFolder(int handle, String folder, String appid);

    /**
     * Create a folder.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Name of the folder.
     * @param appid     AppID of the app to access the wallet.
     * @return True on success, false on error or in case the folder already exists.
     */
    abstract public boolean createFolder(int handle, String folder, String appid);

    /**
     * Delete a folder.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Name of the folder.
     * @param appid AppID of the app to access the wallet.
     * @return True on success, false on error.
     */
    abstract public boolean removeFolder(int handle, String folder, String appid);

    /**
     * Get a list of all the entries (keys) in the given folder.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Name of the folder.
     * @param appid     AppID of the app to access the wallet.
     * @return List of entries (keys) in the folder.
     */
    abstract public List<String> entryList(int handle, String folder, String appid);

    /**
     * Read a secret from the wallet.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Folder that contains the secret.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return The secret or an array bytes with length 0, in case there is no secret stored for that key.
     */
    abstract public byte[] readEntry(int handle, String folder, String key, String appid);

    abstract public List<Byte> readMap(int handle, String folder, String key, String appid);

    /**
     * Read a secret of type password from the wallet.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Folder that contains the secret.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return The secret or an empty String, in case there is no secret stored for that key.
     */
    abstract public String readPassword(int handle, String folder, String key, String appid);

    /*
    abstract public Map<String, Variant> readEntryList(int handle, String folder, String key, String appid);

    abstract public Map<String, Variant> readMapList(int handle, String folder, String key, String appid);

    abstract public Map<String, Variant> readPasswordList(int handle, String folder, String key, String appid);
    */

    abstract public int renameEntry(int handle, String folder, String oldName, String newName, String appid);

    /**
     * Store a secret in the wallet. An existing secret gets overwritten.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Folder to store the secret in.
     * @param key       Identifier for the secret.
     * @param value     The secret itself.
     * @param entryType An enumerated type representing the type of the entry, e.g. 1 for password, 2 for stream, 3 for map
     * @param appid     AppID of the app to access the wallet.
     * @return 0 if storing the secret was successful, -1 otherwise.
     */
    abstract public int writeEntry(int handle, String folder, String key, List<Byte> value, int entryType, String appid);

    /**
     * Store a secret of type stream in the wallet. An existing secret gets overwritten.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Folder to store the secret in.
     * @param key       Identifier for the secret.
     * @param value     The secret itself.
     * @param appid     AppID of the app to access the wallet.
     * @return 0 if storing the secret was successful, -1 otherwise.
     */
    abstract public int writeEntry(int handle, String folder, String key, List<Byte> value, String appid);

    abstract public int writeMap(int handle, String folder, String key, List<Byte> value, String appid);

    /**
     * Store a secret of type password in the wallet. An existing secret gets overwritten.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Folder to store the secret in.
     * @param key       Identifier for the secret.
     * @param value     The secret itself.
     * @param appid     AppID of the app to access the wallet.
     * @return 0 if storing the secret was successful, -1 otherwise.
     */
    abstract public int writePassword(int handle, String folder, String key, String value, String appid);

    /**
     * Check whether a folder in a wallet contains an identifier for a secret.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Folder to search.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return True if the folder contains the key, false otherwise.
     */
    abstract public boolean hasEntry(int handle, String folder, String key, String appid);

    /**
     * Determine the type of the entry key in this folder.
     *
     * @param handle    Handle to the wallet to read from.
     * @param folder    Name of the folder.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return An enumerated type representing the type of the entry on creation, e.g. 1 for password, 2 for stream, 3 for map, 0 if the key was not found.
     */
    abstract public int entryType(int handle, String folder, String key, String appid);

    /**
     * Delete an identifier for a secret from the folder.
     *
     * @param handle    Handle to the wallet to write to.
     * @param folder    Folder to delete the key from.
     * @param key       Identifier for the secret.
     * @param appid     AppID of the app to access the wallet.
     * @return 0 if deleting the key was successful, -1 in case the wallet does not exist, -3 in case the key does not exist.
     */
    abstract public int removeEntry(int handle, String folder, String key, String appid);

    /**
     * Disconnect the application from wallet.
     *
     * @param wallet      The name of the wallet to disconnect from.
     * @param application The name of the application to disconnect.
     * @return True on success, false on error.
     */
    abstract public boolean disconnectApplication(String wallet, String application);

    abstract public void reconfigure();

    abstract public boolean folderDoesNotExist(String wallet, String folder);

    abstract public boolean keyDoesNotExist(String wallet, String folder, String key);

    abstract public void closeAllWallets();

    /**
     * The name of the wallet used to store network passwords.
     *
     * @return Name of the wallet.
     */
    abstract public String networkWallet();

    /**
     * The name of the wallet used to store local passwords.
     *
     * @return Name of the wallet.
     */
    abstract public String localWallet();

    @MethodNoReply
    abstract public void pamOpen(String wallet, List<Byte> passwordHash, int sessionTimeout);

    abstract public boolean isRemote();
}