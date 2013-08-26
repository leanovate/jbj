package de.leanovate.jbj.api;

/**
 * Execute external processes.
 *
 * All external processes shall be spawned through this interface.
 */
public interface JbjProcessExecutor {
    /**
     * Execute a shell command.
     */
    public String execShell(String shellCommand);
}
