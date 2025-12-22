package com.app.service;

import com.app.dto.OperationRequest;
import com.app.dto.Step;

import java.util.List;

public interface DataStructureAdapter {
    List<Step> operate(OperationRequest req);
    List<String> snapshot();
    default void reset() {}
}
