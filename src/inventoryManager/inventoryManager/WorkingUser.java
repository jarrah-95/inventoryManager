package inventoryManager;


import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.LinkedList;

/*
*    TOC19 is a simple program to run TOC payments within a small group.
*    Copyright (C) 2014  Jarrah Gosbell
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
 * @author jarrah
 */
class WorkingUser {

    /**
     * The currently logged in user
     */
    private static String userID;
    private static String userName;
    /**
     * The database which stores all products used by the system.
     */
    private final ItemDatabase itemDatabase;
    /**
     * The database which stores all people who can use the system
     */
    private final PersonDatabase personDatabase;
    /**
     * the checkout used to store what a person is purchasing at a given time
     */
    private CheckOut checkOuts;
    /**
     * The database used for logging changes and transactions
     */
    private LoggingDatabase loggingDatabase;

    /**
     * Create the working user instance with both databases and a checkout.
     */
    public WorkingUser() {
        itemDatabase = new ItemDatabase();
        personDatabase = new PersonDatabase();
        checkOuts = new CheckOut();
        loggingDatabase = new LoggingDatabase();
        userID = null;
        userName = null;
    }

    /**
     * Get a hashed password ready for storage
     *
     * @param password The password to be hashed.
     * @return A string array with the hashed passwod in place 0 and the salt used in place 1.
     * @deprecated Does not generate the correct hash when compared to getSecurePassword(String, String)
     */
    public static String[] getSecurePassword(String password) //throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = new byte[0];
        try {
            salt = getSalt().getBytes();
        } catch (NoSuchAlgorithmException e) {
            Log.print(e);
        }
        byte[] hash = null;

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        String[] ret = new String[0];
        try {
            ret = new String[]{(new String(hash, "UTF-8")), (new String(salt, "UTF-8"))};
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Get a hashed password ready for storage or comparison.
     *
     * @param password The password to hash
     * @param NaCl     The salt to use in the hashing process
     * @return A string array with the password in the 0 position and the salt in the 1 position
     */
    private static String[] getSecurePassword(String password, String NaCl) //throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        System.out.println("-----------------------------------------");
        System.out.println(NaCl);
        byte[] salt = NaCl.getBytes();


        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = null;
        byte[] hash = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1"); // TODO: should this use something more than sha1?
            hash = skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        String[] ret = new String[0];
        try {
            ret = new String[]{(new String(hash, "UTF-8")), (new String(salt, "UTF-8"))};
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Create a random salt for use in generating hashes
     *
     * @return A strting of UTF-8 encoded random bytes
     * @throws NoSuchAlgorithmException
     */
    private static String getSalt() throws NoSuchAlgorithmException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt.toString();
    }

    /**
     * Take a cleartext password and hash it ready for either checking or storage
     * @param password The clear text password
     * @return The hash of the given password.
     */
//    public final String getSecurePassword(String passwordToHash) {
//        String generatedPassword = null;
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            byte[] bytes = md.digest(passwordToHash.getBytes());
//            StringBuilder sb = new StringBuilder();
//            for (byte aByte : bytes) {
//                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
//            }
//            generatedPassword = sb.toString();
//        } catch (NoSuchAlgorithmException e) {
//            inventoryManager.Log.print(e);
//        }
//        return generatedPassword;
//    }

    /**
     * Get the ID of the member currently logged in
     *
     * @return The ID of the currently logged in member.
     */
    public static String getLogedInBarcode() {
        return userID;
    }

    /**
     * Take the given PMKeyS and find the user which correlates with it. Then authenticate the user with the given password.
     *
     * @param ID   The ID that you wish to search for as a string
     * @param pass The password of the ID.
     * @return 0 if the user found, 1 if the user dose not exist, 2 if the user cannot buy.
     */
    public final int getbarcode(String ID, String pass) {
        //if ((input != null && !input.equals("")) && (!input.matches("[0-9]+"))) {
        //    input = input.substring(1);
        //}
        if (ID == null || ID.equals("") || !personDatabase.entryExists(ID)) { // checks for valid numbers in the PMKeyS
            userName = null;
            userID = null;
            return 1;
        } else {
            if (passwordsEqual(ID, pass)) {
                userName = personDatabase.getEntryName(ID);
                userID = ID;
            } else {
                return 1;
            }
        }
        return 0;
    }

    /**
     * Get a list of the names of all users in the database
     *
     * @return A string array of the usernames
     */
    public final ArrayList<String> getUserNames() {
        return personDatabase.getNamesOfEntries();
    }

    /**
     * Get a list of the names of all products in the database
     *
     * @return A string array of the product names
     */
    public final ArrayList<String> getProductNames(String type) { //TODO: DAFAQ is this. Fix.
        if (type.equals("general")) {
            return itemDatabase.getNamesOfEntries();
        } else if (type.equals("controlled")) {
            return itemDatabase.getNamesOfEntries();
        }
        return itemDatabase.getNamesOfEntries();
    }

    /**
     * Test whether a cleartext password is equal to the stored admin password
     *
     * @param PW A cleartext password to test
     * @return The boolean test for whether the passwords are equal.
     */
    public final boolean passwordsEqual(String barcode, String PW) {
        String[] old = personDatabase.getPassword(barcode);
        if (old[0] == null || old[1] == null) return false;
        System.out.println("Hash Old: " + old[0] +
                "\nSalt Old: " + old[1]);
        String[] testing;
        testing = getSecurePassword(PW, old[1]); //get secure password from new password and old salt
        System.out.println("Hash New: " + testing[0]);
        System.out.println("Salt New:" + testing[1]);
        return (testing[0].equals(old[0]));
    }

    /**
     * Takes the given (prehashed) password and set it as the admin password
     *
     * @param PW The prehashed password to be set
     */
    public final int setPassword(String barcode, String PW, String adBarcode, String adPass) {
        if (isUserAdmin(adBarcode) && passwordsEqual(adBarcode, adPass)) {
            if (personDatabase.entryExists(barcode)) {
                String[] pass = new String[0];
                try {
                    pass = getSecurePassword(PW, getSalt());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                    return -1;
                }
                personDatabase.setPassword(barcode, pass[0], pass[1]);
//              LoggingDatabase.appendPasswordLog(String barcode, String adBarcode);
                return 0;
            }
            return 1;
        }
        return 2;
    }

    public final boolean isUserAdmin(String barcode) {
        return personDatabase.getRole(barcode) > PersonDatabase.USER;
    }

    final boolean isLong(String s) {
        if (s == null) return false;
        try {
            Long.parseLong(s); // try to parse the string, catching a failure.
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * Create a scroll pane of one of the databases
     *
     * @param type A string of either SQLInterface.TABITEM or SQLInterface.TABPERSON used to determine which database to print.
     * @return A scroll pane showing the database chosen in the parameter or the person database by default.
     * @throws IOException
     */
    public ScrollPane printDatabase(String type) throws IOException {
        TextArea textArea;
        switch (type) {
            case (SQLInterface.TABITEM):
                textArea = new TextArea(itemDatabase.getDatabase().toString());
                break;
            case (SQLInterface.TABPERSON):
                textArea = new TextArea(personDatabase.getDatabase().toString());
                break;
            default:
                textArea = new TextArea(personDatabase.getDatabase().toString());
                break;
        }
        textArea.setEditable(false); // stop the user being able to edit this and thinking it will save.
        ScrollPane scrollPane = new ScrollPane(textArea);
        textArea.setWrapText(true);
        scrollPane.setHvalue(600);
        scrollPane.setVvalue(800);
        return scrollPane;

    }

    /**
     * Log the user out from this class
     */
    public final void logOut() { //TODO: this will probably have ta change.
        userName = null;
        userID = null;
        checkOuts = new CheckOut();
    }

    /**
     * Have the connected user buy the products in the checkout, adding the total cost to the users bill,
     * taking the number bought from the products in the database and clearing both the user and the checkout
     */
    public final void checkOutItems() {
        LinkedList purchased = checkOuts.productBought(); // clear the quantities and checkout
        itemDatabase.logItemsOut(purchased, userID); //TODO: Make this actually write out the items.
        checkOuts = new CheckOut(); // ensure checkout clear
        userName = null;
        userID = null;
    }

    /**
     * Get the names of all products in the checkout
     *
     * @return A string array of the names of all products in the checkout
     */
    public final LinkedList<String> getCheckOutNames() {
        return checkOuts.getCheckOutNames();
    }

    /**
     * Takes the error value given by getPMKeyS and uses it to give the username or an error message
     *
     * @param userError 0 if the user was correctly found, 1 if the user was not found and 2 if the user has been locked out.
     * @return The appropriate error message or the users name
     */
    public final String userName(int userError) {
        switch (userError) {
            case 0:
                if (userName != null) return userName;
            case 1:
                return "User not found";
        }
        return (userID == null && userName != null) ? "Error" : userName;
    }

    public final String getUserID() {
        return userID;
    }

    public ArrayList<String> getOutItems() {
        return loggingDatabase.getOutItems();
    }

    /**
     * Takes the barcode for a product and adds it to the checkout
     *
     * @param input The barcode for the product as a string
     * @return True if the product was added, false if it failed
     */
    public final boolean addToCart(String input) {
        String tempBarCode = "-1";
        if (input != null && !input.equals("")) {
            tempBarCode = input; // disallows the user from entering nothing or clicking cancel.
        } else if ((input == null) || ("".equals(input))) {
            return false;
        }
        String adding = itemDatabase.getEntryName(tempBarCode);
        if (adding != null) {
            System.out.println(tempBarCode + "\n" + adding);
            checkOuts.addProduct(tempBarCode, adding); //otherwise, add the product as normal.
            return true;
        } else if (userID.equals(tempBarCode)) {
            adding = "Checking yourself out are you? You can't do that.";
            checkOuts.addProduct("-1", adding);
            return true;
        }
        return false;
    }

    /**
     * Get the permission role of the user which is currently logged in.
     *
     * @return The role of the user. 0 for user, 2 for admin, 3 for root (full access)
     */
    public final int getRole() {
        if (userID == null || userName == null) return 0;
        return personDatabase.getRole(userID);
    }

    /**
     * Add a person to the database, given their name and PMKeyS
     *
     * @param name The name of the person you wish to add
     * @param ID   The PMKeyS of the person you wish to add
     */
    public final void addPersonToDatabase(String name, String ID, String password) {
        String[] passwd = new String[0];
        try {
            passwd = getSecurePassword(password, getSalt());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        personDatabase.setEntry(ID, name, PersonDatabase.USER, passwd[0], passwd[1]);
    }

    /**
     * Add barcode product to the database given their name, barcode and price
     *
     * @param name    The name of the product you wish to add
     * @param barcode The barcode for the product you wish to add.
     */
    public final void addItemToDatabase(String barcode, String name) {
        itemDatabase.addEntry(barcode, name);
    } //TODO: make this work for general and controlled items

    /**
     * Alter a product in the database
     *
     * @param name       The new name of the product
     * @param oldName    The old name of the product
     * @param barcode    The new barcode of the product
     * @param oldBarcode The old barcode of the product
     */
    public final void changeDatabaseProduct(String name, String oldName, String barcode, String oldBarcode) {
        itemDatabase.changeItem(oldName, barcode, oldBarcode);
    }

    /**
     * Alter a product in the database
     *
     * @param selectedIndex
     * @param name          The new name of the person
     * @param ID            The new PMKeyS of the person
     * @param oldID         The old PMKeyS of the person
     */
    public final void changeDatabasePerson(String selectedIndex, String name, long ID, long oldID) {
//        personDatabase.changeDatabasePerson(selectedIndex, name, ID, oldID);
    }

    /**
     * Write out the CSV version of the database for the admin.
     *
     * @param type SQLInterface.TABPERSON for the person database or "Produt" for the product database
     * @throws IOException
     */
    public final void adminWriteOutDatabase(String type) {
        switch (type) {
            case (SQLInterface.TABPERSON):
                personDatabase.writeDatabaseCSV("adminPersonDatabase.csv");
                break;
            case ("Item"): //TODO: change all instances of this in interface from product to item.
                itemDatabase.writeDatabaseCSV(type, "adminItemDatabase.csv");
                break;
            case ("general"):
                itemDatabase.writeDatabaseCSV(type, "adminGeneralDatabase.csv");
                break;
            case ("controlled"):
                itemDatabase.writeDatabaseCSV(type, "adminControlledDatabase.csv");
                break;
            default:
                personDatabase.writeDatabaseCSV("adminItemDatabase.csv");
        }
    }

    /**
     * Write the given type of database out to a CSV file at the given location.
     *
     * @param type The type of database to write. Person or Item (general or controlled)
     * @param path The location which the file will be stored at
     */
    public final void adminWriteOutDatabase(String type, String path) {
        switch (type) {
            case (SQLInterface.TABPERSON):
                personDatabase.writeDatabaseCSV(path);
                break;
            default:
                itemDatabase.writeDatabaseCSV(type, path);
        }
    }

    /**
     * Delete the specified person
     *
     * @param ID The PMKeyS or name of the person as a string
     * @throws IOException
     * @throws InterruptedException
     */
    public final void removePerson(String ID, String rootID, String rootPasswd) throws IOException, InterruptedException {
        if (passwordsEqual(rootID, rootPasswd)) {
            personDatabase.deleteEntry(ID);
        }
    }

    /**
     * Remove the specified product
     *
     * @param ID The barcode or name of the product to be removed
     * @throws IOException
     * @throws InterruptedException
     */
    public final void removeItem(String ID) throws IOException, InterruptedException {
        itemDatabase.deleteEntry(ID);
    }

    /**
     * Delete an item, requires root user/password
     *
     * @param ID         The ID of the item to delete
     * @param rootID     The ID of the root user attempting to delete the item.
     * @param rootPassWd The password of the root user attempting to delete the item.
     */
    public final void removeItem(String ID, String rootID, String rootPassWd) {
        if (passwordsEqual(rootID, rootPassWd)) {
            itemDatabase.deleteEntry(ID);
        }
    }

    /**
     * Delete a product from the checkout given it's barcode in the checkout array
     *
     * @param barcode The index of the item to delete in the checkout array
     */
    public final void deleteProduct(int barcode) {
        checkOuts.delItem(barcode);
    }

    /**
     * Get the barcode of a product given it's name
     *
     * @param name The name of the product to get the barcode of
     * @return The barcode of the product with the name specified.
     */
    public final String getProductBarCode(String name) {
        return itemDatabase.getBarcode(name);
    }

    /**
     * Get the name of a product given it's barcode
     *
     * @param barcode The barcode of the product as a string
     * @return The name of the product with the given barcode
     */
    public final String getProductName(String barcode) {
        return itemDatabase.getEntryName(barcode);
    }

    /**
     * Get the number of a product left in stock
     *
     * @param ID The name of the product you wish to check stock count.
     * @return The number of the specified product in stock
     */
    public final int getProductNumber(String ID) {
        return itemDatabase.getNumber(ID);
    }

    /**
     * set the number of a product in stock
     *
     * @param ID               The name of the product you wish to set the stock count for
     * @param numberOfProducts The new stock count.
     */
    public final void setNumberOfProducts(String ID, int numberOfProducts) {
        itemDatabase.setNumber(ID, numberOfProducts);
    }

    /**
     * Determine whether there is a user logged in
     *
     * @return The boolean value of whether the user is logged in.
     */
    public final boolean userLoggedIn() {
        return (userID != null && userName != null);
    }

    /**
     * Determine whether a person exists in the database.
     *
     * @param ID The ID of the person to check for.
     * @return Boolean of does the member exist in the database.
     */
    public boolean PersonExists(String ID) {
        return personDatabase.entryExists(ID);
    }

    public LinkedList<String> getInItems() {
        return checkOuts.getCheckOutNames();
    }

    public String getItemName(String barcode) {
        return itemDatabase.getEntryName(barcode);
    }

    public void signItemsIn(ArrayList<String> items) {
        loggingDatabase.signItemsIn(items, userID);
    }

    public boolean itemExists(String barcode) {
        return itemDatabase.entryExists(barcode);
    }
}
