package com.app.dto;

public class OperationRequest {
    private String operation;   // e.g., add, set, remove, addFirst, addLast, removeFirst, ...
    private Integer index;      // for index-based structures
    private String value;       // carry values as strings for easy display

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
