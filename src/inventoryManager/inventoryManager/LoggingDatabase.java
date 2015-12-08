package inventoryManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Created by jarrah on 4/09/15.
 */

public class LoggingDatabase implements Database {
    /**
     * Stores the path of the database as a string, based on the OS being run.
     */

    public static void appendPasswordLog(String barcode, String adBarcode) {
        SQLInterface.addLog(barcode, adBarcode);
    }

    public static ArrayList<String> getPasswordLog() {
        return SQLInterface.getLog(SQLInterface.TABPERSONLOG, true);
    }

    public static ArrayList<String> getPasswordLog(LocalDate from, LocalDate to) {
        return SQLInterface.getLog(SQLInterface.TABPERSONLOG, true, from, to);
    }

    public static ArrayList<String> getItemLog(boolean outOnly, LocalDate from, LocalDate to) {
        return SQLInterface.getLog(SQLInterface.TABITEMLOG, outOnly, from, to);
    }

    public static ArrayList<String> getItemLog(boolean outOnly) {
        return SQLInterface.getLog(SQLInterface.TABITEMLOG, outOnly);
    }

    /**
     * Log a single item out of the database.
     *
     * @param ID     The ID of the item to log out.
     * @param persID The ID of the person to log the item out to.
     */
    public static void logItemOut(String ID, String persID) {
        SQLInterface.addLog(ID, persID, ItemDatabase.isControlled(ID));
    }

    /**
     * Log a linked list of items out of the database to a given user.
     *
     * @param IDs    A linked list of IDs to log out in the database.
     * @param persID The ID of the person to log the items to.
     */
    public static void logItemsOut(LinkedList<String> IDs, String persID) {
        for (String ID : IDs) {
            logItemOut(ID, persID);
        }
    }

    public ArrayList<String> getOutItems() {
        return SQLInterface.getOutItemsLog();
    }

    public ArrayList<String> getOutItemIDs() {
        return SQLInterface.getOutItemsLog(SQLInterface.COLITEMLOGITEMID);
    }

    public ArrayList<String> getOutItemPersIDs() {
        return SQLInterface.getOutItemsLog(SQLInterface.COLITEMLOGPERSID);
    }

    public void signItemsIn(ArrayList<String> items, String persID) {
        for(String item : items) {
            SQLInterface.returnItem(item, persID);
        }
    }

    @Override
    public ArrayList<String> getDatabase() {
        return null;
    }

    @Override
    @Deprecated
    public void deleteEntry(String barcode) {
        return;
    }

    public void deleteEntry(String type, String barcode) {
        SQLInterface.deleteEntry(type, barcode);
    }

    @Override
    public Optional<String> getEntryName(String barcode) {
        return Optional.empty();
    }

    @Override
    public ArrayList<String> getNamesOfEntries() {
        return SQLInterface.getName(SQLInterface.TABITEMLOG);
    }

    @Override
    @Deprecated
    public boolean entryExists(String barcode) {
        return false;
    }

    public boolean entryExists(String barcode, String type) {
        return SQLInterface.entryExists(type, barcode);
    }

    @Override
    public void writeDatabaseCSV(String path) {

    }
}
