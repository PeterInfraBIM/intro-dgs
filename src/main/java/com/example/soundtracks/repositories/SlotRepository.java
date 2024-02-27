package com.example.soundtracks.repositories;

import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.generated.types.Slot;
import com.example.soundtracks.models.MappedFunction;
import com.example.soundtracks.models.MappedLink;
import com.example.soundtracks.models.MappedNode;
import com.example.soundtracks.models.MappedSlot;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SlotRepository extends AbstractRepository {
    private final ModuleRepository moduleRepository;
    private final LinkRepository linkRepository;
    private final Map<String, MappedSlot> slotMap;

    public SlotRepository(ModuleRepository moduleRepository, LinkRepository linkRepository) {
        this.moduleRepository = moduleRepository;
        this.linkRepository = linkRepository;
        slotMap = new HashMap<>();
    }

    private Map<String, MappedSlot> getSlotMap() {
        if (slotMap.isEmpty()) {
            getSlots();
        }
        return slotMap;
    }

    public List<MappedSlot> getSlots() {
        if (slotMap.isEmpty()) {
            try {
                JSONTokener jsonTokener = getJsonTokener("Slots.json");
                JSONArray jsonArray = new JSONArray(jsonTokener);
                for (int item = 0; item < jsonArray.length(); item++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(item);
                    String id = jsonObject.get("id").toString();
                    String label = jsonObject.get("label").toString();
                    MappedSlot slot = new MappedSlot(id, label, null, null,null, null, null, null, null);

                    Object isPartOf = jsonObject.get("isPartOf");
                    if (isPartOf != null) {
                        slot.setIsPartOfId(isPartOf.toString());
                    }
                    JSONArray portArray = (JSONArray) jsonObject.get("ports");
                    slot.setPortsId(portArray.toList().stream().map(Object::toString).collect(Collectors.toList()));

                    slotMap.put(id, slot);
                }
                closeReader();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return new ArrayList<>(slotMap.values());
    }

    public Slot getSlot(String id) {
        return getSlotMap().get(id);
    }

    public Module getIsPartOfModule(String slotId) {
        MappedSlot slot = (MappedSlot) getSlot(slotId);
        return moduleRepository.getModule(slot.getIsPartOfId());
    }

    public List<Slot> getConsistsOfSlot(String moduleId) {
        Module module = moduleRepository.getModule(moduleId);
        return getSlots()
                .stream()
                .peek(slot -> slot.setIsPartOfModule(getIsPartOfModule(slot.getId())))
                .filter(s -> s.getIsPartOfModule() != null)
                .filter(s -> s.getIsPartOfModule().getId().equals(moduleId))
                .collect(Collectors.toList());
    }

    public Slot getNodeSlot(MappedNode node) {
        Optional<MappedSlot> mappedSlot = getSlotMap().values().stream()
                .filter(slot -> slot.getPortsId().contains(node.getId()))
                .findFirst();
        return mappedSlot.orElse(null);
    }

    public Slot getInSlot(MappedLink link) {
        Optional<MappedSlot> inSlot = getSlotMap().values().stream()
                .filter(slot -> slot.getPortsId().contains(link.getInId()))
                .findFirst();
        return inSlot.orElse(null);
    }

    public Slot getOutSlot(MappedLink link) {
        Optional<MappedSlot> inSlot = getSlotMap().values().stream()
                .filter(slot -> slot.getPortsId().contains(link.getOutId()))
                .findFirst();
        return inSlot.orElse(null);
    }

    public Slot getSlot(MappedFunction function) {
        return getSlot(function.getIsRepresentedById());
    }

    public MappedLink getLink(MappedSlot inSlot, MappedSlot outSlot) {
        Optional<MappedLink> linkOptional = linkRepository.getLinks().stream()
                .filter(link ->
                        (inSlot.getPortsId().contains(link.getInId()) ||
                                inSlot.getPortsId().contains(link.getOutId())) &&
                                (outSlot.getPortsId().contains(link.getInId()) ||
                                        outSlot.getPortsId().contains(link.getOutId())))
                .findFirst();
        return linkOptional.orElse(null);
    }
}
