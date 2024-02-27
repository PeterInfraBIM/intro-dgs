package com.example.soundtracks.models;

import com.example.soundtracks.generated.types.*;
import com.example.soundtracks.generated.types.Module;

import java.util.List;

public class MappedSlot extends Slot {
    private String isPartOfId;

    private List<String> portsId;

    public MappedSlot(String id, String label, Module isPartOf, List<Slot> consistsOfSlot, Selection selects,
                      Module selectedModule, List<Node> ports, List<Link> links, Function function) {
        super(id, label, isPartOf, consistsOfSlot, selects, selectedModule, ports, links, function);
    }

    public String getIsPartOfId() {
        return isPartOfId;
    }

    public void setIsPartOfId(String isPartOfId) {
        this.isPartOfId = isPartOfId;
    }

    public List<String> getPortsId() {
        return portsId;
    }

    public void setPortsId(List<String> portsId) {
        this.portsId = portsId;
    }

}
