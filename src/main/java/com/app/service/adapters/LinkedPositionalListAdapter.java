package com.app.service.adapters;

import com.app.dto.OperationRequest;
import com.app.dto.Step;
import com.app.linear.implementations.LinkedPositionalList;
import com.app.linear.interfaces.Position;
import com.app.service.DataStructureAdapter;

import java.util.ArrayList;
import java.util.List;

public class LinkedPositionalListAdapter implements DataStructureAdapter {
    private final LinkedPositionalList<String> lpl = new LinkedPositionalList<>();

    @Override
    public List<Step> operate(OperationRequest req) {
        List<Step> steps = new ArrayList<>();
        String op = req.getOperation();
        Integer index = req.getIndex();
        String value = req.getValue();

        steps.add(new Step("snapshot", snapshot(), null, "Before"));

        switch (op) {
            case "addFirst": {
                if (value == null) throw new IllegalArgumentException("value required");
                lpl.addFirst(value);
                steps.add(new Step("addFirst", snapshot(), List.of(0), "Added at head: " + value));
                break;
            }
            case "addLast": {
                if (value == null) throw new IllegalArgumentException("value required");
                lpl.addLast(value);
                steps.add(new Step("addLast", snapshot(), List.of(Math.max(lpl.size() - 1, 0)),
                        "Added at tail: " + value));
                break;
            }
            case "addBefore": {
                if (value == null) throw new IllegalArgumentException("value required");
                if (index == null) throw new IllegalArgumentException("index required");

                if (lpl.size() == 0) {
                    lpl.addFirst(value);
                    steps.add(new Step("addBefore", snapshot(), List.of(0), "Added at head: " + value));
                } else {
                    int target = index;
                    if (target < 0) target = 0;
                    if (target > lpl.size()) target = lpl.size();

                    if (target == 0) {
                        lpl.addFirst(value);
                        steps.add(new Step("addBefore", snapshot(), List.of(0), "Added at head: " + value));
                    } else if (target == lpl.size()) {
                        lpl.addLast(value);
                        steps.add(new Step("addBefore", snapshot(), List.of(Math.max(lpl.size() - 1, 0)),
                                "Added at tail: " + value));
                    } else {
                        Position<String> pos = positionAtIndex(target);
                        lpl.addBefore(pos, value);
                        steps.add(new Step("addBefore", snapshot(), List.of(target),
                                "Added before index " + target + ": " + value));
                    }
                }
                break;
            }
            case "addAfter": {
                if (value == null) throw new IllegalArgumentException("value required");
                if (index == null) throw new IllegalArgumentException("index required");

                if (lpl.size() == 0) {
                    lpl.addFirst(value);
                    steps.add(new Step("addAfter", snapshot(), List.of(0),
                            "List was empty, added as head: " + value));
                } else {
                    int target = index;
                    if (target < 0) target = 0;
                    if (target >= lpl.size() - 1) {
                        lpl.addLast(value);
                        steps.add(new Step("addAfter", snapshot(), List.of(Math.max(lpl.size() - 1, 0)),
                                "Added after tail (append): " + value));
                    } else {
                        Position<String> pos = positionAtIndex(target);
                        lpl.addAfter(pos, value);
                        steps.add(new Step("addAfter", snapshot(), List.of(target + 1),
                                "Added after index " + target + ": " + value));
                    }
                }
                break;
            }
            case "set": {
                if (value == null) throw new IllegalArgumentException("value required");
                if (index == null) throw new IllegalArgumentException("index required");
                if (index < 0 || index >= lpl.size())
                    throw new IllegalArgumentException("index out of bounds for set: " + index);

                Position<String> pos = positionAtIndex(index);
                String prev = lpl.set(pos, value);
                steps.add(new Step("set", snapshot(), List.of(index),
                        "Replaced " + prev + " with " + value + " at index " + index));
                break;
            }
            case "remove": {
                if (index == null) throw new IllegalArgumentException("index required");
                if (index < 0 || index >= lpl.size())
                    throw new IllegalArgumentException("index out of bounds for remove: " + index);

                Position<String> pos = positionAtIndex(index);
                String removed = lpl.remove(pos);
                List<String> after = snapshot();
                List<Integer> highlight = after.isEmpty() ? List.of() : List.of(Math.min(index, after.size() - 1));
                steps.add(new Step("remove", after, highlight,
                        "Removed element at index " + index + ": " + removed));
                break;
            }
            case "first": {
                Position<String> p = lpl.first();
                String v = (p == null) ? null : p.getElement();
                steps.add(new Step("first", snapshot(), null, "first = " + v));
                break;
            }
            case "last": {
                Position<String> p = lpl.last();
                String v = (p == null) ? null : p.getElement();
                steps.add(new Step("last", snapshot(), null, "last = " + v));
                break;
            }
            case "size": {
                steps.add(new Step("size", snapshot(), null, "size = " + lpl.size()));
                break;
            }
            case "isEmpty": {
                steps.add(new Step("isEmpty", snapshot(), null, "isEmpty = " + lpl.isEmpty()));
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported operation for LinkedPositionalList: " + op);
        }

        return steps;
    }

    private Position<String> positionAtIndex(int index) {
        if (index < 0 || index >= lpl.size()) {
            throw new IndexOutOfBoundsException("index: " + index);
        }
        Position<String> pos = lpl.first();
        for (int i = 0; i < index; i++) {
            pos = lpl.after(pos);
        }
        return pos;
    }

    @Override
    public List<String> snapshot() {
        List<String> out = new ArrayList<>();
        for (String e : lpl) out.add(e);
        return out;
    }
}
