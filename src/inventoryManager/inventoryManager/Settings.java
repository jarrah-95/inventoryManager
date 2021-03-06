/*
 * Copyright (C) 2014  Jarrah Gosbell
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General public static  License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General public static  License for more details.
 * <p>
 * You should have received a copy of the GNU General public static  License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package inventoryManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

/*
 * Author: Jarrah Gosbell
 * Student Number: z5012558
 * Class: PersonDatabase
 * Description: This program will allow for the input and retreval of the person database and will set the limits of the database.
 */

final class Settings {
    /**
     * The properties object which is used to interact with the properties file
     */
    private static final Properties properties = new Properties();
    /**
     * the path to the properties file which contains the settings
     */
    private static final String propFileName = "inventoryManager.properties";
    /**
     * an input stream which is used to access the properties file
     */
    private static FileInputStream inputStream;

    static {
        try {
            inputStream = new FileInputStream(propFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * Create an instance of the settings class from which to read settings from.
     */
    private Settings() {
        if (inputStream != null) return;

        try {
            if (inputStream == null) {
                inputStream = new FileInputStream(String.valueOf(Paths.get(propFileName)));
            }
            if (inputStream == null) {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
        } catch (FileNotFoundException e) {
            //	Log.print(e);
        }
    }

    /**
     * Get the settings for the person database, specifically the location to store the database
     *
     * @return The location in which the database is stored. This is checked for compatibility against the running OS
     * @throws FileNotFoundException if the settings file is not in the location it should be.
     */
    @SuppressWarnings("Duplicates")
    public static String personSettings() throws FileNotFoundException {
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                Log.print("property file '" + propFileName + "' not " +
                        "found in the classpath");
            }
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        String output;
        output = properties.getProperty("personDatabaseLocation");
        output = Compatibility.getFilePath(output);
        return output;
    }

    /**
     * Get the settings for the product datasbase, specifically the location to store the database in.
     *
     * @return The location in which the database is stored. This is checked for compatibility against the running OS
     * @throws FileNotFoundException If the settings file is not in the location it should be.
     */
    @SuppressWarnings("Duplicates")
    public static String productSettings() throws FileNotFoundException {
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                Log.print("property file '" + propFileName + "' not " +
                        "found in the classpath");
            }
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        String output;
        output = properties.getProperty("productDatabaseLocation");
        output = Compatibility.getFilePath(output);
        return output;
    }

    /**
     * Get the settings for the interface. Specifically the horizontal size, vertical size, (both in pixels) and the text size
     *
     * @return A string array with the horizontal size, vertical size and textsize.
     * @throws FileNotFoundException If the settings file is not in the location it should be.
     */
    @SuppressWarnings("Duplicates")
    public static String[] interfaceSettings() throws FileNotFoundException {
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                Log.print("property file '" + propFileName + "' not " +
                        "found in the classpath");
            }
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        String[] output = new String[3];
        output[0] = properties.getProperty("horizontalSize");
        output[1] = properties.getProperty("verticalSize");
        output[2] = properties.getProperty("textSize");
        return output;
    }

    /**
     * Get the settings for the error log. Specifically the location of it's storage
     *
     * @return A string with the location of the log. This is checked for compatibility against the running OS
     * @throws FileNotFoundException If the settings file is not in the location it should be.
     */
    @SuppressWarnings("Duplicates")
    public static String logSettings() throws FileNotFoundException {
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                //	Log.print("property file '" + propFileName + "' not " +
                //        "found in the classpath");
            }
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }

        String output;
        output = properties.getProperty("logFileLocation");
        output = Compatibility.getFilePath(output);
        return output;
    }




}
