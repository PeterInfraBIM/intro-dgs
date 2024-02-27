package com.example.soundtracks.repositories;

import com.example.soundtracks.generated.types.Link;
import com.example.soundtracks.generated.types.Node;
import com.example.soundtracks.models.MappedLink;
import com.example.soundtracks.models.MappedNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LinkRepository extends AbstractRepository {
    private final Map<String, MappedLink> linkMap;

    private final Map<String, MappedNode> nodeMap;

    public LinkRepository() {
        this.linkMap = new HashMap<>();
        this.nodeMap = new HashMap<>();
    }

    public List<MappedLink> getLinks() {
        if (linkMap.isEmpty()) {
            try {
                JSONTokener jsonTokener = getJsonTokener("Links.json");
                JSONArray jsonArray = new JSONArray(jsonTokener);
                for (int item = 0; item < jsonArray.length(); item++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(item);
                    String id = jsonObject.get("id").toString();
                    MappedLink link = new MappedLink(id, null,null, null, null, null, null, null);
                    link.setInId(jsonObject.get("in").toString());
                    link.setOutId(jsonObject.get("out").toString());
                    nodeMap.put(link.getInId(), new MappedNode(link.getInId(), null, null));
                    nodeMap.put(link.getOutId(), new MappedNode(link.getOutId(), null, null));
                    linkMap.put(id, link);
                }
                closeReader();
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return new ArrayList<>(linkMap.values());
    }

    private Map<String, MappedLink> getLinkMap() {
        if (linkMap.isEmpty()) {
            getLinks();
        }
        return linkMap;
    }

    private Map<String, MappedNode> getNodeMap() {
        if (nodeMap.isEmpty()) {
            try {
                getNodes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return nodeMap;
    }

    public List<Node> getNodes() throws IOException {
        if (nodeMap.isEmpty()) {
            getLinks();
        }
        return new ArrayList<>(nodeMap.values());
    }

    public Node getNode(String nodeId) {
        return getNodeMap().get(nodeId);
    }

    public Link getLink(String linkId) {
        return getLinkMap().get(linkId);
    }

    public Link getLink(Node node) {
        return getLinkMap().values().stream()
                .filter(link -> link.getInId().equals(node.getId()) || link.getOutId().equals(node.getId()))
                .findFirst()
                .get();
    }

    public Node getIn(MappedLink link) {
        return getNode(link.getInId());
    }

    public Node getOut(MappedLink link) {
        return getNode(link.getOutId());
    }

}
