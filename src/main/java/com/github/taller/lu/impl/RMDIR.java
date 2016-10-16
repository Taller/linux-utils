package com.github.taller.lu.impl;


import com.github.taller.lu.interfaces.ConsoleCommand;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

public class RMDIR extends AbstractCommand implements ConsoleCommand {
    private Set<String> dirsToRemove = new LinkedHashSet<>();

    static {
        usage = "jrmdir [dir1] [dir2] ...";
        knownKeys.add(new KeyDescriptions("--help", "Prints this help."));
    }

    public RMDIR(String[] args) {
        super(args);
    }

    @Override
    public void runCommand() {
        if (showHelp || dirsToRemove.isEmpty()) {
            help();
            return;
        }

        for (String file : dirsToRemove) {
            File f = new File(file);

            if (!f.isDirectory()) {
                output.add("Not a directory!");
                return;
            }

            if (!f.delete()) {
                output.add("File " + f.getAbsolutePath() + " wasn't deleted!");
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
            } else if (param.startsWith("--")) {
                dirsToRemove.add(param.substring(1));
            } else {
                dirsToRemove.add(param);
            }
        }
    }

    public static void main(String[] args) {
        ConsoleCommand command = new RMDIR(args);
        command.start();
    }
}
