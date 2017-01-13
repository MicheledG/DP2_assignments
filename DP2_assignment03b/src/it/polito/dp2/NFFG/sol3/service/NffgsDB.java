package it.polito.dp2.NFFG.sol3.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;

import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.neo4jxml.*;


public class NffgsDB {
	
	private Map<String, String> mapNffgNameNffgNodeId = new HashMap<String, String>();
	private Map<String, String> mapNodeIdNodeName = new HashMap<String, String>();
	private Map<String, List<String>> mapNffgNameNodeIds = new HashMap<String, List<String>>();
	private Map<String, List<String>> mapNodeIdRelationshipIds = new HashMap<String, List<String>>();
	
	private static NffgsDB nffgsDB = new NffgsDB();
	
	/* return the single nffgsDB */
	public static NffgsDB newNffgsDB(){
		return nffgsDB;
	}
	
	/* get from neo4j the list of the loaded NFFG and store it into the map*/
	private NffgsDB(){
		/* initialize all the maps */
		initMaps();
		initMapNodeIdNffgName();
		return;
	}
	
	private void initMaps() {
		
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
		
		/* init maps: mapNffgNameNffgNodeId and mapNodeIdNodeName */
		
		if(!nodes.getNode().isEmpty()){
			/* there are nodes loaded to neo4j */
			for (Nodes.Node node: nodes.getNode()) {
				/* check if a node is a NFFG */
				if(this.nodeIsNffg(node)){
					/* the node is an NFFG => add to the map */
					String nffgName = this.getNodeProperty(node.getProperty(), "name");
					//TODO at the moment don't check null nffgName 
					this.mapNffgNameNffgNodeId.put(nffgName, node.getId());
				} else {
					/* the node is a simple node */
					String nodeName = this.getNodeProperty(node.getProperty(), "name");
					this.mapNffgNameNffgNodeId.put(node.getId(), nodeName);
				}
			}
		} 
		else
			return;
		
		//TODO
			
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
		for (Map.Entry<String, String> mapEntry: this.mapNffgNameNffgNodeId.entrySet()) {
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
				Node nodeToCreate = this.translateNodeTypeToNode(node);
				/* load the node */
				Node nodeLoaded = client.createNode(nodeToCreate);
				/* update the list of the loaded nodes */
				loadedNodeIds.put(node.getName(), nodeLoaded.getId());
			}
			
			/* load all the relationships */
			for (LinkType link : nffg.getLinks().getLink()) {
				/* set the parameters of the relationship to create */
				String srcNodeId = loadedNodeIds.get(link.getSourceNode());
				String dstNodeId = loadedNodeIds.get(link.getDestinationNode());
				/* create the relationship */
				Relationship relationshipLoaded = client.createRelationship(srcNodeId, dstNodeId, "link");
				/* update the list of the loaded relationships */
				loadedRelationshipIds.add(relationshipLoaded.getId());
			}
			
			/* create the node of the nffg */
			Node nffgToCreate = this.translateNffgTypeToNode(nffg);
			/* load the nffg node */
			Node nffgLoaded = client.createNode(nffgToCreate);
			/* create the label NFFG */
			client.createLabel(nffgLoaded.getId(), "NFFG");
			/* add nffg node to the map of the loaded nodes */
			loadedNodeIds.put(nffg.getName(), nffgLoaded.getId());
			
			/* load the nffg relationships "belongs" */
			for (NodeType node : nffg.getNodes().getNode()) {
				/* set the parameters the relationships "belongs" */
				String srcNodeId = loadedNodeIds.get(nffg.getName());
				String dstNodeId = loadedNodeIds.get(node.getName());
				/* create the relationship */
				Relationship relationshipLoaded = client.createRelationship(srcNodeId, dstNodeId, "belongs");
				/* update the list of the loaded relationships */
				loadedRelationshipIds.add(relationshipLoaded.getId());
			}
			
			/* add nffg name to the map of the DB */
			this.mapNffgNameNffgNodeId.put(nffg.getName(), nffgLoaded.getId());
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
	
	/* return the node to create on neo4j to represent a node */
	private Node translateNodeTypeToNode(NodeType node){
		/* create the node properties */
		Property nameProperty = new Property();
		nameProperty.setName("name");
		nameProperty.setValue(node.getName());
		Property networkFunctionalityProperty = new Property();
		networkFunctionalityProperty.setName("networkFunctionality");
		networkFunctionalityProperty.setValue(node.getNetworkFunctionality().toString());
		/* create local Node element */
		Node nodeToCreate = new Node();
		/* add all the properties to the new node to create */
		nodeToCreate.getProperty().add(nameProperty);
		nodeToCreate.getProperty().add(networkFunctionalityProperty);
		return nodeToCreate;
	}
	
	/* return the node to create on neo4j to represent a Nffg */
	private Node translateNffgTypeToNode(Nffg nffg){
		/* create the nffg properties */
		Property nameProperty = new Property();
		nameProperty.setName("name");
		nameProperty.setValue(nffg.getName());
		Property lastUpdateProperty = new Property();
		lastUpdateProperty.setName("lastUpdate");
		lastUpdateProperty.setValue(nffg.getLastUpdate().toString());
		/* create local Node element */
		Node nffgToCreate = new Node();
		/* add all the properties to the new node to create */
		nffgToCreate.getProperty().add(nameProperty);
		nffgToCreate.getProperty().add(lastUpdateProperty);
		return nffgToCreate;
	}
	
	//TODO
	/* return a NFFG stored inside neo4j */
	public Nffg getNffg(String nffgName) throws UnknownNameException, ServiceException {
		try{
			/* find the id of the node that models the nffg */
			String nffgNodeId = this.findNffgNodeId(nffgName);
			/* get the nffg properties */
			
			/* get all the nodes of that nffg */
			List<NodeType> nffgNodes = this.getNffgNodes(nffgNodeId);
			/* get all the relationships of the nffg */
			List<LinkType> nffgLinks = this.getNffgLinks(nffgNodeId, nffgNodes);
			/* create the Nffg element containing all the infos */
			Nffg nffg = new Nffg();
			return nffg;
		} 
		finally {
			
		}
		
	}
	
	private String findNffgNodeId(String nffgName) throws UnknownNameException{
		if(this.mapNffgNameNffgNodeId.containsKey(nffgName))
			return this.mapNffgNameNffgNodeId.get(nffgName);
		else
			throw new UnknownNameException("Nffg named "+nffgName+"not found!");	
	}
	
	private List<NodeType> getNffgNodes(String nffgNodeId) {
		/* find all the relationships "belongs" of the nffg */
		
		/* extract all the node id of the nffg */
		/* get all the nodes of the nffg */
	}
}
