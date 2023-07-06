package com.kuaishou.libertydb;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import com.kuaishou.libertydb.common.SupplierLazy;
import com.kuaishou.libertydb.common.logger.Logger;

/**
 * @author zhaozhenhang <zhaozhenhang@kuaishou.com>
 * Created on 2023-07-06
 */
public class Launcher {

    public static final int port = 9999;
    public static final long DEFAULT_MEM = (1<<20)*64;
    public static final long KB = 1 << 10;
    public static final long MB = 1 << 20;
    public static final long GB = 1 << 30;

    private static final Logger logger = SupplierLazy.lazy(Logger::new).get();

    public static void main(String[] args) {
        try {
            Options options = new Options();
            options.addOption("create", true, "-create| Used to create a DB");
            options.addOption("open", true, "-open| Used to open a DB");
            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("create")) {
                logger.info("exec create ");
            }

            if (commandLine.hasOption("open")) {
                logger.info("exec open");
            }
            logger.info("Usage: launcher (open|create) DBPath");
        } catch (Throwable throwable) {
            logger.error("options invalid");
            System.exit(1);
        }
    }
}
