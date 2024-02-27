package com.example.soundtracks.models;

import com.example.soundtracks.generated.types.Function;
import com.example.soundtracks.generated.types.RequiredProperty;
import com.example.soundtracks.generated.types.Slot;

public class MappedFunction extends Function {

    private String isRepresentedById;

    private String requiredPropertyId;

    public MappedFunction(String id, Slot isRepresentedBy, RequiredProperty requiredProperty) {
        super(id, isRepresentedBy, requiredProperty);
    }

    public String getIsRepresentedById() {
        return isRepresentedById;
    }

    public void setIsRepresentedById(String isRepresentedByID) {
        this.isRepresentedById = isRepresentedByID;
    }

    public String getRequiredPropertyId() {
        return requiredPropertyId;
    }

    public void setRequiredPropertyId(String requiredPropertyId) {
        this.requiredPropertyId = requiredPropertyId;
    }
}
