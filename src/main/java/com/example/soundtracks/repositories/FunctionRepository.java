package com.example.soundtracks.repositories;

import com.example.soundtracks.generated.types.Function;
import com.example.soundtracks.generated.types.Slot;
import com.example.soundtracks.models.MappedFunction;
import com.example.soundtracks.models.MappedSlot;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class FunctionRepository extends AbstractRepository {
    private Map<String, MappedFunction> functionMap;

    public FunctionRepository() {
        functionMap = getFunctionMap();
    }

    private Map<String, MappedFunction> getFunctionMap() {
        if (functionMap == null) {
            functionMap = new HashMap<>();
        }
        if (functionMap.isEmpty()) {
            getFunctions();
        }
        return functionMap;
    }

    public List<Function> getFunctions() {
        if (functionMap.isEmpty()) {
            try {
                JSONTokener jsonTokener = getJsonTokener("Functions.json");
                JSONArray jsonArray = new JSONArray(jsonTokener);
                for (int item = 0; item < jsonArray.length(); item++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(item);
                    String id = jsonObject.get("id").toString();
                    MappedFunction function = new MappedFunction(id, null, null);

                    Object isRepresentedBy = jsonObject.get("isRepresentedBy");
                    if (isRepresentedBy != null) {
                        function.setIsRepresentedById(isRepresentedBy.toString());
                    }
                    Object requiredProperty = jsonObject.get("requiredProperty");
                    if (requiredProperty != null) {
                        function.setRequiredPropertyId(requiredProperty.toString());
                    }

                    functionMap.put(id, function);
                }
                closeReader();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return new ArrayList<>(functionMap.values());
    }

    public Function getFunction(String id) {
        return getFunctionMap().get(id);
    }

    public MappedFunction getFunction(MappedSlot slot) {
        Optional<MappedFunction> optionalFunction = getFunctionMap().values().stream()
                .filter(function -> function.getIsRepresentedById().equals(slot.getId()))
                .findFirst();
        return optionalFunction.orElse(null);
    }

}
