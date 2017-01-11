package it.polito.dp2.NFFG.sol3.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.neo4jxml.*;


public class NffgsDB {
	
	private Map<String, String> mapNodeIdNffgName = new HashMap<String, String>();
	private static NffgsDB nffgsDB = new NffgsDB();
	
	/* return the single nffgsDB */
	public static NffgsDB newNffgsDB(){
		return nffgsDB;
	}
	
	/* get from neo4j the list of the loaded NFFG and store it into the map*/
	private NffgsDB(){
		/* initialize mapNodeIdNodeName */
		initMapNodeIdNffgName();
		return;
	}
	
	private void initMapNodeIdNffgName(){
		/* download all the nodes loaded on Neo4J */
		Neo4JXMLClient client = new Neo4JXMLClient();
		Nodes nodes = null;
		try {
			nodes = client.getAllNodes();
		} catch (RuntimeException e) {
			return;
		}
		
		if(!nodes.getNode().isEmpty()){
			/* there are nodes loaded to neo4j */
			for (Nodes.Node node: nodes.getNode()) {
				/* check if a node is a NFFG */
				if(this.nodeIsNffg(node)){
					/* the node is an NFFG => add to the map */
					String nffgName = this.getNodeProperty(node.getProperty(), "name");
					//TODO at the moment don't check null nffgName 
					this.mapNodeIdNffgName.put(node.getId(), nffgName);
				}
			}
		}
	}
	
	/* extract the property value of the property named propertyName from a List<Property> */
	private String getNodeProperty(List<Property> propertyList, String propertyName){
		String propertyValue = null;
		for (Property property : propertyList) {
			if(property.getName().equals(propertyName)){
				propertyValue = property.getValue();
				break;
			}
		}
		return propertyValue;
	}
	
	/* check if a node is a NFFG */
	private boolean nodeIsNffg(Nodes.Node node){
		
		/* check if the node is a NFFG looking at the label */
		Neo4JXMLClient client = new Neo4JXMLClient();
		Labels labels = null;
		try {
			labels = client.getLabelByNodeId(node.getId());
		} catch (RuntimeException e) {
			return false;
		}
		
		if(!labels.getValue().contains("NFFG"))
			/* the node is not a NFFG */
			return false;
		else
			return true;
	}
	
	/* return the name of the nffg stored in the cache map */
	public List<String> getNffgNames(){
		List<String> nffgNames = new ArrayList<String>();
		for (Map.Entry<String, String> mapEntry: this.mapNodeIdNffgName.entrySet()) {
			nffgNames.add(mapEntry.getValue());
		}
		return nffgNames;
	}
	
	/* create a NFFG on neo4j */
	//TODO => must check the validity of the nffg before use this methods
	public void createNffg(Nffg nffg){
		
		Neo4JXMLClient client = new Neo4JXMLClient();
		/* used to keep trace of the loaded elements */
		Map<String, String> loadedNodeIds = new HashMap<String, String>();
		List<String> loadedRelationshipIds = new ArrayList<String>();
			
		try {
			
			/* load all the nodes */
			for (NodeType node : nffg.getNodes().getNode()) {
				/* create the node to load */
				Map<String, String> nodeProperty = new HashMap<String, String>();
				nodeProperty.put("name" , node.getName());
				nodeProperty.put("networkFunctionality", node.getNetworkFunctionality().toString());
				/* load the node */
				Node nodeLoaded = client.createNode(nodeProperty);
				/* update the list of the loaded nodes */
				loadedNodeIds.put(node.getName(), nodeLoaded.getId());
			}
			
			/* load all the relationships */
			for (LinkType link : nffg.getLinks().getLink()) {
				/* set the parameters of the relationship to load */
				String srcNodeId = loadedNodeIds.get(link.getSourceNode());
				String dstNodeId = loadedNodeIds.get(link.getDestinationNode());
				/* load the relationship */
				Relationship relationshipLoaded = client.createRelationship(srcNodeId, dstNodeId, "link");
				/* update the list of the loaded relationships */
				loadedRelationshipIds.add(relationshipLoaded.getId());
			}
			
			/* load the nffg node */
			/* set the properties of the nffg node to load */
			Map<String, String> nffgProperty = new HashMap<String, String>();
			nffgProperty.put("name" , nffg.getName());
			nffgProperty.put("lastUpdate", nffg.getLastUpdate().toString());
			/* load the nffg node */
			Node nffgLoaded = client.createNode(nffgProperty);
			/* create the label NFFG */
			client.createLabel(nffgLoaded.getId(), "NFFG");
			
			/* add nffg node to the map of the loaded nodes */
			loadedNodeIds.put(nffg.getName(), nffgLoaded.getId());
			
			/* load the nffg relationships */
			for (NodeType node : nffg.getNodes().getNode()) {
				/* create the relationships "belongs" */
				String srcNodeId = loadedNodeIds.get(nffg.getName());
				String dstNodeId = loadedNodeIds.get(node.getName());
				/* load the relationship */
				Relationship relationshipLoaded = client.createRelationship(srcNodeId, dstNodeId, "belongs");
				/* update the list of the loaded relationships */
				loadedRelationshipIds.add(relationshipLoaded.getId());
			}
			
			/* add nffg name to the map of the DB */
			this.mapNodeIdNffgName.put(nffg.getName(), nffgLoaded.getId());
			return;
			
		} catch (RuntimeException e) {
			
			/* remove all the loaded relationships */
			for (String relationshipId : loadedRelationshipIds)
				client.deleteRelationshipById(relationshipId);
			/* remove all the loaded nodes */
			for (Map.Entry<String, String> mapEntry: loadedNodeIds.entrySet())
				client.deleteNodeById(mapEntry.getValue());
			//TODO: throw some exception!!!
		} 
	}

	/* return a NFFG stored inside neo4j */
	public Nffg getNffg(String nffgName) {
		
	}
	
	
}
