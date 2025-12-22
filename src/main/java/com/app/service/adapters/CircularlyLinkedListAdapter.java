package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.linear.implementations.CircularlyLinkedList;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class CircularlyLinkedListAdapter implements DataStructureAdapter {
    private final CircularlyLinkedList<String> cll = new CircularlyLinkedList<>();

    @Override
    public List<Step> operate(OperationRequest req) {
        List<Step> steps = new ArrayList<>();
        String op = req.getOperation();
        String value = req.getValue();

        steps.add(new Step("snapshot", snapshot(), null, "Before"));

        switch (op) {
            case "addFirst": {
                if (value == null) throw new IllegalArgumentException("value required");
                cll.addFirst(value);
                steps.add(new Step("addFirst", snapshot(), List.of(0), "Added at head: " + value));
                break;
            }
            case "addLast": {
                if (value == null) throw new IllegalArgumentException("value required");
                cll.addLast(value);
                steps.add(new Step("addLast", snapshot(), List.of(Math.max(cll.size() - 1, 0)),
                        "Added at tail: " + value));
                break;
            }
            case "removeFirst": {
                String removed = cll.removeFirst();
                steps.add(new Step("removeFirst", snapshot(), List.of(0), "Removed head: " + removed));
                break;
            }
            case "rotate": {
                cll.rotate();
                steps.add(new Step("rotate", snapshot(), null, "Rotated"));
                break;
            }
            case "first": {
                String x = cll.first();
                steps.add(new Step("first", snapshot(), null, "first = " + x));
                break;
            }
            case "last": {
                String y = cll.last();
                steps.add(new Step("last", snapshot(), null, "last = " + y));
                break;
            }
            case "size": {
                steps.add(new Step("size", snapshot(), null, "size = " + cll.size()));
                break;
            }
            case "isEmpty": {
                steps.add(new Step("isEmpty", snapshot(), null, "isEmpty = " + cll.isEmpty()));
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported op for CircularlyLinkedList: " + op);
        }

        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        int n = cll.size();
        for (int i = 0; i < n; i++) {
            String v = cll.first();
            out.add(v);
            cll.rotate();
        }
        return out;
    }
}
