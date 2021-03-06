package inventoryManager;
//TODO: Why can't this be static

/***
 *    Copyright (C) 2015  Jarrah Gosbell
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * @author Jarrah Gosbell
 */

import java.util.ArrayList;
import java.util.Optional;

class ItemDatabase implements Database {
    /**
     * Constructor for ItemDatabase.
     * Will create a Person database with the ability to read and write people to the database location given in the preferences file of Settings
     */
    public ItemDatabase() {

    }

    /**
     * Determine whether an item is a controlled item.
     *
     * @param ID The ID of the item to check
     * @return True if the item is controlled.
     */
    public static boolean isControlled(String ID) {
        return SQLInterface.isItemControlled(ID);
    }

    /**
     * Set a new product within the database.
     * Precondition: augments int productNo, String name, String artist, double size, double duration are input
     * Postcondition: Data for the currant working product in this database will be set.
     * @param name The name of the new product
     * @param barcode The barcode of the new product
     */
    public boolean addEntry(String barcode, String name)
    {
        return SQLInterface.addEntry(barcode, name);

    }

    /**
     * Alter an existing product within the database
     * Precondition: augments int productNo, String name, String artist, double size, double duration are input
     * Postcondition: Data for the currant working product in this database will be set.
     * @param newID The new barcode of the product
     * @param ID The old barcode of the product
     * @param name The old name of the product
     */
    public final void changeItem(String name, String newID, String ID)
    {
        SQLInterface.updateEntry(ID, name, newID, SQLInterface.TABITEM);
    }

    /**
     * Get the entire database as a string
     * Precondition: setDatabase has been run
     * Postcondition: the user will be see an output of the persons in the database.
     * @return A string containing the entire database
     */
    public final ArrayList<String> getDatabase() {
        return SQLInterface.getDatabase(SQLInterface.TABITEM);
    }

    /**
     * Deletes the specified product from the database
     * Preconditions: setDatabase has been run
     * Postconditions: the chosen product will no longer exist.
     * @param barcode The barcode of the item you wish to delete
     */
    public void deleteEntry(String barcode) {
        if (!isControlled(barcode)) {
            SQLInterface.deleteEntry(SQLInterface.TABGENERAL, barcode);
        }
    }

    /**
     * Determine Whether a product Exists given only their barcode
     * @param barcode The barcode of the person you wish to check for
     * @return A boolean value of whether the product exists or not
     */
    public final boolean entryExists(String barcode) {
        return SQLInterface.entryExists(SQLInterface.TABITEM, barcode);
    }

    @Override
    public void writeDatabaseCSV(String path) {

    }

    /**
     * Write out a CSV version of the database for future import.
     * @param path The path to the directory you wish to output to
     */
    public void writeDatabaseCSV(String type, String path) {
        SQLInterface.export(type, path);
    }

    /**
     * A list of the names of all products in the database
     * @return A String array of the names of all products in the database.
     */
    public ArrayList<String> getNamesOfEntries(String type) {
        return SQLInterface.getName(type);
    }

    /**
     * Get the name of an item given it's ID
     * @param ID The ID of the item to get the name of.
     * @return The name of the item.
     */
    public Optional<String> getEntryName(String ID) {
        return SQLInterface.getName(SQLInterface.TABITEM, ID);
    }

    /**
     * Get the barcode of an item given it's name.
     * @param name The name of the item to search for.
     * @return The ID of the first item in the database with the given name.
     */
    public final Optional<String> getBarcode(String name) {
        return SQLInterface.getID(SQLInterface.TABITEM, name);
    }


    /**
     * Add an entry to the controlled item database
     * @param barcode The barcode of the item to add.
     * @param name The name of the item to add.
     * @param setName The set name of the item to add. May be null.
     * @param state The state of the item to add.
     * @param tagPos The tag number or position number of the item.
     * @param type The type of the item.
     */
    public boolean addEntry(String barcode, String name, String setName, String state, String tagPos, String type) {
        return SQLInterface.addEntry(barcode, name, setName, state, tagPos, type);
    }

    /**
     * Remove an item from the database.
     * @param ID The ID of the item to delete.
     * @param controlled Whether the item is stored in the controlled database or in the general dataase.
     */
    public void deleteEntry(String ID, boolean controlled) {
        if (controlled) {
            SQLInterface.deleteEntry(SQLInterface.TABCONTROLLED, ID);
            SQLInterface.deleteEntry(SQLInterface.TABITEM, ID);
        } else {
            SQLInterface.deleteEntry(SQLInterface.TABGENERAL, ID);
            SQLInterface.deleteEntry(SQLInterface.TABITEM, ID);
        }
    }


    /**
     * add an entry to the item database.
     * @param barcode The barcode of the item to add. Will be used as the database key.
     * @param name The name of the item.
     * @param setName the name of the set the item is is. May be null.
     * @param description A description of the item.
     * @param quantity The quantity of the item that exists.
     */
    public boolean addEntry(String barcode, String name, String setName, String description, long quantity, String location) {
        return SQLInterface.addEntry(barcode, name, setName, description, quantity, location);
    }

    /**
     * Get the number of a given product left in stock
     * @param barcode the name of the product you wish to check
     * @return The number as an int of the product left in stock
     */
    public final int getNumber(String barcode) {
        return SQLInterface.getQuantity(barcode);
    }

    /**
     * Set the number of a specified item you have in stock
     * @param barcode The name of the item you wish to set
     * @param number The number of that item you now have.
     */
    public final void setNumber(String barcode, int number) {
        SQLInterface.setQuantity(barcode, number);
    }

    /**
     * A list of the names of all products in the database
     * @return A String array of the names of all products in the database.
     */
    public final ArrayList<String> getNamesOfEntries() {
        return SQLInterface.getName(SQLInterface.TABGENERAL);
    }

    public final ArrayList<String> getSets() {
        return SQLInterface.getName(SQLInterface.TABSET);
    }

    public final ArrayList<String> getTypes() {
        return SQLInterface.getName(SQLInterface.TABCONTROLLEDTYPE);
    }

    public final void addSet(String ID, String name) {
        SQLInterface.addSet(ID, name);
    }
    public final void addControlledType(String ID, String name) {
        SQLInterface.addControlledType(ID, name);
    }
}
