package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

import it.polito.dp2.NFFG.sol3.service.neo4jxml.*;

public class Neo4JXMLClient {
	
	private URI baseURL = null;
	private ObjectFactory objectFactory;
	
	public Neo4JXMLClient(){
		/* check base url from the system property */
		String baseURL = System.getProperty("it.polito.dp2.NFFG.lab3.NEO4JURL");
		if(baseURL == null)
			this.baseURL = URI.create("http://localhost:8080/Neo4JXML/rest");
		else
			this.baseURL = URI.create(baseURL);
	}
	
	public Node createNode(String propertyName, String propertyValue) throws RuntimeException{
		/* create local Node element */
		Node node = this.objectFactory.createNode();
		Property nameProperty = this.objectFactory.createProperty();
		nameProperty.setName(propertyName);
		nameProperty.setValue(propertyValue);
		node.getProperty().add(nameProperty);
		/* load node to Neo4JXML */
		Client client = ClientBuilder.newClient();
		try{
			Node nodeLoaded = client.target(this.baseURL+"/resource/node").request("application/xml").post(Entity.xml(node), Node.class);
			return nodeLoaded;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public Node getNodeById(String nodeId) throws RuntimeException{
		/* get a node from Neo4JXML */
		Client client = ClientBuilder.newClient();
		try{
			Node node = client.target(this.baseURL+"/resource/node/"+nodeId).request("application/xml").get(Node.class);
			return node;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public Nodes getAllNodes(){
		/* get a all the nodes from Neo4JXML */
		Client client = ClientBuilder.newClient();
		try{
			Nodes nodes = client.target(this.baseURL+"/resource/nodes").request("application/xml").get(Nodes.class);
			return nodes;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public void deleteNodeById(String nodeId){
		/* delete a node from Neo4JXML */
		Client client = ClientBuilder.newClient();
		try{
			String string = client.target(this.baseURL+"/resource/node/"+nodeId).request("application/xml").delete(String.class);
			return;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	
	public void deleteAllNodes(){
		/* delete all the nodes from Neo4JXML */
		Client client = ClientBuilder.newClient();
		try{
			String string = client.target(this.baseURL+"/resource/nodes").request("application/xml").delete(String.class);
			return;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public void createLabel(String nodeId, String labelValue){
		/* create the label locally */
		Labels labels = this.objectFactory.createLabels();
		labels.getValue().add(labelValue);
		/* load label to the node in Neo4J */
		Client client = ClientBuilder.newClient();
		try{
			String string = client.target(this.baseURL+"/resource/node/"+nodeId+"/label").request("application/xml").post(Entity.xml(labels), String.class);
			return;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public Labels getLabelByNodeId(String nodeId){
		/* get the label from Neo4J*/
		Client client = ClientBuilder.newClient();
		try{
			Labels labels = client.target(this.baseURL+"/resource/node/"+nodeId+"/label").request("application/xml").get(Labels.class);
			return labels;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public void deleteLabelByNodeId(String nodeId, String labelValue){
		/* delete the label from Neo4J*/
		Client client = ClientBuilder.newClient();
		try{
			String string = client.target(this.baseURL+"/resource/node/"+nodeId+"/label/"+labelValue).request("application/xml").delete(String.class);
			return;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}

	public Relationship createRelationship(String srcNodeId, String dstNodeId, String type){
		/* create the relationship locally */
		Relationship relationship = this.objectFactory.createRelationship();
		relationship.setDstNode(dstNodeId);
		relationship.setSrcNode(srcNodeId);
		relationship.setType(type);
		/* load relationship to the node in Neo4J */
		Client client = ClientBuilder.newClient();
		try{
			Relationship relationshipLoaded = client.target(this.baseURL+"/resource/node/"+srcNodeId+"/relationship").request("application/xml").post(Entity.xml(relationship), Relationship.class);
			return relationshipLoaded;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public Relationship getRelationshipById(String relationshipId){
		/* get the relationship from Neo4J*/
		Client client = ClientBuilder.newClient();
		try{
			Relationship relationship = client.target(this.baseURL+"/resource/relationship/"+relationshipId).request("application/xml").get(Relationship.class);
			return relationship;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public void deleteRelationshipById(String relationshipId){
		/* delete the relationship from Neo4J*/
		Client client = ClientBuilder.newClient();
		try{
			String string = client.target(this.baseURL+"/resource/relationship/"+relationshipId).request("application/xml").delete(String.class);
			return;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}
	
	public Paths getPathsSrcNodeDstNode(String srcNodeId, String dstNodeId){
		/* get the paths between src and dst node from Neo4J*/
		Client client = ClientBuilder.newClient();
		try{
			Paths paths = client.target(this.baseURL+"/resource/node/"+srcNodeId+"/paths").queryParam("dst", dstNodeId).request("application/xml").get(Paths.class);
			return paths;
		}
		catch (RuntimeException e) {
			throw e;	
		}
	}

}
