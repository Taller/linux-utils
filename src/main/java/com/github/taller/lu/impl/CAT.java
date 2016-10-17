
package com.github.taller.lu.impl;


import com.github.taller.lu.interfaces.ConsoleCommand;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class CAT extends AbstractCommand implements ConsoleCommand {

    private Set<String> foundFiles = new LinkedHashSet<>();

    private static final int BUFFER_SIZE = 1_000_000; // about 1Mb

    static {
        usage = "jcat [files]\n" +
                "Outputs 'files' content.";
        knownKeys.add(new KeyDescriptions("--help", "Prints this help."));
    }

    public CAT(String[] args) {
        super(args);
    }

    @Override
    public void runCommand() {
        if (showHelp || foundFiles.isEmpty()) {
            help();
            return;
        }

        for (String fname : foundFiles) {
            try (FileInputStream fis = new FileInputStream(fname);
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 DataInputStream dis = new DataInputStream(bis);) {
                byte[] buffer = new byte[BUFFER_SIZE];
                int actualRead;
                while ((actualRead = dis.read(buffer)) != -1) {
                    String strFileContents = new String(buffer, 0, actualRead);
                    LOGGER.debug(strFileContents);
                }

            } catch (IOException e) {
                output.add("File " + fname + "\n" + e.getMessage());
            }
        }

    }

    @Override
    public void parseParameters(String[] args) {
        for (String param : args) {
            if (param.equals("--help")) {
                showHelp = true;
                break;
            } else if (param.startsWith("--")) {
                foundFiles.add(param.substring(1));
            } else {
                foundFiles.add(param);
            }
        }
    }

    public static void main(String[] args) {
        CAT c = new CAT(args);
        c.start();
    }
}
