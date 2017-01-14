package it.polito.dp2.NFFG.sol3.service;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;

import it.polito.dp2.NFFG.sol3.service.neo4jxml.*;

public class Neo4JXMLClient {
	
	private URI baseURL = null;
	
	public Neo4JXMLClient(){
		/* check base url from the system property */
		String baseURL = System.getProperty("it.polito.dp2.NFFG.lab3.NEO4JURL");
		if(baseURL == null)
			this.baseURL = URI.create("http://localhost:8080/Neo4JXML/rest");
		else
			this.baseURL = URI.create(baseURL);
	}
	
	public Node createNode(Node nodeToCreate) {
		/* create node to Neo4JXML */
		Client client = ClientBuilder.newClient();
		Node nodeCreated = client.target(this.baseURL+"/resource/node").request("application/xml").post(Entity.xml(nodeToCreate), Node.class);
		return nodeCreated;
	}
	
	public Node getNodeById(String nodeId) {
		/* get a node from Neo4JXML */
		Client client = ClientBuilder.newClient();
		Node node = client.target(this.baseURL+"/resource/node/"+nodeId).request("application/xml").get(Node.class);
		return node;
	}
	
	public Nodes getAllNodes() {
		/* get a all the nodes from Neo4JXML */
		Client client = ClientBuilder.newClient();
		Nodes nodes = client.target(this.baseURL+"/resource/nodes").request("application/xml").get(Nodes.class);
		return nodes;
	}
	
	public void deleteNodeById(String nodeId) {
		/* delete a node from Neo4JXML */
		Client client = ClientBuilder.newClient();
		String string = client.target(this.baseURL+"/resource/node/"+nodeId).request("application/xml").delete(String.class);
		return;
	}
	
	
	public void deleteAllNodes() {
		/* delete all the nodes from Neo4JXML */
		Client client = ClientBuilder.newClient();
		String string = client.target(this.baseURL+"/resource/nodes").request("application/xml").delete(String.class);
		return;
	}
	
	public void createLabel(String nodeId, String labelValue) {
		/* create the label locally */
		Labels labels = new Labels();
		labels.getValue().add(labelValue);
		/* load label to the node in Neo4J */
		Client client = ClientBuilder.newClient();
		String string = client.target(this.baseURL+"/resource/node/"+nodeId+"/label").request("application/xml").post(Entity.xml(labels), String.class);
		return;
	}
	
	public Labels getLabelByNodeId(String nodeId) {
		/* get the label from Neo4J*/
		Client client = ClientBuilder.newClient();	
		Labels labels = client.target(this.baseURL+"/resource/node/"+nodeId+"/label").request("application/xml").get(Labels.class);
		return labels;
	}
	
	public void deleteLabelByNodeId(String nodeId, String labelValue) {
		/* delete the label from Neo4J*/
		Client client = ClientBuilder.newClient();
		String string = client.target(this.baseURL+"/resource/node/"+nodeId+"/label/"+labelValue).request("application/xml").delete(String.class);
		return;
	}

	public Relationship createRelationship(String srcNodeId, String dstNodeId, String type)  {
		/* create the relationship locally */
		Relationship relationshipToCreate = new Relationship();
		relationshipToCreate.setDstNode(dstNodeId);
		relationshipToCreate.setSrcNode(srcNodeId);
		relationshipToCreate.setType(type);
		/* create relationship to Neo4J */
		Client client = ClientBuilder.newClient();
		Relationship relationshipCreated = client
				.target(this.baseURL+"/resource/node/"+relationshipToCreate.getSrcNode()+"/relationship")
				.request("application/xml").post(Entity.xml(relationshipToCreate), Relationship.class);
		return relationshipCreated;
	}
	
	public Relationship getRelationshipById(String relationshipId)  {
		/* get the relationship from Neo4J*/
		Client client = ClientBuilder.newClient();
		Relationship relationship = client.target(this.baseURL+"/resource/relationship/"+relationshipId).request("application/xml").get(Relationship.class);
		return relationship;
	}
	
	public void deleteRelationshipById(String relationshipId)  {
		/* delete the relationship from Neo4J*/
		Client client = ClientBuilder.newClient();
		String string = client.target(this.baseURL+"/resource/relationship/"+relationshipId).request("application/xml").delete(String.class);
		return;
	}
	
	public Paths getPathsSrcNodeDstNode(String srcNodeId, String dstNodeId)  {
		/* get the paths between src and dst node from Neo4J*/
		Client client = ClientBuilder.newClient();
		Paths paths = client.target(this.baseURL+"/resource/node/"+srcNodeId+"/paths").queryParam("dst", dstNodeId).request("application/xml").get(Paths.class);
		return paths;
	}

}
