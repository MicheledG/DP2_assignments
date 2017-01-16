package it.polito.dp2.NFFG.sol3.client1;

import java.net.URI;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.NffgVerifierFactory;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.ReachabilityPolicyReader;
import it.polito.dp2.NFFG.TraversalPolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.lab3.AlreadyLoadedException;
import it.polito.dp2.NFFG.lab3.NFFGClient;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;
import it.polito.dp2.NFFG.sol3.client1.nffgservice.LinkType;
import it.polito.dp2.NFFG.sol3.client1.nffgservice.NamedEntities;
import it.polito.dp2.NFFG.sol3.client1.nffgservice.NetworkFunctionalityType;
import it.polito.dp2.NFFG.sol3.client1.nffgservice.Nffgs;
import it.polito.dp2.NFFG.sol3.client1.nffgservice.NodeType;
import it.polito.dp2.NFFG.sol3.client1.nffgservice.Policies;
import it.polito.dp2.NFFG.sol3.client1.nffgservice.PropertyType;
import it.polito.dp2.NFFG.sol3.client1.nffgservice.VerificationResultType;

public class NFFGClientImpl implements NFFGClient {
	
	private static final String UNKNOWN_NAME_EXCEPTION_MSG = "locally missing entity named: ";
	private static final String SERVICE_EXCEPTION_MSG = "Error of NffgService";
	
	private URI baseURL;
	private NffgVerifier nffgVerifier;
	
	/* load on NffgService a set of nffgs grouped in the element Nffgs */
	private void postNffgs(Nffgs nffgs) throws AlreadyLoadedException, ServiceException{
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(this.baseURL+"/nffgs").request(MediaType.APPLICATION_XML).post(Entity.xml(nffgs));
		
		switch (response.getStatus()) {
		case 204:
			/* no content, everything went fine */
			break;
		case 403:
			/* forbidden, already loaded nffg */
			String errorDescription = response.readEntity(String.class);
			throw new AlreadyLoadedException(errorDescription);
		case 500:
			/* internal server error */
		default:
			throw new ServiceException(NFFGClientImpl.SERVICE_EXCEPTION_MSG); 
		}
	}
	
	private void putPolicies(Policies policies) throws ServiceException{
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(this.baseURL+"/policies").request(MediaType.APPLICATION_XML).put(Entity.xml(policies));
		
		switch (response.getStatus()) {
		case 204:
			/* no content, everything went fine */
			break;
		case 403:
			/* forbidden, already loaded nffg */
			String errorDescription = response.readEntity(String.class);
			throw new ServiceException(errorDescription);
		case 500:
			/* internal server error */
		default:
			throw new ServiceException(NFFGClientImpl.SERVICE_EXCEPTION_MSG); 
		}
	}
	
	private void deletePolicy(String policyName) throws UnknownNameException, ServiceException{
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(this.baseURL+"/policies/"+policyName).request().delete();
		
		switch (response.getStatus()) {
		case 204:
			/* no content, everything went fine */
			break;
		case 404:
			/* policy not found */
			String errorDescription = response.readEntity(String.class);
			throw new UnknownNameException(errorDescription);
		case 500:
			/* internal server error */
		default:
			throw new ServiceException(NFFGClientImpl.SERVICE_EXCEPTION_MSG); 
		}
	}
	
	private Policies verifyPolicy(NamedEntities policyName) throws UnknownNameException, ServiceException{
		
		Client client = ClientBuilder.newClient();
		Response response = client.target(this.baseURL+"/policies/verifier").request(MediaType.APPLICATION_XML).put(Entity.xml(policyName));
		Policies policiesVerified;
		
		switch (response.getStatus()) {
		case 200:
			policiesVerified = response.readEntity(Policies.class);
			break;
		case 403:
			/* policy not found */
			String errorDescription = response.readEntity(String.class);
			throw new UnknownNameException(errorDescription);
		case 500:
			/* internal server error */
		default:
			throw new ServiceException(NFFGClientImpl.SERVICE_EXCEPTION_MSG); 
		}
		
		return policiesVerified;
	}
	
