package com.github.taller.lu.impl;


import com.github.taller.lu.interfaces.Command;
import com.github.taller.lu.interfaces.ConsoleCommand;

import java.io.File;

public class PWD extends AbstractCommand implements ConsoleCommand {

    static {
        usage = "jpwd - Shows current directory.";
        knownKeys.add(new KeyDescriptions("--help", "Prints this help."));
    }

    public PWD(String[] args) {
        super(args);
    }

    @Override
    protected void parseParameters(String[] args) {
        if (args.length == 0) {
            return;
        }

        if (args.length == 1 && "--help".equals(args[0])) {
            showHelp = true;
        }
    }

    @Override
    public void runCommand() {
        if (showHelp) {
            help();
            return;
        }

        File f = new File(".");
        output.add(f.getAbsoluteFile().getParent());
    }

    public static void main(String[] args) {
        Command command = new PWD(args);
        command.start();
    }

}
