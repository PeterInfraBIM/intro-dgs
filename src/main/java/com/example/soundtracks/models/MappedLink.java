package com.example.soundtracks.models;

import com.example.soundtracks.generated.types.Link;
import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.generated.types.Node;
import com.example.soundtracks.generated.types.Slot;

public class MappedLink  extends Link {
    private String inId;

    private String outId;

    public MappedLink(String id, Link isPartOfLink, Node in, Node out, Slot inSlot, Slot outSlot, Module inModule,
                      Module outModule) {
        super(id, isPartOfLink, in, out, inSlot, outSlot, inModule, outModule);
    }

    public String getInId() {
        return inId;
    }

    public void setInId(String inId) {
        this.inId = inId;
    }

    public String getOutId() {
        return outId;
    }

    public void setOutId(String outId) {
        this.outId = outId;
    }
}