	private Nffgs.Nffg translateNffgReaderToNffg(NffgReader nffg) {
		
		Nffgs.Nffg nffgTranslated = new Nffgs.Nffg();
		nffgTranslated.setName(nffg.getName());
		XMLGregorianCalendar lastUpdate = this.translateCalendarToXMLGregorianCalendar(nffg.getUpdateTime());
		nffgTranslated.setLastUpdate(lastUpdate);
		
		Nffgs.Nffg.Nodes nodesTranslated = new Nffgs.Nffg.Nodes();
		Nffgs.Nffg.Links linksTranslated = new Nffgs.Nffg.Links();
		
		/* extract all the nodes */
		for (NodeReader node : nffg.getNodes()) {
			NodeType nodeTranslated = this.translateNodeReaderToNode(node);
			nodesTranslated.getNode().add(nodeTranslated);
			
			/* for each node extract all the output links */
			for (LinkReader link : node.getLinks()) {
				LinkType linkTranslated = this.translateLinkReaderToLink(link);
				linksTranslated.getLink().add(linkTranslated);
			}
		}
		
		nffgTranslated.setNodes(nodesTranslated);
		nffgTranslated.setLinks(linksTranslated);
		
		return nffgTranslated;
	}

	private LinkType translateLinkReaderToLink(LinkReader link) {
		
		LinkType linkTranslated = new LinkType();
		linkTranslated.setName(link.getName());
		linkTranslated.setSourceNode(link.getSourceNode().getName());
		linkTranslated.setDestinationNode(link.getDestinationNode().getName());
		
		return linkTranslated;
	}

	private NodeType translateNodeReaderToNode(NodeReader node) {
		
		NodeType nodeTranslated = new NodeType();
		nodeTranslated.setName(node.getName());
		NetworkFunctionalityType networkFunctionalityType = NetworkFunctionalityType.valueOf(node.getFuncType().toString());
		nodeTranslated.setNetworkFunctionality(networkFunctionalityType.toString());
		
		return nodeTranslated;
	}
	
	private Policies.Policy translatePolicyReaderToPolicy(PolicyReader policyReader) {

		Policies.Policy policyTranslated = new Policies.Policy();
		
		ReachabilityPolicyReader reachPolicyReader = (ReachabilityPolicyReader) policyReader;
		/* general part of the policy contained both into reachability both into traversal policy */
		policyTranslated.setName(reachPolicyReader.getName());
		policyTranslated.setNffg(reachPolicyReader.getNffg().getName());
		policyTranslated.setPositive(reachPolicyReader.isPositive());
		
		VerificationResultReader result = reachPolicyReader.getResult();
		if(result != null){
			/* we have a result in the policyTranslated */
			VerificationResultType verificationResult = new VerificationResultType();
			verificationResult.setSatisfied(result.getVerificationResult());
			verificationResult.setLastVerification(translateCalendarToXMLGregorianCalendar(result.getVerificationTime()));
			verificationResult.setDescription(result.getVerificationResultMsg());
			policyTranslated.setVerificationResult(verificationResult);
		}
		
		policyTranslated.setSourceNode(reachPolicyReader.getSourceNode().getName());
		policyTranslated.setDestinationNode(reachPolicyReader.getDestinationNode().getName());
		
		/* more specific part depending on the instanceof policyTranslated reader */
		if(reachPolicyReader instanceof TraversalPolicyReader){
			policyTranslated.setProperty(PropertyType.TRAVERSAL.toString());
			TraversalPolicyReader traversalPolicyReader = (TraversalPolicyReader) reachPolicyReader;
			for (FunctionalType functionalType: traversalPolicyReader.getTraversedFuctionalTypes()) {
				policyTranslated.getNetworkFunctionality().add(functionalType.toString());
			}
		}
		else
			policyTranslated.setProperty(PropertyType.REACHABILITY.toString());
		
		return policyTranslated;
	}
	
	private XMLGregorianCalendar translateCalendarToXMLGregorianCalendar(Calendar updateTime) {
		
		GregorianCalendar lastUpdateGregorian = new GregorianCalendar();
		lastUpdateGregorian.setTime(updateTime.getTime());
		lastUpdateGregorian.setTimeZone(updateTime.getTimeZone());
		
		return new XMLGregorianCalendarImpl(lastUpdateGregorian);
	}
	
	
	public NFFGClientImpl() throws NffgVerifierException {
		/* check base url from the system property */
		String baseURL = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
		if(baseURL == null)
			this.baseURL = URI.create("http://localhost:8080/NffgService/rest");
		else
			this.baseURL = URI.create(baseURL);
		/* create the source data from the random generator */
		it.polito.dp2.NFFG.NffgVerifierFactory nffgVerifierFactory = NffgVerifierFactory.newInstance();
		nffgVerifier = nffgVerifierFactory.newNffgVerifier();
	}
	
