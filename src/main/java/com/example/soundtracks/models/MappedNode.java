package com.example.soundtracks.models;

import com.example.soundtracks.generated.types.Link;
import com.example.soundtracks.generated.types.Node;
import com.example.soundtracks.generated.types.Slot;

public class MappedNode extends Node {
    public MappedNode(String id, Slot slot, Link link) {
        super(id, slot, link);
    }
}
