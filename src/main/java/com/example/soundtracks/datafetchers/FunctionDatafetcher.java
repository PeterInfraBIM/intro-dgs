package com.example.soundtracks.datafetchers;

import com.example.soundtracks.generated.types.Slot;
import com.example.soundtracks.models.MappedFunction;
import com.example.soundtracks.repositories.FunctionRepository;
import com.example.soundtracks.repositories.SlotRepository;
import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsData;
import com.netflix.graphql.dgs.DgsDataFetchingEnvironment;

@DgsComponent
public class FunctionDatafetcher {
    private final FunctionRepository functionRepository;
    private final SlotRepository slotRepository;

    public FunctionDatafetcher(FunctionRepository functionRepository, SlotRepository slotRepository) {
        this.functionRepository = functionRepository;
        this.slotRepository = slotRepository;
    }

    @DgsData(parentType = "Function")
    public Slot isRepresentedBy(DgsDataFetchingEnvironment dfe) {
        MappedFunction function = dfe.getSource();
        return slotRepository.getSlot(function);
    }
}
