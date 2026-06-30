package com.channel360.workflow.api.dto.simulator;

import java.util.List;

public record SimulateResultDTO(
    boolean terminal,
    String message,
    List<String> trace
) {}
