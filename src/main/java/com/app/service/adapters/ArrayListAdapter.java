package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class ArrayListAdapter implements DataStructureAdapter {

    private final com.app.linear.implementations.ArrayList<String> list =
            new com.app.linear.implementations.ArrayList<>();

    @Override
    public List<Step> operate(OperationRequest req) {
        List<Step> steps = new ArrayList<>();
        String op = req.getOperation();
        Integer index = req.getIndex();
        String value = req.getValue();

        steps.add(new Step("snapshot", snapshot(), null, "Before"));

        switch (op) {
            case "add": {
                if (value == null) throw new IllegalArgumentException("value required");
                if (index == null) index = list.size();
                if (index < 0) index = 0;
                if (index > list.size()) index = list.size();

                if (index < list.size()) {
                    steps.add(new Step("shiftRight", snapshot(), java.util.List.of(index),
                            "Shifting right from index " + index));
                }
                list.add(index, value);
                steps.add(new Step("insert", snapshot(), java.util.List.of(index),
                        "Inserted " + value + " at index " + index));
                break;
            }

            case "set": {
                if (value == null) throw new IllegalArgumentException("value required");
                if (index == null) throw new IllegalArgumentException("index required");
                if (index < 0 || index >= list.size())
                    throw new IllegalArgumentException("index out of bounds for set: " + index);

                String prev = list.set(index, value);
                steps.add(new Step("set", snapshot(), java.util.List.of(index),
                        "Replaced " + prev + " with " + value + " at index " + index));
                break;
            }

            case "remove": {
                if (index == null) throw new IllegalArgumentException("index required");
                if (index < 0 || index >= list.size())
                    throw new IllegalArgumentException("index out of bounds for remove: " + index);

                steps.add(new Step("remove-pre", snapshot(), java.util.List.of(index),
                        "Removing index " + index));
                String removed = list.remove(index);

                if (index < list.size()) {
                    steps.add(new Step("shiftLeft", snapshot(), java.util.List.of(index),
                            "Shifting left from index " + index));
                }
                steps.add(new Step("remove", snapshot(), null, "Removed " + removed));
                break;
            }

            case "size": {
                steps.add(new Step("size", snapshot(), null, "size = " + list.size()));
                break;
            }

            case "isEmpty": {
                steps.add(new Step("isEmpty", snapshot(), null, "isEmpty = " + list.isEmpty()));
                break;
            }

            default:
                throw new IllegalArgumentException("Unsupported op for ArrayList: " + op);
        }
        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) out.add(list.get(i));
        return out;
    }
}
