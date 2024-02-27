package com.example.soundtracks.datafetchers;

import com.example.soundtracks.generated.types.*;
import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.models.*;
import com.example.soundtracks.repositories.FunctionRepository;
import com.example.soundtracks.repositories.LinkRepository;
import com.example.soundtracks.repositories.SelectionRepository;
import com.example.soundtracks.repositories.SlotRepository;
import com.netflix.graphql.dgs.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@DgsComponent
public class SlotDatafetcher {

    SlotRepository slotRepository;
    private final SelectionRepository selectionRepository;
    private final LinkRepository linkRepository;
    private final FunctionRepository functionRepository;

    public SlotDatafetcher(
            SlotRepository slotRepository,
            SelectionRepository selectionRepository,
            LinkRepository linkRepository,
            FunctionRepository functionRepository) {
        this.slotRepository = slotRepository;
        this.selectionRepository = selectionRepository;
        this.linkRepository = linkRepository;
        this.functionRepository = functionRepository;
    }

    @DgsQuery
    public List<MappedSlot> slots() throws IOException {
        return slotRepository.getSlots();
    }

    @DgsQuery
    public Slot slot(@InputArgument String id) {
        return slotRepository.getSlot(id);
    }

    @DgsData(parentType = "Slot")
    public Module isPartOfModule(DgsDataFetchingEnvironment dfe) {
        Slot slot = dfe.getSource();
        return slotRepository.getIsPartOfModule(slot.getId());
    }

    @DgsData(parentType = "Slot")
    public Selection selects(DgsDataFetchingEnvironment dfe) {
        Slot slot = dfe.getSource();
        return selectionRepository.getSelects(slot.getId());
    }

    @DgsData(parentType = "Slot")
    public List<Node> ports(DgsDataFetchingEnvironment dfe) {
        MappedSlot slot = dfe.getSource();
        return slot.getPortsId().stream().map(linkRepository::getNode).collect(Collectors.toList());
    }

    @DgsData(parentType = "Slot")
    public List<Link> links(DgsDataFetchingEnvironment dfe) {
        MappedSlot slot = dfe.getSource();
        return linkRepository.getLinks().stream()
                .filter(link -> slot.getPortsId().contains(link.getInId()) || slot.getPortsId().contains(link.getOutId()))
                .collect(Collectors.toList());
    }

    @DgsData(parentType = "Node")
    public Slot slot(DgsDataFetchingEnvironment dfe) {
        MappedNode node = dfe.getSource();
        return slotRepository.getNodeSlot(node);
    }

    @DgsData(parentType = "Link")
    public Slot inSlot(DgsDataFetchingEnvironment dfe) {
        MappedLink link = dfe.getSource();
        return slotRepository.getInSlot(link);
    }

    @DgsData(parentType = "Link")
    public Slot outSlot(DgsDataFetchingEnvironment dfe) {
        MappedLink link = dfe.getSource();
        return slotRepository.getOutSlot(link);
    }

    @DgsData(parentType = "Slot")
    public Function function(DgsDataFetchingEnvironment dfe) {
        MappedSlot slot = dfe.getSource();
        return functionRepository.getFunction(slot);
    }

    @DgsData(parentType = "Link")
    public Link isPartOfLink(DgsDataFetchingEnvironment dfe) {
        MappedLink link = dfe.getSource();
        Node inNode = linkRepository.getNode(link.getInId());
        Node outNode = linkRepository.getNode(link.getOutId());
        MappedSlot inSlot = slotRepository.getSlots().stream()
                .filter(slot -> slot.getPortsId().contains(inNode.getId()))
                .findFirst()
                .orElse(null);
        MappedSlot outSlot = slotRepository.getSlots().stream()
                .filter(slot -> slot.getPortsId().contains(outNode.getId()))
                .findFirst()
                .orElse(null);
        return selectionRepository.getIsPartOfLink(link, inSlot, outSlot);
    }

    @DgsData(parentType = "Slot")
    public List<Slot> consistsOfSlot(DgsDataFetchingEnvironment dfe) {
        MappedSlot slot = dfe.getSource();
        Optional<Selection> optionalSelection = selectionRepository.getSelections().stream()
                .filter(selection -> ((MappedSelection) selection).getIsSelectedById().equals(slot.getId()))
                .findFirst();
        if (optionalSelection.isPresent()) {
            Module module = selectionRepository.selects((MappedSelection) optionalSelection.get());
            return slotRepository.getConsistsOfSlot(module.getId());
        }
        return new ArrayList<>();
    }
}
