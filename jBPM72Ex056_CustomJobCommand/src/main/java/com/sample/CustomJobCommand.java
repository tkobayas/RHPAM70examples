package com.sample;

import org.kie.api.executor.Command;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomJobCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(CustomJobCommand.class);

    public ExecutionResults execute(CommandContext ctx) {

        logger.info("CustomJobCommand!");

        if (true) {
            throw new RuntimeException("XXX");
        }

        ExecutionResults executionResults = new ExecutionResults();
        return executionResults;
    }
}
