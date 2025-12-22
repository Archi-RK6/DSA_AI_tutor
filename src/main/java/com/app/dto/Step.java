package com.app.dto;

import java.util.ArrayList;
import java.util.List;

public class Step {
    private String action;             // "snapshot", "insert", "remove", "shiftRight", ...
    private List<String> snapshot;     // a linear view of the structure after this step
    private List<Integer> highlights;  // indices to highlight
    private String message;            // narration text

    public Step() {
        this.snapshot = new ArrayList<>();
        this.highlights = new ArrayList<>();
    }

    public Step(String action, List<String> snapshot, List<Integer> highlights, String message) {
        this.action = action;
        this.snapshot = snapshot != null ? snapshot : new ArrayList<>();
        this.highlights = highlights != null ? highlights : new ArrayList<>();
        this.message = message;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public List<String> getSnapshot() { return snapshot; }
    public void setSnapshot(List<String> snapshot) { this.snapshot = snapshot; }

    public List<Integer> getHighlights() { return highlights; }
    public void setHighlights(List<Integer> highlights) { this.highlights = highlights; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
