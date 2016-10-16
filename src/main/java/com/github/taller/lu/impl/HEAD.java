package com.github.taller.lu.impl;


import com.github.taller.lu.interfaces.Command;
import com.github.taller.lu.interfaces.ConsoleCommand;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class HEAD extends AbstractCommand implements ConsoleCommand {

    private Set<String> foundFiles = new LinkedHashSet<>();

    private int headSize;

    static {
        usage = "jhead [-n] [files]";
        knownKeys.add(new KeyDescriptions("--help", "Prints this help."));
        knownKeys.add(new KeyDescriptions("-n", "Print the first 'n' lines instead of the first 10."));
    }


    public HEAD(String[] args) {
        super(args);
    }

    @Override
    public void runCommand() {
        if (showHelp || foundFiles.isEmpty()) {
            help();
            return;
        }

        for (String fname : foundFiles) {

            try (BufferedReader br = new BufferedReader(new FileReader(fname))) {
                String line;
                int count = headSize;
                while ((line = br.readLine()) != null && count > 0) {
                    LOGGER.debug("{}", line);
                    count--;
                }
            } catch (IOException e) {
                output.add("File " + fname + "\n" + e.getMessage());
            }
        }
    }

    @Override
    public void parseParameters(String[] args) {
        if (args.length == 0) {
            showHelp = true;
            return;
        }

        try {
            headSize = Integer.valueOf(args[0].substring(1));
        } catch (NumberFormatException nfex) {
            headSize = 10;
        }

        LinkedList<String> argList = new LinkedList<>(Arrays.asList(args));
        argList.removeFirst();

        for (String param : argList) {
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
        Command command = new HEAD(args);
        command.start();
    }
}
