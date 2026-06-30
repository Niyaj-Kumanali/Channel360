package com.channel360.workflow.application.designer.model;

import java.util.List;

public record SyncResult(
    boolean success,
    int nodesCreated,
    int nodesUpdated,
    int nodesDeleted,
    int transitionsCreated,
    int transitionsUpdated,
    int transitionsDeleted,
    List<String> warnings
) {
    public static SyncResult empty() {
        return new SyncResult(true, 0, 0, 0, 0, 0, 0, List.of());
    }
}
