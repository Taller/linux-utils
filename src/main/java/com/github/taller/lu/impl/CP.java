package com.github.taller.lu.impl;

import com.github.taller.lu.interfaces.Command;
import com.github.taller.lu.interfaces.ConsoleCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class CP extends AbstractCommand implements ConsoleCommand {

    private Set<String> srcFiles = new LinkedHashSet<>();

    private String target = null;

    static {
        usage = "jcp <source_file> <target_file>\n" +
                "jcp <source_files> <target_dir>\n" +
                "jcp -r <source_dir> <target_dir>\n" +
                "jcp -r --parents <path/to/source> <target_dir>\n" +
                "Copy files from source to target.\n" +
                "Wildcards are not supported.\n" +
                "Target files are not supported.";
        knownKeys.add("-r\t \tRecursively creates files and dirs.");
        knownKeys.add("--parents\t \tUse full source file name under <target>");
        knownKeys.add("--help\t \tPrints this help.");
    }

    private boolean isRecursive = false;

    private boolean needParent = false;

    public CP(String[] args) {
        super(args);
    }

    @Override
    public void runCommand() {
        if (showHelp || srcFiles.isEmpty()) {
            help();
            return;
        }

        String result;
        if ((result = targetExist()) != null) {
            output.add(result);
            return;
        }

        if ((result = allSourceExist()) != null) {
            output.add(result);
            return;
        }

        for (String srcFile : srcFiles) {
            File src = new File(srcFile);

            if (src.isDirectory() && isRecursive) {
                try {
                    Files.walkFileTree(src.toPath(), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                            try {
                                String targetDir;
                                if (needParent) {
                                    targetDir = target + src.getPath();
                                } else {
                                    targetDir = target + File.separator + src.getName();
                                }
                                File targetFileDir = new File(targetDir);
                                if (!targetFileDir.exists() && !targetFileDir.mkdirs()) {
                                    throw new IOException("Can't create target directory " + targetDir + ".");
                                }
                                String fname = file.getFileName().toString();
                                Path targetPath = new File(targetDir + File.separator + fname).toPath();
                                Files.copy(file, targetPath);
                            } catch (FileAlreadyExistsException faex) {
                                output.add("File " + faex.getMessage() + " already exists.");
                            } catch (Exception e) {
                                LOGGER.error("Can't copy file " + e.getMessage() + ".");
                            }

                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    output.add("Error during coping files " + e.getMessage());
                }
            } else if (src.isFile()) {
                try {
                    String fname = src.getName();
                    String targetDir = target;
                    if (needParent) {
                        targetDir = targetDir + src.getParent();
                    }
                    Path targetPath = new File(targetDir + File.separator + fname).toPath();
                    Files.copy(src.toPath(), targetPath);
                } catch (FileAlreadyExistsException faex) {
                    output.add("File " + faex.getMessage() + " already exists.");
                } catch (Exception e) {
                    LOGGER.error("Can't copy file " + e.getMessage() + ".");
                }
            }
        }
    }

    private String allSourceExist() {
        String result = null;
        for (String srcFile : srcFiles) {
            File src = new File(srcFile);
            if (src.exists() && src.canRead()) {
                continue;
            }

            result = "Can't read file " + srcFile + ".";
        }
        return result;
    }

    private String targetExist() {
        String result = null;
        File targetFile = new File(target);
        if (!targetFile.exists()) {
            result = "Target directory should exist.";
        }
        return result;
    }

    @Override
    public void parseParameters(String[] args) {
        if (args.length == 0) {
            showHelp = true;
            return;
        }

        target = args[args.length - 1];
        if (target.equals("--help")) {
            showHelp = true;
            return;
        }

        if (target.startsWith("--")) {
            target = target.substring(1);
        }

        LinkedList<String> argList = new LinkedList<>(Arrays.asList(args));
        argList.removeLast();

        for (String param : argList) {
            if (param.equals("--help")) {
                showHelp = true;
                break;
            } else if ("-r".equals(param)) {
                isRecursive = true;
            } else if ("--parents".equals(param)) {
                needParent = true;
            } else if (param.startsWith("--")) {
                srcFiles.add(param.substring(1));
            } else {
                srcFiles.add(param);
            }
        }
    }

    public static void main(String[] args) {
        Command command = new CP(args);
        command.start();
    }
}
