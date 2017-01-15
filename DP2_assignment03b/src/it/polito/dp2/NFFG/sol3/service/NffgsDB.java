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
	private LinkType translateRelationshipToLinkType(Relationship relationship) throws UnknownNameException{
		/* create the link properties */
		LinkType link = new LinkType();
		String linkName = this.findLinkName(relationship.getId());
		String srcNode = this.findNodeName(relationship.getSrcNode());
		String dstNode = this.findNodeName(relationship.getDstNode());
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
	
	private String findNodeName(String nodeId) throws UnknownNameException{
		if(this.mapNodeIdNodeName.containsKey(nodeId))
			return this.mapNodeIdNodeName.get(nodeId);
		else
			throw new UnknownNameException("Node with id "+nodeId+" not found!");	
	}
	
	private String findNodeId(String nffgName, String nodeName) throws UnknownNameException{
		if(this.containsNffg(nffgName)){
			/* nffg is on the DB */
			Set<String> nffgNodeIds = this.mapNffgNameNodeIds.get(nffgName);
			String nodeId = null;
			for (String nffgNodeId : nffgNodeIds) {
				/* find among all nffg's nodes the one we are interested in */
				String tmpNodeId = this.findNodeName(nffgNodeId);
				if(tmpNodeId.equals(nodeName)){
					nodeId = tmpNodeId;
					break;
				}
			}
			return nodeId;
		}
		else
			throw new UnknownNameException("nffg named "+nffgName+" not found");
	}
	
	private String findLinkName(String linkId) throws UnknownNameException{
		if(this.mapLinkIdLinkName.containsKey(linkId))
			return this.mapLinkIdLinkName.get(linkId);
		else
			throw new UnknownNameException("Link with id "+linkId+" not found!");	
	}
	
	private String findNffgId(String nffgName) throws UnknownNameException{
		if(this.mapNffgNameNffgId.containsKey(nffgName))
			return this.mapNffgNameNffgId.get(nffgName);
		else
			throw new UnknownNameException("Nffg named "+nffgName+" not found!");	
	}
	
	private List<NodeType> getNffgNodes(String nffgName) throws ServiceException {
		Neo4JXMLClient client = new Neo4JXMLClient();
		List<NodeType> nodes = new ArrayList<NodeType>();
		try{
			for (String nodeId : this.mapNffgNameNodeIds.get(nffgName)) {
				Node node = client.getNodeById(nodeId);
				NodeType nodeType = translateNodeToNodeType(node);
				nodes.add(nodeType);
			}
		} catch (RuntimeException e) {
			throw new ServiceException("Error retrieving nodes of nffg " + nffgName);
		}
		return nodes;
	}
	
	private List<LinkType> getNffgLinks(String nffgName) throws ServiceException {
		Neo4JXMLClient client = new Neo4JXMLClient();
		List<LinkType> links = new ArrayList<LinkType>();
		try{
			for (String linkId : this.mapNffgNameLinkIds.get(nffgName)) {
				Relationship relationship = client.getRelationshipById(linkId);
				LinkType linkType = this.translateRelationshipToLinkType(relationship);
				links.add(linkType);
			}
		} catch (UnknownNameException | RuntimeException e) {
			throw new ServiceException("Error retrieving links of nffg " + nffgName);
		} 
		return links;
	}
	
	/* return a NFFG stored inside neo4j */
	private Nffgs.Nffg getNffg(String nffgName) throws UnknownNameException, ServiceException {
		/* find nffgId of the node that models the nffg */
		String nffgId = this.findNffgId(nffgName);
		/* get the nffg properties */
		Neo4JXMLClient client = new Neo4JXMLClient();
		Node nffgNode = client.getNodeById(nffgId);
		String lastUpdateString = this.getNodeProperty(nffgNode.getProperty(), "lastUpdate");
		XMLGregorianCalendar lastUpdateCalendar = this.getNffgLastUpdateXMLGregorianCalendar(lastUpdateString);
		/* get all the nodes of that nffg */
		List<NodeType> nffgNodes = this.getNffgNodes(nffgName);
		/* get all the relationships of the nffg */
		List<LinkType> nffgLinks = this.getNffgLinks(nffgName);
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
	
	/* create a NFFG on neo4j */
	private void createNffg(Nffgs.Nffg nffg) throws ServiceException{
		
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
			/* update NffgName <=> NffgId map */
			this.mapNffgNameNffgId.put(nffg.getName(), nffgCreated.getId());
			
			/* update NffgName <=> BelongsIds map */
			this.mapNffgNameBelongsIds.put(nffg.getName(), createdBelongs);
			
			/* update NffgName <=> NodeIds map + NodeId <=> NodeName map*/
			Set<String> nffgNodeIds = new HashSet<String>(); 
			for (NodeType node : nffg.getNodes().getNode()) {
				String nodeName = node.getName();
				String nodeId = mapCreatedNodeNameNodeId.get(nodeName);
				nffgNodeIds.add(nodeId);
				this.mapNodeIdNodeName.put(nodeId, nodeName);
			}
			this.mapNffgNameNodeIds.put(nffg.getName(), nffgNodeIds);
			
			/* update NffgName <=> LinkIds map + LinkId <=> LinkName map */
			Set<String> nffgLinkIds = new HashSet<String>(); 
			for (LinkType link : nffg.getLinks().getLink()) {
				String linkName = link.getName(); 
				String linkId = mapCreatedLinkNameLinkId.get(linkName);
				nffgLinkIds.add(linkId);
				this.mapLinkIdLinkName.put(linkId, linkName);
			}
			this.mapNffgNameLinkIds.put(nffg.getName(), nffgLinkIds);
			
		} catch (RuntimeException e) {
			throw new ServiceException("Error loading nffg named " + nffg.getName());
		} 
	}
	
	/* store the nffg contained inside nffgs (1 or more)*/
	public void storeNffgs(Nffgs nffgs) throws AlreadyLoadedException, ServiceException {
		for (Nffgs.Nffg nffg: nffgs.getNffg()) {
			if(!this.containsNffg(nffg.getName()))
				this.createNffg(nffg);
			else 
				throw new AlreadyLoadedException("already loaded nffg named " + nffg.getName());
		}
	}
	
	/* return the list of all the nffg */
	public Nffgs getNffgs() throws NoGraphException, ServiceException{
		if(this.mapNffgNameNffgId.isEmpty())
			throw new NoGraphException("no nffg in the DB");
		
		Nffgs nffgs = new Nffgs();
		try{
			for (String nffgName: this.mapNffgNameNffgId.keySet()) {
				Nffgs.Nffg nffg = getNffg(nffgName);
				nffgs.getNffg().add(nffg);
			}
		} 
		catch (UnknownNameException e) {
			throw new ServiceException(e);
		}
		return nffgs;
	}
	
	/* return nffgs containing a single nffg */
	public Nffgs getNffgs(String nffgName) throws UnknownNameException,ServiceException{
		Nffgs nffgs = new Nffgs();	
		Nffgs.Nffg nffg = getNffg(nffgName);
		nffgs.getNffg().add(nffg);
		return nffgs;
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
		String nffgId = this.findNffgId(nffgName);
		
		Neo4JXMLClient client = new Neo4JXMLClient();
		/* delete all the links */
		Set<String> linkIds = this.mapNffgNameLinkIds.get(nffgName);
		for (String linkId: linkIds) {
			client.deleteRelationshipById(linkId);
			this.mapLinkIdLinkName.remove(linkId);
		}
		this.mapNffgNameLinkIds.remove(nffgName);
		
		/* delete all the belongs */
		Set<String> belongsIds = this.mapNffgNameBelongsIds.get(nffgName);
		for (String belongsId: belongsIds) {
			client.deleteRelationshipById(belongsId);
		}
		this.mapNffgNameBelongsIds.remove(nffgName);
		
		/* delete all the nodes */
		Set<String> nodeIds = this.mapNffgNameNodeIds.get(nffgName);
		for (String nodeId: nodeIds) {
			client.deleteNodeById(nodeId);
			this.mapNodeIdNodeName.remove(nodeId);
		}
		this.mapNffgNameNodeIds.get(nffgName);
		
		/* delete nffg node */
		client.deleteNodeById(nffgId);
		this.mapNffgNameNffgId.remove(nffgName);
		this.mapNffgNameNodeIds.remove(nffgName);
		this.mapNffgNameLinkIds.remove(nffgName);
		this.mapNffgNameBelongsIds.remove(nffgName);
	}
	
	public Set<String> getNffgsNames(){
		return this.mapNffgNameNffgId.keySet();
	}
	
	public boolean containsNffg(String nffgName){
		return this.mapNffgNameNffgId.containsKey(nffgName);
	}
	
	public boolean isEmpty(){
		return this.mapNffgNameNffgId.isEmpty();
	}
	
	/* check if there is at least one path between two nodes of the same nffg */
	public boolean isTherePath(String nffgName, String srcNodeName, String dstNodeName) throws UnknownNameException, ServiceException{
		String srcNodeId = this.findNodeId(nffgName, srcNodeName);
		String dstNodeId = this.findNodeId(nffgName, dstNodeName);
		
		Neo4JXMLClient client = new Neo4JXMLClient();
		Paths paths = client.getPathsSrcNodeDstNode(srcNodeId, dstNodeId);
		
		if(paths.getPath().size()>=1)
			return true;
		else
			return false;
	}
	
}