	@Override
	public void loadNFFG(String name) throws UnknownNameException, AlreadyLoadedException, ServiceException {
		
		NffgReader nffg = this.nffgVerifier.getNffg(name);
		if(nffg == null)
			/* nffg not found */
			throw new UnknownNameException(NFFGClientImpl.UNKNOWN_NAME_EXCEPTION_MSG + name);
		
		Nffgs.Nffg nffgToLoad = this.translateNffgReaderToNffg(nffg);
		Nffgs nffgsToLoad = new Nffgs();
		nffgsToLoad.getNffg().add(nffgToLoad);
		
		this.postNffgs(nffgsToLoad);
		
		return;
	}

	

	@Override
	public void loadAll() throws AlreadyLoadedException, ServiceException {
		
		/* load all the nffgs */
		Set<NffgReader> nffgReaders = this.nffgVerifier.getNffgs();
		Nffgs nffgsToLoad = new Nffgs();
		List<Nffgs.Nffg> listOfNffgsToLoad = nffgsToLoad.getNffg();
		for (NffgReader nffgReader: nffgReaders) {
			Nffgs.Nffg nffgToLoad = this.translateNffgReaderToNffg(nffgReader);
			listOfNffgsToLoad.add(nffgToLoad);
		}
		this.postNffgs(nffgsToLoad);
		
		/* load all the policies */
		Set<PolicyReader> policyReaders = this.nffgVerifier.getPolicies();
		Policies policiesToLoad = new Policies();
		List<Policies.Policy> listOfPoliciesToLoad = policiesToLoad.getPolicy();
		for (PolicyReader policyReader: policyReaders) {
			Policies.Policy policyToLoad = this.translatePolicyReaderToPolicy(policyReader);
			listOfPoliciesToLoad.add(policyToLoad);
		}
		this.putPolicies(policiesToLoad);
		
	}

	@Override
	public void loadReachabilityPolicy(String name, String nffgName, boolean isPositive, String srcNodeName,
			String dstNodeName) throws UnknownNameException, ServiceException {
		
		/* check the corectness of the poliy data */
		NffgReader nffg = this.nffgVerifier.getNffg(nffgName);
		if(nffg == null)
			throw new UnknownNameException(NFFGClientImpl.UNKNOWN_NAME_EXCEPTION_MSG + nffgName);
		NodeReader srcNode = nffg.getNode(srcNodeName);
		if(srcNode == null)
			throw new UnknownNameException(NFFGClientImpl.UNKNOWN_NAME_EXCEPTION_MSG + srcNodeName);
		NodeReader dstNode = nffg.getNode(dstNodeName);
		if(dstNode == null)
			throw new UnknownNameException(NFFGClientImpl.UNKNOWN_NAME_EXCEPTION_MSG + dstNodeName);
		
		/* create the policy to load */
		Policies.Policy policyToLoad = new Policies.Policy();
		policyToLoad.setName(name);
		policyToLoad.setNffg(nffgName);
		policyToLoad.setProperty(PropertyType.REACHABILITY.toString());
		policyToLoad.setPositive(isPositive);
		policyToLoad.setSourceNode(srcNodeName);
		policyToLoad.setDestinationNode(dstNodeName);
		
		/* create the Policies envelope */
		Policies policiesToLoad = new Policies();
		policiesToLoad.getPolicy().add(policyToLoad);
		
		/* load the policy to the NffgService */
		this.putPolicies(policiesToLoad);
		
		return;

	}

	@Override
	public void unloadReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		this.deletePolicy(name);
		return;
	}

	@Override
	public boolean testReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		
		/* create the named entities with the name of the policy to verify */
		NamedEntities policyName = new NamedEntities();
		policyName.getName().add(name);
		
		/* set of policies with only one policy */
		Policies policyVerified = this.verifyPolicy(policyName);
		
		return policyVerified.getPolicy().get(0).getVerificationResult().isSatisfied();
	}

}
