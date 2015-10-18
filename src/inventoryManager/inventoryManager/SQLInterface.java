package inventoryManager;

/***
 * TOC19 is a simple program to run TOC payments within a small group.
 * Copyright (C) 2014  Jarrah Gosbell
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Author: Jarrah Gosbell
 * Student Number: z5012558
 * Class: PersonDatabase
 * Description: This program will allow for the input and retreval of the person database and will set the limits of the database.
 */


import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class SQLInterface {
    // Table names:
    public static final String TABCONTROLLED = "controlled";
    public static final String TABCONTROLLEDTYPE = "controlledType";
    public static final String TABGENERAL = "general";
    public static final String TABITEM = "item";
    public static final String TABITEMLOG = "itemLog";
    public static final String TABPERSON = "person";
    public static final String TABPERSONLOG = "personLog";
    public static final String TABSET = "itemSet";
    // column names TABCONTROLLED
    private final String COLCONTROLLEDID = "ID";
    private final String COLCONTROLLEDTAGNO = "tagno";
    private final String COLCONTROLLEDTYPE = "type";
    private final int TABCONTROLLEDCOUNT = 3;
    // column names TABCONTROLLEDTYPE
    private final String COLCONTROLLEDTYPEID = "ID";
    private final String COLCONTROLLEDTYPENAME = "name";
    private final int TABCONTROLLEDTYPECOUNT = 2;
    // Column names TABGENERAL
    private final String COLGENERALID = "ID";
    private final String COLGENERALDESCRIPTION = "description";
    private final String COLGENERALQUANTITY = "quantity";
    private final String COLGENERALLOCATION = "location";
    private final int TABGENERALCOUNT = 3;
    // Column names TABITEM
    private final String COLITEMID = "ID";
    private final String COLITEMNAME = "name";
    private final String COLITEMSETID = "setID";
    private final int TABITEMCOUNT = 3;
    // Column names TABITEMLOG
    private final String COLITEMLOGID = "ID";
    private final String COLITEMLOGOUTDATE = "outDate";
    private final String COLITEMLOGOUT = "out";
    private final String COLITEMLOGINDATE = "inDate";
    private final String COLITEMLOGPERSID = "persID";
    private final String COLITEMLOGCONTROLLED = "controlled";
    private final String COLITEMLOGADMINNAME = "adminName";
    private final int TABITEMLOGCOUNT = 7;
    // Column names TABPERSON
    private final String COLPERSONID = "ID";
    private final String COLPERSONNAME = "name";
    private final String COLPERSONADMIN = "admin";
    private final String COLPERSONPASSOWRD = "password";
    private final String COLPERSONSALT = "salt";
    private final int TABPERSONCOUNT = 5;
    // Column names TABPERSONLOG
    private final String COLPERSONLOGPERSID = "persID";
    private final String COLPERSONLOGDATE = "changeDate";
    private final String COLPERSONLOGAUTHNAME = "authName";
    private final int TABPERSONLOGCOUNT = 3;
    // Column names TABSET
    private final String COLSETID = "ID";
    private final String COLSETNAME = "name";
    private final int TABSETCOUNT = 2;
    private String URL = "jdbc:sqlite:/Users/jarrah/ideaProjects/inventoryManager/inv.db"; // these will be initialised from the file.
    private String user = "jarrah"; // when sure it works, remove these.
    private String password = "password";
    private Connection db;


    public SQLInterface() {
        try {
            Class.forName("org.sqlite.JDBC").newInstance();
        } catch (ClassNotFoundException e) {
//            Log.print("could not find driver class\n" + e.toString());
        } catch (InstantiationException | IllegalAccessException e) {
//            Log.print("could not create instance\n" + e.toString());
        }
        String[] settings = new String[0];
        try {
            settings = Settings.SQLInterfaceSettings();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
///        URL = settings[0];
        try {
            db = DriverManager.getConnection(URL);
            System.out.println(db);
        } catch (java.sql.SQLException e) {
        }
    }

    public void deleteEntry(String type, String barcode) {
        String statement = "";
        switch (type) {
            case "person":
                statement = "DELETE FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
                break;
            case "GeneralItem":
                statement = "DELETE FROM " + TABGENERAL + " WHERE " + COLGENERALID + " = ?";
                break;
            case "controlledItem":
                statement = "DELETE FROM " + TABCONTROLLED + " WHERE " + COLCONTROLLEDID + " = ?"; // TODO: this will delete controlled but not item. Use the key and a delete on cascade.
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEntry(String ID, String name, int admin, String password, String salt) { // add a new person
        String statement = "INSERT INTO " + TABPERSON + " (" + COLPERSONID + ", " + COLPERSONNAME + ", " + COLPERSONADMIN + ", " + COLPERSONPASSOWRD + ", " + COLPERSONSALT + ")" +
                "VALUES(?, ?, ?, ?, ?)";
        System.out.println(statement + ID + name + admin + password + salt);
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.setInt(3, admin);
            ps.setString(4, password);
            ps.setString(5, salt);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public void addEntry(String name) // add new set
    {
        String statement = "INSERT INTO " + TABSET + " (" + COLSETNAME + ") VALUES(?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public void addEntry(String ID, String name, String setName, String Description, Long Quantity) { // Add generalItem
        String statement = "INSERT INTO " + TABITEM + " (" + COLITEMID + ", " + COLITEMNAME + ")" +
                "VALUES(?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        statement = "INSERT INTO " + TABGENERAL + " (" + COLGENERALID + ", " + COLGENERALDESCRIPTION + ", " + COLGENERALQUANTITY + ")" +
                "VALUES(?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, Description);
            ps.setLong(3, Quantity);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        if (setName != null && !setName.isEmpty()) {
            statement = "SELECT " + COLSETID + " FROM " + TABSET + " WHERE " + COLSETNAME + " = ?";
            ResultSet rs = null;
            try {
                PreparedStatement ps = db.prepareStatement(statement);
                ps.setString(1, setName);
                rs = ps.executeQuery();
                ps.closeOnCompletion();
                if (rs.next()) {
                    statement = ""; // TODO: List of items is fucked.
                    ps = db.prepareStatement(statement);
                    ps.setString(1, name);
                }
                rs.close();
            } catch (SQLException e) {
                Log.print(e);
            }

        }
    }

    public void addEntry(String ID, String name, String setName, String state, String tagpos, String type) { // add Controlled " + TABITEM + "
        String statement = "INSERT INTO " + TABITEM + " (" + COLITEMID + ", " + COLITEMNAME + ")" +
                "VALUES(?, ?)"; // Sort SetID at the end. There may not be a set ID for every item.
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        if (type != null && !type.isEmpty()) {
            statement = "SELECT " + COLCONTROLLEDTYPEID + " FROM " + TABCONTROLLEDTYPE + " WHERE " + COLCONTROLLEDTYPENAME + " = ?";
            ResultSet rs = null;
            try {
                PreparedStatement ps = db.prepareStatement(statement);
                ps.setString(1, type);
                rs = ps.executeQuery();
                ps.closeOnCompletion();

                if (!rs.next()) {
                    statement = "INSERT INTO " + TABCONTROLLEDTYPE + "(?)";
                    ps = db.prepareStatement(statement);
                    ps.setString(1, type);
                    ps.execute();
                    ps.closeOnCompletion();
                    statement = "SELECT " + COLCONTROLLEDTYPEID + " FROM " + TABCONTROLLEDTYPE + " WHERE " + COLCONTROLLEDTYPENAME + " = ?";
                    ps = db.prepareStatement(statement);
                    ps.setString(1, type);
                    rs = ps.executeQuery();
                    ps.closeOnCompletion();
                }
                rs.close();

                statement = "INSERT INTO " + TABCONTROLLED + " (" + COLCONTROLLEDID + ", " + COLCONTROLLEDTAGNO + ", State)" + // TODO: DAFAQ is state
                        "VALUES(?, ?, ?, ?, ?)";
                try {
                    ps = db.prepareStatement(statement);
                    ps.setString(1, ID);
                    ps.setString(2, tagpos);
                    ps.setInt(3, rs.getInt("ID"));
                    ps.setString(4, state);
                    ps.execute();
                    ps.closeOnCompletion();
                } catch (SQLException e) {
                    Log.print(e);
                }
            } catch (SQLException e) {
                Log.print(e);
            }
        }
        if (setName != null && !setName.isEmpty()) {
            statement = "SELECT " + COLSETID + " FROM " + TABSET + "s WHERE " + COLSETNAME + " = ?";
            ResultSet rs = null;
            try {
                PreparedStatement ps = db.prepareStatement(statement);
                ps.setString(1, setName);
                rs = ps.executeQuery();
                ps.closeOnCompletion();
                if (rs.next()) {
                    statement = ""; // TODO: List of items is fucked.
                    ps = db.prepareStatement(statement);
                    ps.setString(1, name);
                    ps.execute();
                    ps.closeOnCompletion();
                }
                rs.close();
            } catch (SQLException e) {
                Log.print(e);
            }

        }
    }

    public void addEntry(String ID, String name) {
        String statement = "INSERT INTO " + TABITEM + " (" + COLITEMID + ", " + COLITEMNAME + ") " +
                "VALUES(?, ?)";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setString(2, name);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public void addLog(String persID, String adminID) { // add to change password log.
        String statement = String.format("INSERT INTO %s (%s, %s, %s) VALUES(?, NOW(), (SELECT %s FROM %s WHERE %s = ?)", TABPERSONLOG, COLPERSONLOGPERSID, COLPERSONLOGDATE, COLPERSONLOGAUTHNAME, COLPERSONNAME, TABPERSON, COLPERSONID);
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, persID);
            ps.setString(2, adminID);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }

    }

    public void addLog(String itemID, String persID, boolean controlled) { // Sign an item out
        //TODO: Should this check the item as out in the controlled table?
        String statment = "INSERT INTO " + TABITEMLOG + " (" + COLITEMLOGID + ", " + COLITEMLOGOUTDATE + ", " + COLITEMLOGOUT + ", " + COLITEMLOGINDATE + ", " + COLITEMLOGPERSID + ", " + COLITEMLOGCONTROLLED + ") " +
                "VALUES(?, NOW(), TRUE, \"FALSE\", ?, ?)"; // TODO: inDate may be setting gibberish
        try {
            PreparedStatement ps = db.prepareStatement(statment);
            ps.setString(1, itemID);
            ps.setString(2, persID);
            ps.setBoolean(3, controlled);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public void returnItem(String itemID) { // Return an item
        String statement = "UPDATE " + TABITEMLOG + " SET in=TRUE, inDate=NOW()" +
                "WHERE ID=?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, itemID);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public void returnItem(String itemID, String persID) { // Return a general item.
        String statement = "UPDATE " + TABITEMLOG + " SET in=TRUE, inDate=NOW()" +
                "WHERE ID=? AND persID=?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, itemID);
            ps.setString(2, persID);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    /**
     *<b>On Logs</b>
     * The logs in this system are created in the following suituations:
     * <ul>
     *     <li>Password reset</li>
     *     <li>Item signed out</li>
     * </ul>
     * Thus, logs will be available for people (passwords), and item.
     * Logs will be condensed by item type, date and ID.
     */
    public ArrayList<String> getLog(String type) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM " + TABPERSONLOG + " ";
                break;
            case "item":
                statement = "SELECT * FROM " + TABITEMLOG + "";
                break;
            case "controlled":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=TRUE";
                break;
            case "general":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=FALSE";
                break;
            default:
                statement = "SELECT * FROM " + TABITEMLOG + "";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    public ArrayList<String> getLog(String type, String ID) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM " + TABPERSONLOG + " " +
                        "WHERE " + COLPERSONLOGPERSID + " = ?";
                break;
            case "item":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGID + " = ?";
                break;
            case "controlled":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=TRUE AND " +
                        COLITEMLOGID + " = ?";
                break;
            case "general":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=FALSE AND " +
                        COLITEMLOGID + " = ?";
                break;
            default:
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGID + " = ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString.
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    public ArrayList<String> getLog(String type, LocalDate date) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM " + TABPERSONLOG + " " +
                        "WHERE " + COLPERSONLOGDATE + " > ?";
                break;
            case "item":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGOUTDATE + " > ?";
                break;
            case "controlled":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=TRUE AND " +
                        COLITEMLOGOUTDATE + " > ?";
                break;
            case "general":
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGCONTROLLED + "=FALSE AND " +
                        COLITEMLOGOUTDATE + " >  ?";
                break;
            default:
                statement = "SELECT * FROM " + TABITEMLOG + " " +
                        "WHERE " + COLITEMLOGOUTDATE + " > ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setDate(1, java.sql.Date.valueOf(date));
            rs = ps.executeQuery();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString. see above.
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    public ArrayList<String> getOutItemsLog() {
        String statement = "SELECT * FROM " + TABITEMLOG + " WHERE " + COLITEMLOGOUT + " = 1";
        ResultSet rs;
        ArrayList<String> ret = new ArrayList<>();
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            while (rs.next()) {
                ret.add(rs.toString());
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    public ArrayList<String> getDatabase(String type) {
        String statement;
        ResultSet rs = null;
        int count = 0;
        switch (type) {
            case TABPERSON:
                statement = "SELECT * FROM " + TABPERSON + " ";
                count = TABPERSONCOUNT;
                break;
            case TABITEM:
                statement = "SELECT * FROM " + TABITEM + " i" +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                count = TABITEMCOUNT;
                break;
            case TABCONTROLLED:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                count = TABCONTROLLEDCOUNT;
                break;
            case TABGENERAL:
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID";
                count = TABGENERALCOUNT;
                break;
            default:
                statement = "SELECT * FROM " + TABITEM + " i" +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                count = TABITEMCOUNT; //TODO: this needs to be added to general and item. Work out what the joins will return.
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = new ArrayList<>();
        StringBuilder line = new StringBuilder();
        try {
            if (rs != null) {
                while (rs.next()) {
                    line = new StringBuilder();
                    for (int i = 1; i <= count; i++) {
                        line.append(rs.getString(i));
                    }
                    ret.add(line.toString());
                }
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    public ArrayList<String> getDatabase(String type, String ID) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM " + TABPERSON + " WHERE ID=?";
                break;
            case "item":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE ID = ?";
                break;
            case "controlled":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE ID = ?";
                break;
            case "general":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID" +
                        " WHERE ID = ?";
                break;
            case "persGeneral":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID" +
                        " WHERE persID = ?";
                break;
            case "persControlled":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE persID = ?";
                break;
            default:
                statement = "SELECT * FROM " + TABITEM + " i" +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE ID = ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return ret;
    }

    public ArrayList<String> getDatabase(String type, LocalDate date) {
        String statement;
        ResultSet rs = null;
        switch (type) {
            case "person":
                statement = "SELECT * FROM " + TABPERSON + " WHERE ID=?";
                break;
            case "item":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE date > ?";
                break;
            case "controlled":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE date > ?";
                break;
            case "general":
                statement = "SELECT * FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID" +
                        " WHERE date > ?";
                break;
            default:
                statement = "SELECT * FROM " + TABITEM + " i" +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID" +
                        " WHERE date > ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setDate(1, java.sql.Date.valueOf(date));
            rs = ps.executeQuery();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
        ArrayList<String> ret = null;
        try {
            for (int i = 0; rs.next(); i++) {
                ret.add(rs.toString()); // TODO: test this toString
            }
            rs.close();
        } catch (SQLException e) { //TODO: why are there two try catch blocks here. merge.
            Log.print(e);
        }
        return ret;
    }

    public void lowerQuantity(String ID, int sub) {
        String statement = "UPDATE " + TABGENERAL + " SET " + COLGENERALQUANTITY + " = " +
                "((SELECT " + COLGENERALQUANTITY + " FROM " + TABGENERAL + " WHERE " + COLGENERALID + " = ?) - ?)" +
                " WHERE " + COLGENERALID + " = ?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            ps.setInt(2, sub);
            ps.setString(3, ID);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public String getName(String type, String ID) {
        String statement;
        ResultSet rs;
        String out = "";
        switch (type) {
            case "person":
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + " WHERE " + COLPERSONID + "=?";
                break;
            case "item":
                statement = "SELECT " + COLITEMNAME + " FROM " + TABITEM + "" +
                        " WHERE " + COLITEMID + " = ?";
                break;
            default:
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + " WHERE " + COLPERSONID + "=?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            if (rs.next()) {
                out = rs.getString(1);
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return out;
    }

    public ArrayList<String> getName(String type) {
        String statement;
        ResultSet rs;
        ArrayList<String> out = new ArrayList<>();
        switch (type) {
            case TABPERSON:
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + "";
                break;
            case TABITEM:
                statement = "SELECT " + COLITEMNAME + " FROM " + TABITEM + "";
                break;
            case TABGENERAL:
                statement = "SELECT " + COLITEMNAME + " FROM " + TABGENERAL +
                        " JOIN " + TABITEM + " ON " + TABGENERAL + "." + COLGENERALID +
                        " = " + TABITEM + "." + COLITEMID + ";";
                break;
            default:
                statement = "SELECT " + COLPERSONNAME + " FROM " + TABPERSON + "";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            while (rs.next()) {
                out.add(rs.getString(1));
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return out;
    }

    public String getID(String type, String name) {
        String statement;
        ResultSet rs;
        String out = "";
        switch (type) {
            case "person":
                statement = "SELECT " + COLPERSONID + " FROM " + TABPERSON + " WHERE " + COLPERSONNAME + "=?";
                break;
            case "item":
                statement = "SELECT " + COLITEMID + " FROM " + TABITEM + "" +
                        " WHERE " + COLITEMNAME + " = ?";
                break;
            default:
                statement = "SELECT " + COLPERSONID + " FROM " + TABPERSON + " WHERE " + COLPERSONNAME + " = ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            if (rs.next()) {
                out = rs.getString(0);
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return out;
    }

    public String[] getPassword(String barcode) {
        String statement = "SELECT " + COLPERSONPASSOWRD + ", " + COLPERSONSALT + " FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
        ResultSet rs;
        String[] out = new String[2];
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            if (rs.next()) {
                out[0] = rs.getString(COLPERSONPASSOWRD);
                System.out.println("Password: " + out[0]);
                out[1] = rs.getString(COLPERSONSALT);
                System.out.println("Salt: " + out[1]);
            } else System.out.print("userNotFound");
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return out;
    }

    public void setPassword(String ID, String password, String salt) {
        String statement = "UPDATE " + TABPERSON + " SET " + COLPERSONPASSOWRD + " = ?, " + COLPERSONSALT + " = ? WHERE " + COLPERSONID + " = ?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, password);
            ps.setString(2, salt);
            ps.setString(3, ID);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }

    }

    public int getRole(String barcode) {
        String statement = "SELECT " + COLPERSONADMIN + " FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
        ResultSet rs;
        int admin = 0;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, barcode);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            if (rs.next()) {
                admin = rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return admin;
    }

    public boolean entryExists(String type, String ID) {
        String statement;
        ResultSet rs;
        switch (type) {
            case "person":
                statement = "SELECT " + COLPERSONID + " FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
                break;
            case "item":
                statement = "SELECT " + COLITEMID + " FROM " + TABITEM + " WHERE " + COLITEMID + " = ?";
                break;
            default:
                statement = "SELECT " + COLPERSONID + " FROM " + TABPERSON + " WHERE " + COLPERSONID + " = ?";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            //System.out.println(rs);
            if (rs.next()) {
                return rs.getString(COLPERSONID).equals(ID);
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return false;
    }

    public void export(String type, String path) {
        String statement;
        switch (type) {
            case "person":
                statement = "SELECT * INTO OUTFILE \'?\' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' " +
                        "FROM " + TABPERSON + "";
                break;
            case "item":
                statement = "SELECT * INTO OUTFILE '?' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                break;
            case "controlled":
                statement = "SELECT * INTO OUTFILE '?' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                break;
            case "general":
                statement = "SELECT * INTO OUTFILE '?' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g " +
                        "ON i.ID = g.ID";
                break;
            default:
                statement = "SELECT * INTO OUTFILE '?' " +
                        "FIELDS TERMINATED BY ',' OPTIONALLY ENCLOSED BY \'\"\' " +
                        "LINES TERMINATED BY \'\n\' FROM " + TABITEM + " i " +
                        "INNER JOIN " + TABGENERAL + " g" +
                        " ON i.ID = g.ID" +
                        "INNER JOIN " + TABCONTROLLED + " c " +
                        "ON i.ID = c.ID";
                break;
        }
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, path);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public int getQuantity(String ID) {
        String statement = "SELECT " + COLGENERALQUANTITY + " FROM " + TABGENERAL + " WHERE " + COLGENERALID + " = ?";
        ResultSet rs = null;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return 0;
    }

    public void setQuantity(String ID, int quantity) {
        String statement = "UPDATE " + TABGENERAL + " SET " + COLGENERALQUANTITY + "=?  WHERE " + COLGENERALID + " = ?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setInt(1, quantity);
            ps.setString(2, ID);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public void updateEntry(String ID, String name, String newID) {
        String statement = "UPDATE " + TABITEM + " SET " + COLITEMNAME + " = ?, set " + COLITEMID + " = ? " +
                "WHERE " + COLITEMID + " = ?";
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, name);
            ps.setString(2, newID);
            ps.setString(3, ID);
            ps.execute();
            ps.closeOnCompletion();
        } catch (SQLException e) {
            Log.print(e);
        }
    }

    public boolean isItemControlled(String ID) {
        String statement = "SELECT " + COLCONTROLLEDID + " FROM " + TABCONTROLLED + " WHERE " + COLCONTROLLEDID + " = ?";
        ResultSet rs;
        try {
            PreparedStatement ps = db.prepareStatement(statement);
            ps.setString(1, ID);
            rs = ps.executeQuery();
            ps.closeOnCompletion();
            if (rs.next()) {
                return true;
            }
            rs.close();
        } catch (SQLException e) {
            Log.print(e);
        }
        return false;
    }

}
