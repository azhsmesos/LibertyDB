package com.kuaishou.libertydb;

import static com.kuaishou.libertydb.common.Error.InvalidMemException;
import static com.kuaishou.libertydb.tm.TransactionManager.create;
import static com.kuaishou.libertydb.tm.TransactionManager.open;
import static org.apache.commons.lang3.StringUtils.isBlank;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import com.kuaishou.libertydb.common.ForceExit;
import com.kuaishou.libertydb.common.SupplierLazy;
import com.kuaishou.libertydb.common.logger.Logger;
import com.kuaishou.libertydb.tm.TransactionManager;

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
                logger.info("【exec create command start】... ");
                createDB(commandLine.getOptionValue("create"));
                return;
            }

            if (commandLine.hasOption("open")) {
                logger.info("exec open");
                openDB(commandLine.getOptionValue("open"), parseMem(commandLine.getOptionValue("mem")));
                return;
            }
            logger.info("Usage: launcher (open|create) DBPath");
        } catch (Throwable throwable) {
            logger.error("options invalid");
            System.exit(1);
        }
    }

    private static void openDB(String path, long mem) {
        TransactionManager tm = open(path);
    }

    private static void createDB(String path) {
        TransactionManager tm = create(path);
    }

    private static long parseMem(String memStr) {
        if (isBlank(memStr)) {
            return DEFAULT_MEM;
        }

        String unit = memStr.substring(memStr.length()-2);
        long memNum = Long.parseLong(memStr.substring(0, memStr.length()-2));
        switch(unit) {
            case "KB":
                return memNum*KB;
            case "MB":
                return memNum*MB;
            case "GB":
                return memNum*GB;
            default:
                ForceExit.exit(InvalidMemException);
        }
        return DEFAULT_MEM;
    }
}
