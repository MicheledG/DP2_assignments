package it.polito.dp2.NFFG.sol3.client1;

import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.NffgVerifierFactory;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.lab3.AlreadyLoadedException;
import it.polito.dp2.NFFG.lab3.NFFGClient;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;
import it.polito.dp2.NFFG.sol3.service.jaxb.LinkType;
import it.polito.dp2.NFFG.sol3.service.jaxb.NetworkFunctionalityType;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs.Nffg;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs.Nffg.Links;
import it.polito.dp2.NFFG.sol3.service.jaxb.NodeType;

public class NFFGClientImpl implements NFFGClient {
	
	private static final String UNKNOWN_NAME_EXCEPTION_MSG = "";
	private static final String ALREADY_LOADED_EXCEPTION_MSG = "";
	private static final String SERVICE_EXCEPTION_MSG = "";
	
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
		nodeTranslated.setNetworkFunctionality(networkFunctionalityType);
		
		return nodeTranslated;
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
		
		Set<NffgReader> nffgReaders = this.nffgVerifier.getNffgs();
		List<Nffgs.Nffg> nffgsToLoad = new ArrayList<Nffgs.Nffg>();
		for (NffgReader nffgReader: nffgReaders) {
			Nffgs.Nffg nffgToLoad = this.translateNffgReaderToNffg(nffgReader);
			nffgsToLoad.add(nffgToLoad);
		}
		Nffgs nffgs = new 

	}

	@Override
	public void loadReachabilityPolicy(String name, String nffgName, boolean isPositive, String srcNodeName,
			String dstNodeName) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unloadReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean testReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		return false;
	}

}
