package com.example.soundtracks.models;

import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.generated.types.Selection;
import com.example.soundtracks.generated.types.Slot;

import java.util.List;

public class MappedSelection extends Selection {
    private String isSelectedById;
    private String selectsId;

    private List<String> optionId;

    private List<String> acceptsId;

    private List<String> rejectsId;

    public MappedSelection(String id, Slot isSelectedBy, Module selects, List<Module> option,
                           List<Module> accepts, List<Module> rejects) {
        super(id, isSelectedBy, selects, option, accepts, rejects);
    }

    public String getIsSelectedById() {
        return isSelectedById;
    }

    public void setIsSelectedById(String isSelectedById) {
        this.isSelectedById = isSelectedById;
    }

    public String getSelectsId() {
        return selectsId;
    }

    public void setSelectsId(String selectsId) {
        this.selectsId = selectsId;
    }

    public List<String> getOptionId() {
        return optionId;
    }

    public void setOptionId(List<String> optionId) {
        this.optionId = optionId;
    }

    public List<String> getAcceptsId() {
        return acceptsId;
    }

    public void setAcceptsId(List<String> acceptsId) {
        this.acceptsId = acceptsId;
    }

    public List<String> getRejectsId() {
        return rejectsId;
    }

    public void setRejectsId(List<String> rejectsId) {
        this.rejectsId = rejectsId;
    }
}
