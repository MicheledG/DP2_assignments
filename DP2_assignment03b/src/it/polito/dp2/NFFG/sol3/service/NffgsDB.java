package it.polito.dp2.NFFG.sol3.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.*;
import it.polito.dp2.NFFG.sol3.service.neo4jxml.*;

public class NffgsDB {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
	
	private Map<String, String> mapNffgNameNffgId = new HashMap<String, String>();
	private Map<String, Set<String>> mapNffgNameBelongsIds = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> mapNffgNameNodeIds = new HashMap<String, Set<String>>();
	private Map<String, Set<String>> mapNffgNameLinkIds = new HashMap<String, Set<String>>();
	private Map<String, String> mapNodeIdNodeName = new HashMap<String, String>();
	private Map<String, String> mapLinkIdLinkName = new HashMap<String, String>();
	
	private static NffgsDB nffgsDB = new NffgsDB();
	
	/* return the single nffgsDB */
	public static NffgsDB newNffgsDB(){
		return nffgsDB;
	}
	
	/* MAJOR PUBLIC FUNCTIONALITIES */
	/* store a NFFG on neo4j */
	public void storeNffg(Nffgs.Nffg nffg) throws AlreadyLoadedException, ServiceException {
		
		String nffgName = nffg.getName();
		if(this.containsNffg(nffgName))
			throw new AlreadyLoadedException("already loaded nffg named " + nffg.getName());
		
		Neo4JXMLClient client = new Neo4JXMLClient();
		/* used to keep trace of the loaded elements */
		Map<String, String> mapCreatedNodeNameNodeId = new HashMap<String, String>();
		Map<String, String> mapCreatedLinkNameLinkId = new HashMap<String, String>();
		Set<String> createdBelongs = new HashSet<String>();
		
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
				String srcNodeId = nffgCreated.getId();
				String dstNodeId = mapCreatedNodeNameNodeId.get(node.getName());
				/* create the belongs */
				Relationship belongsCreated = client.createRelationship(srcNodeId, dstNodeId, "belongs");
				/* update the list of the loaded belongs */
				createdBelongs.add(belongsCreated.getId());
			}
			
			/* add all the information to the NffgsDB maps */
			/* update NffgName <=> NodeIds map + NodeId <=> NodeName map*/
			Set<String> nffgNodeIds = new HashSet<String>(); 
			for (Map.Entry<String, String> entryNodeNameNodeId: mapCreatedNodeNameNodeId.entrySet()) {
				String nodeName = entryNodeNameNodeId.getKey();
				String nodeId = entryNodeNameNodeId.getValue();
				nffgNodeIds.add(nodeId);
				this.mapNodeIdNodeName.put(nodeId, nodeName);
			}
			this.mapNffgNameNodeIds.put(nffgName, nffgNodeIds);
			
			/* update NffgName <=> LinkIds map + LinkId <=> LinkName map */
			Set<String> nffgLinkIds = new HashSet<String>(); 
			for (Map.Entry<String, String> entryLinkNameLinkId: mapCreatedLinkNameLinkId.entrySet()) {
				String linkName = entryLinkNameLinkId.getKey();
				String linkId = entryLinkNameLinkId.getValue();
				nffgLinkIds.add(linkId);
				this.mapLinkIdLinkName.put(linkId, linkName);
			}
			this.mapNffgNameLinkIds.put(nffgName, nffgLinkIds);
			
			/* update NffgName <=> BelongsIds map */
			this.mapNffgNameBelongsIds.put(nffg.getName(), createdBelongs);
			
