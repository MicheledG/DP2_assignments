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
	
	
	private URI baseURL = null;
	private NffgVerifier nffgVerifier;
	private String uploadedNffgName = null;
	private Nodes uploadedNodes = null;
	
	public ReachabilityTesterImpl() {
		try {
			/*exploits random data generator*/
			NffgVerifierFactory nffgVerifierFactory = NffgVerifierFactoryImpl.newInstance();
			this.nffgVerifier = nffgVerifierFactory.newNffgVerifier();
		} catch (NffgVerifierException | FactoryConfigurationError e) {
			System.err.println("Exception instantiating a new NffgVerifier");
			e.printStackTrace();
		}
		//TODO: check url in the generate-artifacts target
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
		/* check if a graph is already uploaded */
		if(getCurrentGraphName() == null)
			throw new NoGraphException();
		
		/* check if the srcName and dstName nodes are present into the graph uploaded */
		String srcNodeId = findNodeIdFromNodeName(srcName);
		String dstNodeId = findNodeIdFromNodeName(destName);
		
		/* query for the paths between srcNode and dstNode */
		return queryPathsSrcNodeDstNode(srcNodeId,dstNodeId);
	}
	
	private boolean queryPathsSrcNodeDstNode(String srcNodeId, String dstNodeId) throws ServiceException {
		Client client = ClientBuilder.newClient();
		try{
			Response response = client.target(this.baseURL+"resource/node/"+srcNodeId+"/paths")
					.queryParam("dst", dstNodeId).request("application/xml").get();
			handleResponseStatusCode(response);
			Paths paths = response.readEntity(Paths.class);
			if(paths.getPath().size() == 0)
				return false;
			else if (paths.getPath().size() > 0)
				return true;
			else 
				throw new ServiceException("Unexpected paths response");
		}
		catch (RuntimeException e) {
			throw new ServiceException(e);	
		}
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
			handleResponseStatusCode(response);
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
			handleResponseStatusCode(response);
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
		
		try{
			/* create a relationship object to be loaded*/
			Relationship relationship = newRelationship(linkReader);
			/* load the relationship through API*/
			Client client = ClientBuilder.newClient();
		
			String nodeId = relationship.getSrcNode();
			Response response = client.target(this.baseURL+"resource/node/"+nodeId+"/relationship").request("application/xml").post(Entity.xml(relationship));
			handleResponseStatusCode(response);
		}
		catch (RuntimeException | UnknownNameException e) {
			throw new ServiceException(e);	
		}
	}
	
	private Relationship newRelationship(LinkReader linkReader) throws UnknownNameException{
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
			handleResponseStatusCode(response);
			nodes = response.readEntity(Nodes.class);
		}
		catch (RuntimeException e) {
			throw new ServiceException(e);	
		}
		
		return nodes;
	}
	
	/* find the node id of a node */
	private String findNodeIdFromNodeName(String nodeName) throws UnknownNameException{
		for (Nodes.Node node : this.uploadedNodes.getNode()) {
			for (Property property : node.getProperty()) {
				if(property.getName().equals("name") && property.getValue().equals(nodeName))
					return node.getId();
			}
		}
		/* node not found... */
		throw new UnknownNameException("Node ID not found for the node:" + nodeName);
	}
	
	/* handle response status code */
	private void handleResponseStatusCode(Response response){
		System.out.print("Response status code: ");
		System.out.print(response.getStatus());
		System.out.println();
	}
	
	//TODO:debug
	public NffgReader getNffgReader(String name){
		return this.nffgVerifier.getNffg(name);
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
		
		ReachabilityTesterImpl reach = (ReachabilityTesterImpl) reachabilityTester;
		
		try {
			System.out.println("Looking for an eventual nffg already uploaded...");
			String nffgName = reachabilityTester.getCurrentGraphName();
			if(nffgName == null)
				System.out.println("No uploaded nffg");
			else
				System.out.println("Nffg uploaded: "+nffgName);
			for(Integer i = 0; i < 10; i++ ){
				nffgName = "Nffg" + i.toString();
				System.out.println("Deleting previous uploaded nffg and uploading "+nffgName+"...");
				reachabilityTester.loadNFFG(nffgName);
				nffgName = reachabilityTester.getCurrentGraphName();
				if(nffgName == null)
					System.out.println("No uploaded nffg");
				else
					System.out.println("Nffg uploaded: "+nffgName);
				/* test that all the links are found */
				System.out.println("Testing existing links...");
				for (NodeReader nodeReader : reach.getNffgReader(reach.getCurrentGraphName()).getNodes()) {
					for (LinkReader linkReader : nodeReader.getLinks()) {
						System.out.print("Relationship between " + linkReader.getSourceNode().getName() +
								" and " + linkReader.getDestinationNode().getName() + ": ");
						if(reach.testReachability(linkReader.getSourceNode().getName(), linkReader.getDestinationNode().getName()))
							System.out.println("existing");
						else
							System.out.println("missing");
					}
				}
			}
			
		} catch (UnknownNameException | ServiceException | NoGraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
