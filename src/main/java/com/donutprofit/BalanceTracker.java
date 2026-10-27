package com.donutprofit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parses Donut SMP /bal chat responses and tracks profit relative to a baseline. */
public final class BalanceTracker {
    private static final Pattern BALANCE = Pattern.compile("(?i)\\byou have\\s+\\$?([0-9][0-9,]*(?:\\.[0-9]+)?)\\s*(?:dollars|coins|\\$)?");
    private long current;
    private long baseline;
    private boolean initialized;

    public boolean acceptChat(String message) {
        Matcher match = BALANCE.matcher(message);
        if (!match.find()) return false;
        long parsed;
        try { parsed = (long) Double.parseDouble(match.group(1).replace(",", "")); }
        catch (NumberFormatException ex) { return false; }
        current = parsed;
        if (!initialized) { baseline = parsed; initialized = true; }
        return true;
    }
    public void reset() { if (initialized) baseline = current; }
    public void setBaseline(long amount) { baseline = amount; }
    public long baseline() { return baseline; }
    public long profit() { return current - baseline; }
    public boolean initialized() { return initialized; }
    public long balance() { return current; }
}
