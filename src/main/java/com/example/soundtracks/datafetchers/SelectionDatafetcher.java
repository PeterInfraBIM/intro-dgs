package com.example.soundtracks.datafetchers;

import com.example.soundtracks.generated.types.Link;
import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.generated.types.Selection;
import com.example.soundtracks.generated.types.Slot;
import com.example.soundtracks.models.MappedSelection;
import com.example.soundtracks.models.MappedSlot;
import com.example.soundtracks.repositories.ModuleRepository;
import com.example.soundtracks.repositories.SelectionRepository;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;
import com.netflix.graphql.dgs.DgsQuery;

import java.io.IOException;
import java.util.List;

@DgsComponent
public class SelectionDatafetcher {
    private final SelectionRepository selectionRepository;
    private final ModuleRepository moduleRepository;

    public SelectionDatafetcher(SelectionRepository selectionRepository, ModuleRepository moduleRepository) {
        this.selectionRepository = selectionRepository;
        this.moduleRepository = moduleRepository;
    }

    @DgsQuery
    public List<Selection> selections() throws IOException {
        return selectionRepository.getSelections();
    }

    @DgsData(parentType = "Selection", field = "isSelectedBy")
    public Slot isSelectedBySelection(DgsDataFetchingEnvironment dfe) {
        Selection selection = dfe.getSource();
        return selectionRepository.isSelectedBy(selection.getId());
    }

    @DgsData(parentType = "Selection")
    public Module selects(DgsDataFetchingEnvironment dfe) {
        MappedSelection selection = dfe.getSource();
        return selectionRepository.selects(selection);
    }

    @DgsData(parentType = "Selection")
    public List<Module> option(DgsDataFetchingEnvironment dfe) {
        MappedSelection selection = dfe.getSource();
        return moduleRepository.option(selection);
    }

    @DgsData(parentType = "Selection")
    public List<Module> accepts(DgsDataFetchingEnvironment dfe) {
        MappedSelection selection = dfe.getSource();
        return moduleRepository.accepts(selection);
    }

    @DgsData(parentType = "Link")
    public Module inModule(DgsDataFetchingEnvironment dfe) {
        Link link = dfe.getSource();
        return selectionRepository.getInModule(link);
    }

    @DgsData(parentType = "Link")
    public Module outModule(DgsDataFetchingEnvironment dfe) {
        Link link = dfe.getSource();
        return selectionRepository.getOutModule(link);
    }

    @DgsData(parentType = "Module", field = "isSelectedBy")
    public List<Selection> isSelectedByModule (DgsDataFetchingEnvironment dfe) {
        Module module = dfe.getSource();
        return selectionRepository.getIsSelectedBy(module.getId());
    }

    @DgsData(parentType = "Slot")
    public Module selectedModule (DgsDataFetchingEnvironment dfe) {
        MappedSlot slot = dfe.getSource();
        return selectionRepository.getSelectedModule(slot);
    }
}
