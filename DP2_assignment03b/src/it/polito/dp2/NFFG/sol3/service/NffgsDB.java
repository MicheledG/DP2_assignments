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
	
	private Map<String, String> mapNffgNameNffgId = new HashMap<String, String>();
	private Map<String, List<String>> mapNffgNameBelongsIds = new HashMap<String, List<String>>();
	private Map<String, String> mapNodeIdNodeName = new HashMap<String, String>();
	private Map<String, List<String>> mapNodeIdLinkIds = new HashMap<String, List<String>>();
	private Map<String, String> mapLinkIdLinkName = new HashMap<String, String>();
	
	private static NffgsDB nffgsDB = new NffgsDB();
	
	/* return the single nffgsDB */
	public static NffgsDB newNffgsDB(){
		return nffgsDB;
	}
	
	/* clear Neo4J */
	private NffgsDB(){
		/* clear all */
		this.clearAll();
		return;
	}
	
	/* clear local maps and neo4j */
	private void clearAll(){
		/* clear all the eventual nodes on neo4j */
		Neo4JXMLClient client = new Neo4JXMLClient();
		//TODO CAN THROW RuntimeException
		client.deleteAllNodes();
		
		/* clear all the maps */
		this.mapLinkIdLinkName.clear();
		this.mapNffgNameBelongsIds.clear();
		this.mapNffgNameNffgId.clear();
		this.mapNodeIdLinkIds.clear();
		this.mapNodeIdNodeName.clear();
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
	
	private String findNffgNodeId(String nffgName) throws UnknownNameException{
		if(this.mapNffgNameNffgId.containsKey(nffgName))
			return this.mapNffgNameNffgId.get(nffgName);
		else
			throw new UnknownNameException("Nffg named "+nffgName+"not found!");	
	}
	
	/* create a NFFG on neo4j */
	public void createNffg(Nffg nffg) throws ServiceException{
		
		Neo4JXMLClient client = new Neo4JXMLClient();
		/* used to keep trace of the loaded elements */
		Map<String, String> mapCreatedNodeNameNodeId = new HashMap<String, String>();
		Map<String, String> mapCreatedLinkNameLinkId = new HashMap<String, String>();
		List<String> createdBelongs = new ArrayList<String>();
		
		try {
			
			/* load all the nodes */
			for (NodeType node : nffg.getNodes().getNode()) {
				/* create the node to load */
				Node nodeToCreate = this.translateNodeTypeToNode(node);
				/* load the node */
				Node nodeCreated = client.createNode(nodeToCreate);
				/* update the map of the loaded nodes */
				mapCreatedNodeNameNodeId.put(node.getName(), nodeCreated.getId());
			}
			
			/* load all the links */
			for (LinkType link : nffg.getLinks().getLink()) {
				/* set the parameters of the relationship to create */
				String srcNodeId = mapCreatedNodeNameNodeId.get(link.getSourceNode());
				String dstNodeId = mapCreatedNodeNameNodeId.get(link.getDestinationNode());
				/* create the link */
				Relationship linkCreated = client.createRelationship(srcNodeId, dstNodeId, "link");
				/* update the map of the loaded links */
				mapCreatedLinkNameLinkId.put(link.getName(), linkCreated.getId());
			}
			
			/* create the node of the nffg */
			Node nffgToCreate = this.translateNffgTypeToNode(nffg);
			/* load the nffg node */
			Node nffgCreated = client.createNode(nffgToCreate);
			/* create the label NFFG */
			client.createLabel(nffgCreated.getId(), "NFFG");
			
			/* load all the belongs */
			for (NodeType node : nffg.getNodes().getNode()) {
				/* set the parameters the relationships "belongs" */
				String srcNodeId = mapCreatedNodeNameNodeId.get(nffg.getName());
				String dstNodeId = mapCreatedLinkNameLinkId.get(node.getName());
				/* create the belongs */
				Relationship belongsCreated = client.createRelationship(srcNodeId, dstNodeId, "belongs");
				/* update the list of the loaded belongs */
				createdBelongs.add(belongsCreated.getId());
			}
			
			/* add all the information to the NffgsDB maps */
			/* update NffgName <=> NffgId map */
			this.mapNffgNameNffgId.put(nffg.getName(), nffgCreated.getId());
			
			/* update NffgName <=> BelongsIds map */
			this.mapNffgNameBelongsIds.put(nffg.getName(), createdBelongs);
			
			/* update NodeId <=> NodeName map */
			for (Map.Entry<String, String> nodeNameNodeIdEntry: mapCreatedNodeNameNodeId.entrySet()) {
				this.mapNodeIdNodeName.put(nodeNameNodeIdEntry.getValue(), nodeNameNodeIdEntry.getKey());
			}
			
			/* update NodeId <=> LinkIds map */
			for (NodeType node : nffg.getNodes().getNode()) {
				List<String> outputLink = new ArrayList<String>();
				for (LinkType link : nffg.getLinks().getLink()) {
					if(link.getSourceNode().equals(node.getName()))
						outputLink.add(mapCreatedLinkNameLinkId.get(link.getName()));
				}
				this.mapNodeIdLinkIds.put(mapCreatedNodeNameNodeId.get(node.getName()), outputLink);
			}
			
			/* update LinkId <=> LinkName map */
			for (Map.Entry<String, String> linkNameLinkIdEntry: mapCreatedLinkNameLinkId.entrySet()) {
				this.mapLinkIdLinkName.put(linkNameLinkIdEntry.getValue(), linkNameLinkIdEntry.getKey());
			}
			
			return;
			
		} catch (RuntimeException e) {
			/* clear all */
			this.clearAll();
			throw new ServiceException("Error loading nffg named " + nffg.getName() + ", content reset");
			//TODO clear also the policies
		} 
	}
	
	//TODO
	/* return a NFFG stored inside neo4j */
	public Nffg getNffg(String nffgName) throws UnknownNameException, ServiceException {
		/* find nffgId of the node that models the nffg */
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
	
}
