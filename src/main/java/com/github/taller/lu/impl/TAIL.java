package com.github.taller.lu.impl;


import com.github.taller.lu.interfaces.Command;
import com.github.taller.lu.interfaces.ConsoleCommand;

import java.io.*;
import java.util.LinkedList;

public class TAIL extends AbstractCommand implements ConsoleCommand {

    private static final int FOLLOW_PERIOD = 200;

    private String file;

    private int tailSize;

    private boolean follow = false;

    private boolean wrongArgs = false;

    static {
        usage = "jtail [-f | -n] file\n" +
                "Output last 'n' lines from file.\n" +
                "Only one file is supported.\n";
        knownKeys.add("-f\t \tOutput appended data as the file grows.");
        knownKeys.add("-n\t \tOutput the last 'n' lines, instead of the last 10.");
        knownKeys.add("--help\t \tPrints this help.");
    }

    public TAIL(String[] args) {
        super(args);
    }

    @Override
    public void runCommand() {
        if (showHelp) {
            help();
            return;
        }

        if (wrongArgs) {
            output.add("Wrong parameters.");
            help();
            return;
        }

        if (follow) {
            runFollow();
        } else {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                LinkedList<String> list = new LinkedList<>();
                String line;
                int count = tailSize;
                while ((line = br.readLine()) != null) {
                    if (count > 0) {
                        list.addLast(line);
                        count--;
                    } else {
                        list.addLast(line);
                        list.removeFirst();
                    }
                }

                output(list);
            } catch (IOException e) {
                output.add("Command failed with exception " + e.getMessage() + " on file " + file + ".");
            }
        }

    }

    private void runFollow() {
        Thread th = new Thread(new Runnable() {
            private long lastKnownPosition = 0;

            private int runEveryNSeconds = FOLLOW_PERIOD;

            private boolean following = true;

            @Override
            public void run() {
                File followFile = new File(file);
                try {
                    while (following) {
                        Thread.sleep(runEveryNSeconds);
                        long fileLength = followFile.length();
                        if (fileLength > lastKnownPosition) {
                            RandomAccessFile readFileAccess = new RandomAccessFile(followFile, "r");
                            readFileAccess.seek(lastKnownPosition);
                            byte[] buf = new byte[1024];
                            int bufSize;
                            while ((bufSize = readFileAccess.read(buf)) > 0) {
                                byte[] l = new byte[bufSize];
                                System.arraycopy(buf, 0, l, 0, bufSize);
                                LOGGER.debug(new String(l));
                                lastKnownPosition += bufSize;
                            }
                            readFileAccess.close();
                        }
                    }
                } catch (Exception e) {
                    following = false;
                }
            }
        });
        th.start();
    }

    @Override
    public void parseParameters(String[] args) {
        if (args.length == 0) {
            showHelp = true;
            return;
        }

        if (args.length == 1) {
            if ("--help".equals(args[0])) {
                showHelp = true;
            } else {
                file = args[0];
                tailSize = 10;
            }
            return;
        }

        if (args.length == 2) {
            if ("-f".equals(args[0])) {
                follow = true;
                file = args[1];
            } else {
                try {
                    tailSize = Integer.valueOf(args[0].substring(1));
                    file = args[1];
                } catch (NumberFormatException nfex) {
                    wrongArgs = true;
                }
            }

            return;
        }

        wrongArgs = true;
    }

    public static void main(String[] args) {
        Command command = new TAIL(args);
        command.start();
    }
}
