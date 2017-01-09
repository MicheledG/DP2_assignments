package it.polito.dp2.NFFG.sol3.service.sol2;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol3.service.lab2.NoGraphException;
import it.polito.dp2.NFFG.sol3.service.lab2.ReachabilityTester;
import it.polito.dp2.NFFG.sol3.service.lab2.ReachabilityTesterException;
import it.polito.dp2.NFFG.sol3.service.lab2.ReachabilityTesterFactory;
import it.polito.dp2.NFFG.sol3.service.lab2.ServiceException;
import it.polito.dp2.NFFG.sol3.service.lab2.UnknownNameException;

/* modifications */
import it.polito.dp2.NFFG.sol3.service.neo4jxml.*;

public class ReachabilityTesterImpl implements ReachabilityTester {
	
	
	private URI baseURL = null;
	private NffgVerifier nffgVerifier;
	private String uploadedNffgName = null;
	private Map<String, String> mapNodeNodeId;
	private ObjectFactory objectFactory;
	
	public ReachabilityTesterImpl() {
		try {
			/*exploits random data generator*/
			NffgVerifierFactory nffgVerifierFactory = NffgVerifierFactory.newInstance();
			this.nffgVerifier = nffgVerifierFactory.newNffgVerifier();
			this.mapNodeNodeId = new HashMap<String, String>();
			this.objectFactory = new ObjectFactory();
		} catch (NffgVerifierException | FactoryConfigurationError e) {
			System.err.println("Exception instantiating a new NffgVerifier");
			e.printStackTrace();
		}
		/* check base url from the system property */
		String baseURL = System.getProperty("it.polito.dp2.NFFG.lab2.URL");
		if(baseURL == null)
			this.baseURL = URI.create("http://localhost:8080/Neo4JXML/rest");
		else
			this.baseURL = URI.create(baseURL);
	}
	
	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		/* Search the Nffg named "name" */
		NffgReader nffgReader = nffgVerifier.getNffg(name);
		if(nffgReader == null)
			throw new UnknownNameException();
		/* Delete all the eventual nodes on the Neo4J DB */
		System.out.println("Deleting nodes on Neo4J DB...");
		deleteNFFG();
		/* Load all the nodes of the Nffg */
		System.out.println("Uploading nodes on Neo4J DB...");
		for (NodeReader nodeReader: nffgReader.getNodes()) {
			System.out.println("Uploading node "+nodeReader.getName()+"...");
			loadNode(nodeReader);
		}
				
		/* Load all the relationships of the node */
		for (NodeReader nodeReader : nffgReader.getNodes()) {
			for (LinkReader linkReader : nodeReader.getLinks()) {
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
	
	@Override
	public String getCurrentGraphName() {
		return this.uploadedNffgName;
	}
	
	/* Delete all the eventual nodes on the Neo4JXML DB */
	private void deleteNFFG() throws ServiceException {
		Client client = ClientBuilder.newClient();
		try{
			String response = client.target(this.baseURL+"/resource/nodes").request("application/xml").delete(String.class);
			this.uploadedNffgName = null;
			this.mapNodeNodeId.clear();
		}
		catch (RuntimeException e) {
			/* catch all the possible exception indeed the only throwable exception is ServiceException */
			throw new ServiceException(e);	
		}	
	}
	
	/* Load a node and retrieve its id*/
	private void loadNode(NodeReader nodeReader) throws ServiceException{
		/* create a node object to be loaded*/
		Node nodeToLoad = newNode(nodeReader);
		/* load the node through API*/
		Client client = ClientBuilder.newClient();
		try{
			Node nodeLoaded = client.target(this.baseURL+"/resource/node").request("application/xml").post(Entity.xml(nodeToLoad), Node.class);
			/* store pair nodeName - nodeId into the map */
			this.mapNodeNodeId.put(nodeReader.getName(), nodeLoaded.getId());
		}
		catch (RuntimeException e) {
			throw new ServiceException(e);	
		}	
	}
	
	/* create a Node object to upload on Neo4J DB starting from a nodeReader */
	private Node newNode(NodeReader nodeReader){
		Node node = this.objectFactory.createNode();
		Property name = this.objectFactory.createProperty();
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
			String response = client.target(this.baseURL+"/resource/node/"+nodeId+"/relationship").request("application/xml")
					.post(Entity.xml(relationship), String.class);
		}
		catch (RuntimeException | UnknownNameException e) {
			throw new ServiceException(e);	
		}
	}
	
	/* create a Relationship object to upload on Neo4J DB starting from a linkReader */
	private Relationship newRelationship(LinkReader linkReader) throws UnknownNameException{
		Relationship relationship = this.objectFactory.createRelationship();
		relationship.setId(linkReader.getName());
		relationship.setType("Link");
		relationship.setSrcNode(findNodeIdFromNodeName(linkReader.getSourceNode().getName()));
		relationship.setDstNode(findNodeIdFromNodeName(linkReader.getDestinationNode().getName()));
		return relationship;
	}
	
	/* find the node id of a node */
	private String findNodeIdFromNodeName(String nodeName) throws UnknownNameException{		
		/* node not found... */
		if(!this.mapNodeNodeId.containsKey(nodeName))
			throw new UnknownNameException("Node ID not found for the node:" + nodeName);
		else
			return this.mapNodeNodeId.get(nodeName);
		
	}
	
	/* return true if there is at least one path between the srcNode and the dstNode else return false */
	private boolean queryPathsSrcNodeDstNode(String srcNodeId, String dstNodeId) throws ServiceException {
		Client client = ClientBuilder.newClient();
		try{
			Paths paths = client.target(this.baseURL+"/resource/node/"+srcNodeId+"/paths")
					.queryParam("dst", dstNodeId).request("application/xml").get(Paths.class);	
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
	
	//TODO:debug
	public NffgReader getNffgReader(String name){
		return this.nffgVerifier.getNffg(name);
	}
	
	//TODO:debug
	public static void main(String[] args){
		ReachabilityTesterFactory reachabilityTesterFactory = ReachabilityTesterFactory.newInstance();
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