			/* ADDING ENTRY in this last map at the end to ensure consistency at reading the other maps*/
			/* update NffgName <=> NffgId map */
			this.mapNffgNameNffgId.put(nffgName, nffgCreated.getId());
			
		} catch (RuntimeException e) {
			throw new ServiceException("Error loading nffg named " + nffg.getName());
		} 
	}
	
	/* return the list of all the nffg */
	public Set<String> getNffgNames() throws NoGraphException {
		if(this.isEmpty())
			throw new NoGraphException("no nffg in the DB");
		Set<String> nffgNames = this.extractNffgNames();
		return nffgNames;
	}
	
	/* return a NFFG from neo4j */
	public Nffgs.Nffg getNffg(String nffgName) throws UnknownNameException, ServiceException {

		if(!this.containsNffg(nffgName))
			throw new UnknownNameException("missing nffg named " + nffgName);
		
		/* find nffgId of the node that models the nffg */
		String nffgId = this.extractNffgId(nffgName);
		/* get the nffg properties */
		Neo4JXMLClient client = new Neo4JXMLClient();
		Node nffgNode = client.getNodeById(nffgId);
		String lastUpdateString = this.getNodeProperty(nffgNode.getProperty(), "lastUpdate");
		XMLGregorianCalendar lastUpdateCalendar = this.getNffgLastUpdateXMLGregorianCalendar(lastUpdateString);
		/* get all the nodes of that nffg */
		List<NodeType> nffgNodes = this.extractNffgNodes(nffgName);
		/* get all the relationships of the nffg */
		List<LinkType> nffgLinks = this.extractNffgLinks(nffgName);
		/* create the Nffg element containing all the infos */
		Nffgs.Nffg nffg = new Nffgs.Nffg();
		nffg.setName(nffgName);
		nffg.setLastUpdate(lastUpdateCalendar);
		Nffgs.Nffg.Nodes nodes = new Nffgs.Nffg.Nodes();
		for (NodeType node : nffgNodes) {
			nodes.getNode().add(node);
		}
		nffg.setNodes(nodes);
		Nffgs.Nffg.Links links = new Nffgs.Nffg.Links();
		for (LinkType link : nffgLinks) {
			links.getLink().add(link);
		}
		nffg.setLinks(links);
		return nffg;
	}
	
	/* clear neo4j and all local maps*/
	public void deleteNffgs() throws ServiceException{
		try{
			/* clear all the eventual nodes on neo4j */
			Neo4JXMLClient client = new Neo4JXMLClient();
			client.deleteAllNodes();
			/* clear all the maps */
			this.mapNffgNameNffgId.clear();
			this.mapNffgNameBelongsIds.clear();
			this.mapNffgNameNodeIds.clear();
			this.mapNffgNameLinkIds.clear();
			this.mapNodeIdNodeName.clear();
			this.mapLinkIdLinkName.clear();
			
		} 
		catch (RuntimeException e) {
			throw new ServiceException("Error deleting all nffgs");
		}
		return;
		
	}

	/* remove data of a single nffg from neo4j and the maps */
	public void deleteNffg(String nffgName) throws UnknownNameException, ServiceException{		
		
		/* find and check if the nffg is present */
		if(!this.containsNffg(nffgName))
			throw new UnknownNameException("missing nffg named "+nffgName);
		
		/* delete nffg corrispondence on the local map */
		String nffgId = this.extractNffgId(nffgName);
		this.mapNffgNameNffgId.remove(nffgName);
		
		Neo4JXMLClient client = new Neo4JXMLClient();
		/* delete all the links */
		Set<String> linkIds = this.extractNffgLinkIds(nffgName);
		for (String linkId: linkIds) {
			client.deleteRelationshipById(linkId);
			this.mapLinkIdLinkName.remove(linkId);
		}
		this.mapNffgNameLinkIds.remove(nffgName);
		
		/* delete all the belongs */
		Set<String> belongsIds = this.extractNffgBelongsIds(nffgName);
		for (String belongsId: belongsIds) {
			client.deleteRelationshipById(belongsId);
		}
		this.mapNffgNameBelongsIds.remove(nffgName);
		
		/* delete all the nodes */
		Set<String> nodeIds = this.extractNffgNodeIds(nffgName);
		for (String nodeId: nodeIds) {
			client.deleteNodeById(nodeId);
			this.mapNodeIdNodeName.remove(nodeId);
		}
		this.mapNffgNameNodeIds.remove(nffgName);
		
		/* delete nffg node */
		client.deleteNodeById(nffgId);
	}
	
	/* check if there is at least one path between two nodes of the same nffg */
	public  boolean isTherePath(String nffgName, String srcNodeName, String dstNodeName) throws UnknownNameException, ServiceException{
		
		String srcNodeId = this.extractNodeId(nffgName, srcNodeName);
		String dstNodeId = this.extractNodeId(nffgName, dstNodeName);
		
		Neo4JXMLClient client = new Neo4JXMLClient();
		Paths paths = client.getPathsSrcNodeDstNode(srcNodeId, dstNodeId);
		
		if(paths.getPath().size()>=1)
			return true;
		else
			return false;
	}
	
	/* OTHER PUBLIC METHODS INVOKED BY NffgService */
	//TODO: need to understand the advantages of the concurrent hash map
	public boolean isEmpty(){
		return this.mapNffgNameNffgId.isEmpty();
	}
	
	public Set<String> getNffgsNames(){
		return this.extractNffgNames();
	}
	
	public boolean containsNffg(String nffgName){
		return this.mapNffgNameNffgId.containsKey(nffgName);
	}
	
	public boolean nffgContainsNode(String nffgName, String nodeName) throws UnknownNameException, ServiceException {
		
		Set<String> nodeNames = this.getNffgNodeNames(nffgName);
		return nodeNames.contains(nodeName);
	
	}
	
	public Set<String> getNffgNodeNames(String nffgName) throws UnknownNameException, ServiceException{
		
		if(!this.containsNffg(nffgName))
			throw new UnknownNameException("missing nffg named "+nffgName);
		
		Set<String> nodeIds = this.extractNffgNodeIds(nffgName);
		Set<String> nodeNames = new HashSet<String>();
		for (String nodeId: nodeIds) {
			String nodeName = this.extractNodeName(nodeId);
			nodeNames.add(nodeName);
		}
		
		return nodeNames;
	}
	
	/* PRIVATE SET OF FUNCTIONS */
	/* TRANSALTION SET OF FUNCTIONS between neo4j world and NffgsDB */
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
	
	/* return the nodeType to send to the client */
	private NodeType translateNodeToNodeType(Node node){
		/* create the node properties */
		NodeType nodeType = new NodeType();
		String nodeName = this.getNodeProperty(node.getProperty(), "name");
		String nodeNetworkFunctionality = this.getNodeProperty(node.getProperty(), "networkFunctionality");
		nodeType.setName(nodeName);
		nodeType.setNetworkFunctionality(NetworkFunctionalityType.valueOf(nodeNetworkFunctionality));
		return nodeType;
	}
	
	/* return the linkType to send to the client */
	private LinkType translateRelationshipToLinkType(Relationship relationship) throws ServiceException{
		/* create the link properties */
		LinkType link = new LinkType();
		String linkName = this.extractLinkName(relationship.getId());
		String srcNode = this.extractNodeName(relationship.getSrcNode());
		String dstNode = this.extractNodeName(relationship.getDstNode());
		link.setName(linkName);
		link.setSourceNode(srcNode);
		link.setDestinationNode(dstNode);
		return link;
	}
	
	/* return the node to create on neo4j to represent a Nffg */
	private Node translateNffgTypeToNode(Nffgs.Nffg nffg){
		/* create the nffg properties */
		Property nameProperty = new Property();
		nameProperty.setName("name");
		nameProperty.setValue(nffg.getName());
		Property lastUpdateProperty = this.setNffgLastUpdateProperty();
		/* create local Node element */
		Node nffgToCreate = new Node();
		/* add all the properties to the new node to create */
		nffgToCreate.getProperty().add(nameProperty);
		nffgToCreate.getProperty().add(lastUpdateProperty);
		return nffgToCreate;
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
	
	private Property setNffgLastUpdateProperty(){
		/* compute now instant using default time zone*/
		SimpleDateFormat df = new SimpleDateFormat(NffgsDB.DATE_FORMAT);
		Date now = new Date(System.currentTimeMillis());
		String nowString = df.format(now);
		/* create the property */
		Property lastUpdateProperty = new Property();
		lastUpdateProperty.setName("lastUpdate");
		lastUpdateProperty.setValue(nowString);
		return lastUpdateProperty;
	}
	
	private XMLGregorianCalendar getNffgLastUpdateXMLGregorianCalendar(String lastUpdate){
		/* compute now instant using default time zone*/
		SimpleDateFormat df = new SimpleDateFormat(NffgsDB.DATE_FORMAT);
		Date lastUpdateDate;
		try{
			lastUpdateDate = df.parse(lastUpdate);
		} catch (java.text.ParseException e) {
			/* set to the current time */
			lastUpdateDate =  new Date(System.currentTimeMillis());
		}
		/* create the xml gregorian calendar */
		GregorianCalendar lastUpdateGregorianCalendar = new GregorianCalendar();
		lastUpdateGregorianCalendar.setTime(lastUpdateDate);
		XMLGregorianCalendarImpl lastUpdateXMLGregorianCalendar = new XMLGregorianCalendarImpl(lastUpdateGregorianCalendar);
		return lastUpdateXMLGregorianCalendar;
	}
	
	/* SET OF FUNCTIONS TO TO EXTRACT INFORMATIONS FROM LOCAL MAPS*/
	private String extractNffgId(String nffgName) throws ServiceException{
		if(!this.containsNffg(nffgName))
			throw new ServiceException("corrupted map! missing nffg id of nffg "+nffgName);
		
		return this.mapNffgNameNffgId.get(nffgName);
		
	}
	
	private Set<String> extractNffgNames(){
		return this.mapNffgNameNffgId.keySet();
	}
	
	private Set<String> extractNffgNodeIds(String nffgName) throws ServiceException{
		if(!this.mapNffgNameNodeIds.containsKey(nffgName))
			throw new ServiceException("corrupted map! missing node ids of nffg "+nffgName);
		
		return this.mapNffgNameNodeIds.get(nffgName);
		
	}
	
	private Set<String> extractNffgLinkIds(String nffgName) throws ServiceException{
		if(!this.mapNffgNameLinkIds.containsKey(nffgName))
			throw new ServiceException("corrupted map! missing link ids of nffg "+nffgName);
		
		return this.mapNffgNameLinkIds.get(nffgName);
		
	}
	
	private Set<String> extractNffgBelongsIds(String nffgName) throws ServiceException{
		if(!this.mapNffgNameBelongsIds.containsKey(nffgName))
			throw new ServiceException("corrupted map! missing belongs ids of nffg "+nffgName);
		
		return this.mapNffgNameBelongsIds.get(nffgName);
		
	}
	
	private String extractNodeName(String nodeId) throws ServiceException{
		if(!this.mapNodeIdNodeName.containsKey(nodeId))
			throw new ServiceException("corrupted map! missing node id "+nodeId);
		
		return this.mapNodeIdNodeName.get(nodeId);
	}
	
	private String extractNodeId(String nffgName, String nodeName) throws UnknownNameException, ServiceException{
		if(!this.nffgContainsNode(nffgName, nodeName))
			throw new UnknownNameException("missing node "+nodeName+" in nffg "+nffgName);
		
		Set<String> nodeIds = this.extractNffgNodeIds(nffgName);
		String foundNodeId = null;
		for (String nodeId : nodeIds) {
			String foundNodeName = this.extractNodeName(nodeId);
			if(foundNodeName.equals(nodeName)){
				foundNodeId = nodeId;
				break;
			}
		}
		
		if(foundNodeId == null)
			throw new ServiceException("corrupted map! missing node id of the node "+nodeName+" of the nffg "+nffgName);
		
		return foundNodeId;
		
	}
	
	private String extractLinkName(String linkId) throws ServiceException{
		if(!this.mapLinkIdLinkName.containsKey(linkId))
			throw new ServiceException("corrupted map! missing link id "+linkId);
		
		return this.mapLinkIdLinkName.get(linkId);
	}
	
	
	private List<NodeType> extractNffgNodes(String nffgName) throws ServiceException {
		Neo4JXMLClient client = new Neo4JXMLClient();
		List<NodeType> nodes = new ArrayList<NodeType>();
		try{
			Set<String> nodeIds = this.extractNffgNodeIds(nffgName);
			for (String nodeId : nodeIds) {
				Node node = client.getNodeById(nodeId);
				NodeType nodeType = translateNodeToNodeType(node);
				nodes.add(nodeType);
			}
		} catch (RuntimeException e) {
			throw new ServiceException("Error retrieving nodes of nffg " + nffgName);
		}
		return nodes;
	}
	
	private List<LinkType> extractNffgLinks(String nffgName) throws ServiceException {
		Neo4JXMLClient client = new Neo4JXMLClient();
		List<LinkType> links = new ArrayList<LinkType>();
		try{
			Set<String> linkIds = this.extractNffgLinkIds(nffgName);
			for (String linkId : linkIds) {
				Relationship relationship = client.getRelationshipById(linkId);
				LinkType linkType = this.translateRelationshipToLinkType(relationship);
				links.add(linkType);
			}
		} catch (RuntimeException e) {
			throw new ServiceException("Error retrieving links of nffg " + nffgName);
		} 
		return links;
	}
	
	/* SET OF FUNCTIONS TO STORE/EXTRACT INFORMATION TO/FROM NEO4J */
	
}