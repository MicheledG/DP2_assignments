package it.polito.dp2.NFFG.sol2;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.Random.NffgVerifierFactoryImpl;
import it.polito.dp2.NFFG.lab2.NoGraphException;
import it.polito.dp2.NFFG.lab2.ReachabilityTester;
import it.polito.dp2.NFFG.lab2.ReachabilityTesterException;
import it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory;
import it.polito.dp2.NFFG.lab2.ServiceException;
import it.polito.dp2.NFFG.lab2.UnknownNameException;

public class ReachabilityTesterImpl implements ReachabilityTester {
	
	
	URI baseURL = null;
	NffgVerifier nffgVerifier;
	String uploadedNffgName = null;
	Nodes uploadedNodes = null;
	
	public ReachabilityTesterImpl() {
		try {
			/*exploits random data generator*/
			NffgVerifierFactory nffgVerifierFactory = NffgVerifierFactoryImpl.newInstance();
			this.nffgVerifier = nffgVerifierFactory.newNffgVerifier();
		} catch (NffgVerifierException | FactoryConfigurationError e) {
			System.err.println("Exception instantiating a new NffgVerifier");
			e.printStackTrace();
		}
		String baseURL = System.getProperty("it.polito.dp2.NFFG.lab2.URL");
		if(baseURL == null)
			this.baseURL = URI.create("http://localhost:8080/Neo4JXML/rest/");
		else
			this.baseURL = URI.create(baseURL);
	}
	
	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		/* Search the Nffg named "name" */
		NffgReader nffgReader = nffgVerifier.getNffg(name);
		if(nffgReader == null)
			throw new UnknownNameException();
		/* Delete all the eventual nodes on the Neo4JXML DB */
		//TODO: debug
		System.out.println("Deleting...");
		deleteNFFG();
		/* Load all the nodes of the Nffg */
		//TODO: debug
		System.out.println("Uploading...");
		for (NodeReader nodeReader: nffgReader.getNodes()) {
			//TODO: debug
			System.out.println("Uploading node "+nodeReader.getName()+"...");
			loadNode(nodeReader);
		}
		
		/* download the list of all the uploaded nodes */
		//TODO: debug
		System.out.println("Resolving node id...");
		this.uploadedNodes = downloadNodes();
		
		/* Load all the relationships of the node */
		for (NodeReader nodeReader : nffgReader.getNodes()) {
			for (LinkReader linkReader : nodeReader.getLinks()) {
				//TODO: debug
				System.out.println("Uploading relationship "+linkReader.getName()+"...");
				loadRelationship(linkReader);
			}
		}
		
		/* last thing: if the loading ends up successfully set uploadedNffgName */
		uploadedNffgName = name;
	}

	@Override
	public boolean testReachability(String srcName, String destName)
			throws UnknownNameException, ServiceException, NoGraphException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCurrentGraphName() {
		return this.uploadedNffgName;
	}
	
	/* Delete all the eventual nodes on the Neo4JXML DB */
	private void deleteNFFG() throws ServiceException {
		Client client = ClientBuilder.newClient();
		try{
			Response response = client.target(this.baseURL+"resource/nodes").request("application/xml").delete();
			System.out.print("Response status code: ");
			System.out.print(response.getStatus());
			System.out.println();
		}
		catch (RuntimeException e) {
			throw new ServiceException(e);	
		}
		this.uploadedNffgName = null;
		this.uploadedNodes = null;
	}
	
	/* Load a node */
	private void loadNode(NodeReader nodeReader) throws ServiceException{
		/* create a node object to be loaded*/
		Node node = newNode(nodeReader);
		/* load the node through API*/
		Client client = ClientBuilder.newClient();
		try{
			Response response = client.target(this.baseURL+"resource/node").request("application/xml").post(Entity.xml(node));
			System.out.print("Response status code: ");
			System.out.print(response.getStatus());
			System.out.println();
		}
		catch (RuntimeException e) {
			throw new ServiceException(e);	
		}
	}
	
	private Node newNode(NodeReader nodeReader){
		Node node = new Node();
		Property name = new Property();
		name.setName("name");
		name.setValue(nodeReader.getName());
		node.getProperty().add(name);
		return node;
	}
	
	/* Load a relationship */
	private void loadRelationship(LinkReader linkReader) throws ServiceException{
		/* create a relationship object to be loaded*/
		Relationship relationship = newRelationship(linkReader);
		/* load the relationship through API*/
		Client client = ClientBuilder.newClient();
		try{
			String nodeId = relationship.getSrcNode();
			Response response = client.target(this.baseURL+"resource/node/"+nodeId+"/relationship").request("application/xml").post(Entity.xml(relationship));
			System.out.print("Response status code: ");
			System.out.print(response.getStatus());
			System.out.println();
		}
		catch (RuntimeException e) {
			throw new ServiceException(e);	
		}
	}
	
	private Relationship newRelationship(LinkReader linkReader) throws ServiceException{
		Relationship relationship = new Relationship();
		relationship.setId(linkReader.getName());
		relationship.setType("Link");
		relationship.setSrcNode(findNodeIdFromNodeName(linkReader.getSourceNode().getName()));
		relationship.setDstNode(findNodeIdFromNodeName(linkReader.getDestinationNode().getName()));
		return relationship;
	}
	
	/* download the list of all the uploaded nodes */
	private Nodes downloadNodes() throws ServiceException{
		Nodes nodes = null;
		/* download the nodes through API*/
		Client client = ClientBuilder.newClient();
		try{
			Response response = client.target(this.baseURL+"resource/nodes").request("application/xml").get();
			System.out.print("Response status code: ");
			System.out.print(response.getStatus());
			System.out.println();
			nodes = response.readEntity(Nodes.class);
		}
		catch (RuntimeException e) {
			throw new ServiceException(e);	
		}
		
		return nodes;
	}
	
	/* find the node id of a node */
	private String findNodeIdFromNodeName(String nodeName) throws ServiceException{
		for (Nodes.Node node : this.uploadedNodes.getNode()) {
			for (Property property : node.getProperty()) {
				if(property.getName().equals("name") && property.getValue().equals(nodeName))
					return node.getId();
			}
		}
		/* node not found... */
		throw new ServiceException("Node ID not found for the node:" + nodeName);
	}
	
	//TODO:debug
	public static void main(String[] args){
		ReachabilityTesterFactory reachabilityTesterFactory = ReachabilityTesterFactoryExt.newInstance();
		ReachabilityTester reachabilityTester = null;
		try {
			reachabilityTester = reachabilityTesterFactory.newReachabilityTester();
		} catch (ReachabilityTesterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println("Looking for an eventual nffg already updated...");
			String nffgName = reachabilityTester.getCurrentGraphName();
			if(nffgName == null)
				System.out.println("No updated nffg");
			else
				System.out.println("Nffg updated: "+nffgName);
			for(Integer i = 0; i < 10; i++ ){
				nffgName = "Nffg" + i.toString();
				System.out.println("Deleting previous updated nffg and uploading "+nffgName+"...");
				reachabilityTester.loadNFFG(nffgName);
				nffgName = reachabilityTester.getCurrentGraphName();
				if(nffgName == null)
					System.out.println("No updated nffg");
				else
					System.out.println("Nffg updated: "+nffgName);
			}
			
		} catch (UnknownNameException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
