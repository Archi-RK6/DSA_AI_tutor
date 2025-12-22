package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.service.DataStructureAdapter;
import com.app.linear.implementations.DoublyLinkedList;

import java.util.ArrayList;
import java.util.List;

public class DoublyLinkedListAdapter implements DataStructureAdapter {
    private final DoublyLinkedList<String> dll = new DoublyLinkedList<>();

    @Override
    public List<Step> operate(OperationRequest req) {
        List<Step> steps = new ArrayList<>();
        String op = req.getOperation();
        String value = req.getValue();

        steps.add(new Step("snapshot", snapshot(), null, "Before"));

        switch (op) {
            case "addFirst": {
                if (value == null) throw new IllegalArgumentException("value required");
                dll.addFirst(value);
                steps.add(new Step("addFirst", snapshot(), List.of(0), "Added at head: " + value));
                break;
            }
            case "addLast": {
                if (value == null) throw new IllegalArgumentException("value required");
                dll.addLast(value);
                steps.add(new Step("addLast", snapshot(), List.of(Math.max(dll.size() - 1, 0)),
                        "Added at tail: " + value));
                break;
            }
            case "removeFirst": {
                String rf = dll.removeFirst();
                steps.add(new Step("removeFirst", snapshot(), List.of(0), "Removed head: " + rf));
                break;
            }
            case "removeLast": {
                String rl = dll.removeLast();
                int hl = Math.max(dll.size() - 1, 0);
                steps.add(new Step("removeLast", snapshot(), List.of(hl), "Removed tail: " + rl));
                break;
            }
            case "first": {
                String f = dll.first();
                steps.add(new Step("first", snapshot(), null, "first = " + f));
                break;
            }
            case "last": {
                String l = dll.last();
                steps.add(new Step("last", snapshot(), null, "last = " + l));
                break;
            }
            case "size": {
                steps.add(new Step("size", snapshot(), null, "size = " + dll.size()));
                break;
            }
            case "isEmpty": {
                steps.add(new Step("isEmpty", snapshot(), null, "isEmpty = " + dll.isEmpty()));
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported op for DoublyLinkedList: " + op);
        }
        return steps;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        for (String e : dll) out.add(e);
        return out;
    }
}
