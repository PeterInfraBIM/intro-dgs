package com.example.soundtracks.repositories;

import com.example.soundtracks.generated.types.Link;
import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.generated.types.Selection;
import com.example.soundtracks.generated.types.Slot;
import com.example.soundtracks.models.MappedModule;
import com.example.soundtracks.models.MappedSelection;
import com.example.soundtracks.models.MappedSlot;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ModuleRepository extends AbstractRepository {

    private Map<String, MappedModule> moduleMap;

    public ModuleRepository() {
        moduleMap = getModuleMap();
    }

    private Map<String, MappedModule> getModuleMap() {
        if (moduleMap == null) {
            moduleMap = new HashMap<>();
        }
        if (moduleMap.isEmpty()) {
            getModules();
        }
        return moduleMap;
    }

    public List<Module> getModules() {
        try {
            if (moduleMap.isEmpty()) {
                JSONTokener jsonTokener = null;
                jsonTokener = getJsonTokener("Modules.json");
                JSONArray jsonArray = new JSONArray(jsonTokener);
                for (int item = 0; item < jsonArray.length(); item++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(item);
                    String id = jsonObject.get("id").toString();
                    String label = jsonObject.get("label").toString();
                    MappedModule module = new MappedModule(id, label, new ArrayList<Module>(), new ArrayList<Module>(), new ArrayList<Slot>(), new ArrayList<Selection>());
                    moduleMap.put(id, module);
                }
                closeReader();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<>(moduleMap.values());
    }

    public MappedModule getModule(String id) {
        return getModuleMap().get(id);
    }

    public List<Module> option(MappedSelection selection) {
        return getModuleMap().values().stream()
                .filter(module -> selection.getOptionId().contains(module.getId()))
                .collect(Collectors.toList());
    }

    public List<Module> accepts(MappedSelection selection) {
        return getModuleMap().values().stream()
                .filter(module -> selection.getAcceptsId().contains(module.getId()))
                .collect(Collectors.toList());
    }
}
