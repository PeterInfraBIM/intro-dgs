package com.example.soundtracks.datafetchers;

import com.example.soundtracks.generated.types.Link;
import com.example.soundtracks.generated.types.Module;
import com.example.soundtracks.generated.types.Node;
import com.example.soundtracks.generated.types.Slot;
import com.example.soundtracks.models.MappedLink;
import com.example.soundtracks.repositories.LinkRepository;
import com.netflix.graphql.dgs.*;

import java.io.IOException;
import java.util.List;

@DgsComponent
public class LinkDatafetcher {
    private final LinkRepository linkRepository;

    public LinkDatafetcher(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @DgsQuery
    public List<MappedLink> links() throws IOException {
        return linkRepository.getLinks();
    }

    @DgsQuery
    public Link link(@InputArgument String id) throws IOException {
        return linkRepository.getLink(id);
    }

    @DgsData(parentType = "Node")
    public Link link(DgsDataFetchingEnvironment dfe) {
        Node node = dfe.getSource();
        return linkRepository.getLink(node);
    }

    @DgsData(parentType = "Link")
    public Node inPort(DgsDataFetchingEnvironment dfe) {
        MappedLink link = dfe.getSource();
        return linkRepository.getIn(link);
    }

    @DgsData(parentType = "Link")
    public Node outPort(DgsDataFetchingEnvironment dfe) {
        MappedLink link = dfe.getSource();
        return linkRepository.getOut(link);
    }

}
