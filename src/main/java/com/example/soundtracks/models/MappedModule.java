package com.example.soundtracks.models;

import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.generated.types.Selection;
import com.example.soundtracks.generated.types.Slot;

import java.util.ArrayList;
import java.util.List;

public class MappedModule extends Module {

    private List<String> consistsOfId;

    public MappedModule(String id, String label, List<Module> isPartOfModule, List<Module> consistsOfModule, List<Slot> consistsOfSlot, List<Selection> isSelectedBy) {
        super(id, label, isPartOfModule, consistsOfModule, consistsOfSlot, isSelectedBy);
        consistsOfId = new ArrayList<>();
    }

    public List<String> getConsistsOfId() {
        return consistsOfId;
    }

    public void setConsistsOfId(List<String> consistsOfId) {
        this.consistsOfId = consistsOfId;
    }

}
