package com.github.taller.lu.impl;


import com.github.taller.lu.interfaces.Command;
import com.github.taller.lu.interfaces.ConsoleCommand;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class MKDIR extends AbstractCommand implements ConsoleCommand {

    private Set<String> dirToCreate = new HashSet<>();

    private boolean makeParents = false;

    static {
        usage = "jmkdir [-p] [dir1] [dir2] ...\n" +
                "Create directories 'dir1', 'dir2', etc.";
        knownKeys.add("--help\t \tPrints this help.");
        knownKeys.add("-p\t \tNo error if existing, make parent directories as needed.");
    }

    public MKDIR(String[] args) {
        super(args);
    }

    @Override
    public void runCommand() {
        if (showHelp || dirToCreate.isEmpty()) {
            help();
            return;
        }

        for (String dir : dirToCreate) {
            File dirToCreate = new File(dir);

            if (dirToCreate.exists()) {
                if (!makeParents) {
                    output.add("Can't create " + dir + ". File or directory already exists.");
                }
                continue;
            }

            if (makeParents) {
                if (!dirToCreate.mkdirs()) {
                    output.add("Can't create directory " + dir);
                }
            } else {
                if (!dirToCreate.mkdir()) {
                    output.add("Can't create directory " + dir);
                }
            }

        }
    }

    @Override
    public void parseParameters(String[] args) {
        if (args.length == 0) {
            showHelp = true;
            return;
        }

        for (String param : args) {
            if (param.equals("--help")) {
                showHelp = true;
                break;
            } else if ("-p".equals(param)) {
                makeParents = true;
            } else if (param.startsWith("--")) {
                dirToCreate.add(param.substring(1));
            } else {
                dirToCreate.add(param);
            }
        }
    }

    public static void main(String[] args) {
        Command command = new MKDIR(args);
        command.start();
    }
}
