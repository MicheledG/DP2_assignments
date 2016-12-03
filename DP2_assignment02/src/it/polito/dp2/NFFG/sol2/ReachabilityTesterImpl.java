package it.polito.dp2.NFFG.sol2;

import java.net.URI;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.RedirectionException;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ResponseProcessingException;
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
	
	NffgVerifier nffgVerifier;
	URI baseURL;
	
	public ReachabilityTesterImpl() {
		try {
			/*exploits random data generator*/
			NffgVerifierFactory nffgVerifierFactory = NffgVerifierFactoryImpl.newInstance();
			this.nffgVerifier = nffgVerifierFactory.newNffgVerifier();
		} catch (NffgVerifierException | FactoryConfigurationError e) {
			System.err.println("Exception instantiating a new NffgVerifier");
			e.printStackTrace();
		}
		baseURL = URI.create(System.getProperty("it.polito.dp2.NFFG.lab2.URL"));
	}
	
	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		/* Search the Nffg named "name" */
		NffgReader nffgReader = nffgVerifier.getNffg(name);
		if(nffgReader == null)
			throw new UnknownNameException();
		/* Delete all the eventual nodes on the Neo4JXML DB */
		deleteNFFG();
		/* Load all the nodes of the Nffg named "name" */
		for (NodeReader nodeReader: nffgReader.getNodes()) {
			loadNode(nodeReader);
		}
		
		
	}

	@Override
	public boolean testReachability(String srcName, String destName)
			throws UnknownNameException, ServiceException, NoGraphException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCurrentGraphName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* Delete all the eventual nodes on the Neo4JXML DB */
	private void deleteNFFG() throws ServiceException {
		Client client = ClientBuilder.newClient();
		try{
			client.target(this.baseURL+"/resource/nodes").request("text/plain").get(String.class);
		}
		catch (RuntimeException e) {
			throw new ServiceException(e);	
		}
		
	}
	
	/* Load a node with all its links */
	private void loadNode(NodeReader nodeReader) throws ServiceException{
		/* load the new node resource*/
		//TODO: PROBLEM DETECTING THE RIGHT NODE CLASS => I WOULD LIKE TO USE THE GEN-SRC ONE
		Node node = NodeReaderToNode(nodeReader);
		/* load all the node links as relationships */
		
	}
	
	private Node NodeReaderToNode(NodeReader nodeReader){
		Node node = new Node();
		node.id = nodeReader.getName();
		//TODO: complete the translation
		return node;
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
			reachabilityTester.loadNFFG("Nffg0");
		} catch (UnknownNameException | ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
