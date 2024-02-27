package com.example.soundtracks.repositories;

import com.example.soundtracks.generated.types.*;
import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.models.MappedLink;
import com.example.soundtracks.models.MappedModule;
import com.example.soundtracks.models.MappedSelection;
import com.example.soundtracks.models.MappedSlot;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SelectionRepository extends AbstractRepository {
    private Map<String, MappedSelection> selectionMap;
    private final SlotRepository slotRepository;
    private final ModuleRepository moduleRepository;

    public SelectionRepository(SlotRepository slotRepository, ModuleRepository moduleRepository) {
        this.slotRepository = slotRepository;
        this.moduleRepository = moduleRepository;
        this.selectionMap = getSelectionMap();
    }

    private Map<String, MappedSelection> getSelectionMap() {
        if (selectionMap == null) {
            selectionMap = new HashMap<>();
        }
        if (selectionMap.isEmpty()) {
            getSelections();
        }
        return selectionMap;
    }

    public List<Selection> getSelections() {
        if (selectionMap.isEmpty()) {
            try {
                JSONTokener jsonTokener = getJsonTokener("Selections.json");
                JSONArray jsonArray = new JSONArray(jsonTokener);
                for (int item = 0; item < jsonArray.length(); item++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(item);
                    String id = jsonObject.get("id").toString();
                    MappedSelection selection = new MappedSelection(id, null, null, new ArrayList<Module>(), new ArrayList<Module>(), new ArrayList<Module>());
                    Object selects = jsonObject.get("selects");
                    if (selects != null) {
                        selection.setSelectsId(selects.toString());
                    }
                    Object isSelectedBy = jsonObject.get("isSelectedBy");
                    if (isSelectedBy != null) {
                        selection.setIsSelectedById(isSelectedBy.toString());
                    }
                    JSONArray optionIds = (JSONArray) jsonObject.get("option");
                    if (!optionIds.isEmpty()) {
                        List<String> optionIdList = new ArrayList<String>();
                        for (Object idItem : optionIds.toList()) {
                            optionIdList.add(idItem.toString());
                        }
                        selection.setOptionId(optionIdList);
                    }
                    JSONArray acceptsIds = (JSONArray) jsonObject.get("accepts");
                    if (!acceptsIds.isEmpty()) {
                        List<String> acceptsIdList = new ArrayList<String>();
                        for (Object idItem : acceptsIds.toList()) {
                            acceptsIdList.add(idItem.toString());
                        }
                        selection.setAcceptsId(acceptsIdList);
                    }
                    JSONArray rejectsIds = (JSONArray) jsonObject.get("rejects");
                    if (!rejectsIds.isEmpty()) {
                        List<String> rejectsIdList = new ArrayList<String>();
                        for (Object idItem : optionIds.toList()) {
                            rejectsIdList.add(idItem.toString());
                        }
                        selection.setRejectsId(rejectsIdList);
                    }

                    selectionMap.put(id, selection);
                }
                closeReader();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>(selectionMap.values());
    }

    public Selection getSelection(String id) {
        return getSelectionMap().get(id);
    }

    public Slot isSelectedBy(String selectionId) {
        MappedSelection selection = (MappedSelection) getSelection(selectionId);
        return slotRepository.getSlot(selection.getIsSelectedById());
    }

    public Module selects(MappedSelection selection) {
        return moduleRepository.getModule(selection.getSelectsId());
    }

    public MappedSelection getSelects(String slotId) {
        Optional<MappedSelection> optionalMappedSelection = getSelectionMap().values()
                .stream()
                .filter(s -> s.getIsSelectedById() != null)
                .filter(s -> s.getIsSelectedById().equals(slotId))
                .peek(s -> s.setIsSelectedBy(slotRepository.getSlot(s.getIsSelectedById())))
                .findFirst();

        return optionalMappedSelection.orElse(null);
    }

    public List<Selection> getIsSelectedBy(String moduleId) {
        return getSelectionMap().values()
                .stream()
                .filter(s -> s.getSelectsId() != null)
                .filter(s -> s.getSelectsId().equals(moduleId))
                .peek(s -> s.setSelects(moduleRepository.getModule(s.getSelectsId())))
                .collect(Collectors.toList());
    }

    public Module getInModule(Link link) {
        Optional<MappedModule> module = getSelectionMap().values().stream()
                .filter(selection -> ((MappedSlot) slotRepository.getSlot(selection.getIsSelectedById())).getPortsId().contains(((MappedLink) link).getInId()))
                .map(selection -> moduleRepository.getModule(selection.getSelectsId())).findFirst();
        return module.orElse(null);
    }

    public Module getOutModule(Link link) {
        Optional<MappedModule> module = getSelectionMap().values().stream()
                .filter(selection -> ((MappedSlot) slotRepository.getSlot(selection.getIsSelectedById())).getPortsId().contains(((MappedLink) link).getOutId()))
                .map(selection -> moduleRepository.getModule(selection.getSelectsId())).findFirst();
        return module.orElse(null);
    }

    public Module getSelectedModule(MappedSlot slot) {
        MappedSelection selection = getSelects(slot.getId());
        Optional<Module> optionalModule = moduleRepository.getModules().stream()
                .filter(module -> module.getId().equals(selection.getSelectsId()))
                .findFirst();
        return optionalModule.orElse(null);
    }

    public Link getIsPartOfLink(MappedLink link, MappedSlot inSlot, MappedSlot outSlot) {
        MappedModule inModule = moduleRepository.getModule(inSlot.getIsPartOfId());
        MappedModule outModule = moduleRepository.getModule(outSlot.getIsPartOfId());
        if (inModule.getId() != outModule.getId()) {
            Optional<Slot> optionalInSlot = selectionMap.values().stream()
                    .filter(selection -> inModule.getId().equals(selection.getSelectsId()))
                    .map(selection -> slotRepository.getSlot(selection.getIsSelectedById()))
                    .findFirst();
            Optional<Slot> optionalOutSlot = selectionMap.values().stream()
                    .filter(selection -> outModule.getId().equals(selection.getSelectsId()))
                    .map(selection -> slotRepository.getSlot(selection.getIsSelectedById()))
                    .findFirst();
            if (optionalInSlot.isPresent() && optionalOutSlot.isPresent()) {
                return slotRepository.getLink((MappedSlot) optionalInSlot.get(), (MappedSlot) optionalOutSlot.get());
            }
        }
        return null;
    }

}
