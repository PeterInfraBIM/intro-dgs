package com.example.soundtracks.datafetchers;

import com.example.soundtracks.generated.types.Link;
import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.generated.types.Selection;
import com.example.soundtracks.generated.types.Slot;
import com.example.soundtracks.models.MappedSelection;
import com.example.soundtracks.models.MappedSlot;
import com.example.soundtracks.repositories.ModuleRepository;
import com.example.soundtracks.repositories.SelectionRepository;
import com.example.soundtracks.repositories.SlotRepository;
import com.netflix.graphql.dgs.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@DgsComponent
public class ModuleDatafetcher {
    private final SlotRepository slotRepository;
    private final ModuleRepository moduleRepository;
    private final SelectionRepository selectionRepository;

    public ModuleDatafetcher(SlotRepository slotRepository,
                             ModuleRepository moduleRepository,
                             SelectionRepository selectionRepository) {
        this.slotRepository = slotRepository;
        this.moduleRepository = moduleRepository;
        this.selectionRepository = selectionRepository;
    }

    @DgsQuery
    public List<Module> modules() throws IOException {
        return moduleRepository.getModules();
    }

    @DgsQuery
    public Module module(@InputArgument String id) throws IOException {
        return moduleRepository.getModule(id);
    }

    @DgsData(parentType = "Module")
    public List<Slot> consistsOfSlot(DgsDataFetchingEnvironment dfe) {
        Module module = dfe.getSource();
        return slotRepository.getConsistsOfSlot(module.getId());
    }

    @DgsData(parentType = "Module")
    public List<Module> isPartOfModule(DgsDataFetchingEnvironment dfe) {
        Module module = dfe.getSource();
        List<Selection> isSelectedByList = selectionRepository.getIsSelectedBy(module.getId());
        return isSelectedByList.stream()
                .map(selection -> (MappedSelection)selection)
                .peek(selection -> selection.setIsSelectedBy(slotRepository.getSlot(selection.getIsSelectedById())))
                .map(Selection::getIsSelectedBy)
                .filter(Objects::nonNull)
                .map(slot -> (MappedSlot)slot)
                .peek(slot -> slot.setIsPartOfModule(moduleRepository.getModule(slot.getIsPartOfId())))
                .map(Slot::getIsPartOfModule)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @DgsData(parentType = "Module")
    public List<Module> consistsOfModule(DgsDataFetchingEnvironment dfe) {
        Module module = dfe.getSource();
        List<Slot> consistsOfSlot = slotRepository.getConsistsOfSlot(module.getId());
        return consistsOfSlot.stream()
                .map(slot -> (MappedSlot) slot)
                .map(slot -> selectionRepository.getSelects(slot.getId()))
                .filter(Objects::nonNull)
                .map(selection -> (MappedSelection)selection)
                .filter(selection -> selection.getSelectsId() != null)
                .map(selection -> moduleRepository.getModule(selection.getSelectsId()))
                .collect(Collectors.toList());
    }

}
