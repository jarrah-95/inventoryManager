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

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Jarrah Gosbell
 */

public class Log {
    /**
     * The location to store the log in
     */
    private static String logLocation = "./";
    /**
     * The file writer which will append to the log
     */
    private static FileWriter fwriter;
    /**
     * The buffered writer which will write to the log
     */
    private static BufferedWriter bwriter;
    /**
     * whether an exception occurred the first time. Stops recursive loops
     */
    private static boolean first = true;
    /**
     * Whether to print to the database or to the console. True for console
     */
    private static boolean debug = true; // print to the console.

    /**
     * Set up the logLocation and writers ready to write the log
     */
    private Log() {
        try {
            logLocation = Settings.logSettings();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fwriter = new FileWriter(logLocation + "LOG", true);
            bwriter = new BufferedWriter(fwriter);
        } catch (IOException e) {
            print(e);
        }
    }

    /**
     * Turn a throwable into a string stack trace
     *
     * @param e The throwable to be traced and turned into a string
     * @return a string containing the stack trace of e
     */
    private static String stackTraceToString(Throwable e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Print the stacktrace of a throwable to the log
     *
     * @param e The throwable which will be traced and printed to the log
     */
    public static void print(Exception e) {
        if (!debug) {
            if (bwriter == null) new Log();
            String stackTrace = stackTraceToString(e);
            try {
                bwriter.write(stackTrace, 0, stackTrace.length());
                bwriter.newLine();
                bwriter.flush();
            } catch (IOException e1) {
                if (first) {
                    first = false;
                    print(e1);
                } else {
                    first = true;
                    e1.printStackTrace();
                    return;
                }
            }
        } else e.printStackTrace();
    }

    /**
     * Print a predefined string to the Log
     *
     * @param s The string to print to the log.
     */
    public static void print(String s) {
        if (!debug) {
            if (bwriter == null) new Log();
            try {
                bwriter.write(s, 0, s.length());
                bwriter.newLine();
                bwriter.flush();
            } catch (IOException e1) {
                if (first) {
                    first = false;
                    print(e1);
                } else {
                    first = true;
                    e1.printStackTrace();
                    return;
                }
            }
        } else System.out.println(s);
    }
}
