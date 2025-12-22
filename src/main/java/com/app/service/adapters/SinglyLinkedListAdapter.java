package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.linear.implementations.SinglyLinkedList;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class SinglyLinkedListAdapter implements DataStructureAdapter {
    private final SinglyLinkedList<String> sll = new SinglyLinkedList<>();

    @Override
    public List<Step> operate(OperationRequest req) {
        List<Step> steps = new ArrayList<>();
        String op = req.getOperation();
        String value = req.getValue();

        steps.add(new Step("snapshot", snapshot(), null, "Before"));

        switch (op) {
            case "addFirst": {
                if (value == null) throw new IllegalArgumentException("value required");
                sll.addFirst(value);
                steps.add(new Step("addFirst", snapshot(), List.of(0), "Added at head: " + value));
                break;
            }
            case "addLast": {
                if (value == null) throw new IllegalArgumentException("value required");
                sll.addLast(value);
                steps.add(new Step("addLast", snapshot(), List.of(Math.max(sll.size() - 1, 0)),
                        "Added at tail: " + value));
                break;
            }
            case "removeFirst": {
                String removed = sll.removeFirst();
                steps.add(new Step("removeFirst", snapshot(), List.of(0), "Removed head: " + removed));
                break;
            }
            case "first": {
                String f = sll.first();
                steps.add(new Step("first", snapshot(), null, "first = " + f));
                break;
            }
            case "last": {
                String l = sll.last();
                steps.add(new Step("last", snapshot(), null, "last = " + l));
                break;
            }
            case "size": {
                steps.add(new Step("size", snapshot(), null, "size = " + sll.size()));
                break;
            }
            case "isEmpty": {
                steps.add(new Step("isEmpty", snapshot(), null, "isEmpty = " + sll.isEmpty()));
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported op for SinglyLinkedList: " + op);
        }
        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        for (String e : sll) out.add(e);
        return out;
    }
}
