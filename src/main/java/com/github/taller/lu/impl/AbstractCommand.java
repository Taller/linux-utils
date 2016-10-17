package com.github.taller.lu.impl;


import com.github.taller.lu.interfaces.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractCommand implements Command {
    protected static final Logger LOGGER = LoggerFactory.getLogger("LOGGER");

    protected static String usage = "";

    protected static final Set<String> knownKeys = new LinkedHashSet<>();

    protected LinkedHashSet<String> output = new LinkedHashSet<>();

    protected Set<String> parsedKeys = new LinkedHashSet<>();

    protected String[] args;

    protected boolean showHelp = false;

    public AbstractCommand(String[] args) {
        this.args = args;
    }

    public void start() {
        parseParameters(args);
        runCommand();
        parseOutput();
    }

    protected abstract void parseParameters(String[] args);

    protected abstract void runCommand();

    protected void parseOutput() {
        output(output);
    }


    public void help() {
        LOGGER.debug("Usage:\n{}", usage);
        output(knownKeys);
    }

    protected void output(Collection<?> output) {
        for (Object kd : output) {
            LOGGER.debug("{}", kd);
        }

    }
}
