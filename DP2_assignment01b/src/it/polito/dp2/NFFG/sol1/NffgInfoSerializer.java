package it.polito.dp2.NFFG.sol1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;


public class NffgInfoSerializer {
	private NffgVerifier monitor;
	private DateFormat dateFormat;
	//create the data structure where store the information of the random NFFG
	private NffgInfoWrapper nffgInfoWrapperToMarshall;

	
	/**
	 * Default constructror
	 * @throws NffgVerifierException 
	 */
	public NffgInfoSerializer() throws NffgVerifierException {
		NffgVerifierFactory factory = NffgVerifierFactory.newInstance();
		monitor = factory.newNffgVerifier();
		dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		nffgInfoWrapperToMarshall = new NffgInfoWrapper();
	}
	
	public NffgInfoSerializer(NffgVerifier monitor) {
		super();
		this.monitor = monitor;
		dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
		nffgInfoWrapperToMarshall = new NffgInfoWrapper();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		NffgInfoSerializer wf;
		try {
			wf = new NffgInfoSerializer();
			wf.getAllRandomData();
			wf.marshallAll();
		} catch (NffgVerifierException e) {
			System.err.println("Could not instantiate data generator.");
			e.printStackTrace();
			System.exit(1);
		}
	}


	public void getAllRandomData() {
		nffgInfoWrapperToMarshall.setCatalog(getCatalog()); 
		nffgInfoWrapperToMarshall.setNffgInfos(getNffgInfos());
	}

	/* populate an object of class CatalogType with all the possible network functionality*/
	private CatalogType getCatalog(){
		CatalogType catalog = new CatalogType();
		for (NetworkFunctionalityType networkFunctionality : NetworkFunctionalityType.values()) {
			catalog.getNetworkFunctionality().add(networkFunctionality);
		}
		
		return catalog;
	}
	
	/* return an NffgInfos object gathering all the NffgInfo created from the random xml document */
	private NffgInfoWrapper.NffgInfos getNffgInfos(){
		// Get the list of policies
		Set<PolicyReader> policies = monitor.getPolicies();
		
		// Get the list of nffgs
		Set<NffgReader> nffgs = monitor.getNffgs();
		
		NffgInfoWrapper.NffgInfos nffgInfos = new NffgInfoWrapper.NffgInfos();
		for (NffgReader nffg : nffgs) {
			nffgInfos.getNffgInfo().add(getNffgInfo(nffg, policies));
		}
		
		return nffgInfos;
	}
	
	/* create the NffgInfoType object related to an nffg */
	private NffgInfoType getNffgInfo(NffgReader nffgReader, Set<PolicyReader> policies){
		 
		
		NffgInfoType nffgInfo = new NffgInfoType();
		
		nffgInfo.setNffg(getNffg(nffgReader));
		nffgInfo.setPolicies(getPolicies(nffgReader, policies));
		
		return nffgInfo;
	}
	
	/* create an NffgType object from an NffgReader object */
	private NffgType getNffg(NffgReader nffgReader){
		
		/* translate nffgReader into my nffgType */
		NffgType nffg = new NffgType();
		
		nffg.setName(nffgReader.getName());
		nffg.setLastUpdate(translateCalendarToXMLGregorianCalendar(nffgReader.getUpdateTime()));
		nffg.setNodes(getNodesOfNffg(nffgReader));
		nffg.setLinks(getLinksOfNffg(nffgReader));
		
		return nffg;
	}
	
	/* create a Nodes object from NffgReader's objects node */
	private NffgType.Nodes getNodesOfNffg(NffgReader nffgReader){
		
		NffgType.Nodes nodes = new NffgType.Nodes();
		
		for (NodeReader nodeReader: nffgReader.getNodes()) {
			it.polito.dp2.NFFG.sol1.jaxb.NodeType node = new it.polito.dp2.NFFG.sol1.jaxb.NodeType();
			node.setNetworkFunctionality(translateFunctionalityToNetworkFunctionality(nodeReader.getFuncType()));
			node.setName(nodeReader.getName());
			nodes.getNode().add(node);
		}
		
		
		return nodes;
	}
	
	/* create a Links object from NffgReader's objects link */
	private NffgType.Links getLinksOfNffg(NffgReader nffgReader){
		
		NffgType.Links links = new NffgType.Links();
		
		for (NodeReader nodeReader: nffgReader.getNodes()) {
			for (LinkReader linkReader: nodeReader.getLinks()) {
				it.polito.dp2.NFFG.sol1.jaxb.LinkType link = new it.polito.dp2.NFFG.sol1.jaxb.LinkType();
				link.setName(linkReader.getName());
				link.setSourceNode(linkReader.getSourceNode().getName());
				link.setDestinationNode(linkReader.getDestinationNode().getName());
				links.getLink().add(link);
			}
		}
		
		return links;
	}
	
	/* create a Policies object containing all the policies of a Nffg*/
	private NffgInfoType.Policies getPolicies(NffgReader nffgReader, Set<PolicyReader> policyReaders){
		
		/* populate object Policies only with the policies related to the nffg of nffgReader */
		NffgInfoType.Policies policies = new NffgInfoType.Policies();
		
		for (PolicyReader policyReader : policyReaders) {
			if(policyReader.getNffg().getName().equals(nffgReader.getName())){
				/* the policy of this policyReader is related to the nffg of this nffgReader */
				ReachabilityPolicyReader reachPolicyReader = (ReachabilityPolicyReader) policyReader;
				PolicyType policy = new PolicyType();
				
				/* general part of the policy contained both into reachability both into traversal policy */
				policy.setName(reachPolicyReader.getName());
				policy.setNffg(reachPolicyReader.getNffg().getName());
				policy.setPositive(reachPolicyReader.isPositive());
				policy.setResult(reachPolicyReader.getResult().getVerificationResult());
				policy.setLastVerification(translateCalendarToXMLGregorianCalendar(policyReader.getResult().getVerificationTime()));
				policy.setSourceNode(reachPolicyReader.getSourceNode().getName());
				policy.setDestinationNode(reachPolicyReader.getDestinationNode().getName());
				
				/* more specific part depending on the instanceof policy reader */
				if(reachPolicyReader instanceof TraversalPolicyReader){
					policy.setProperty(PropertyType.TRAVERSAL);
					TraversalPolicyReader traversalPolicyReader = (TraversalPolicyReader) reachPolicyReader;
					for (FunctionalType functionalType: traversalPolicyReader.getTraversedFuctionalTypes()) {
						policy.getNetworkFunctionality().add(translateFunctionalityToNetworkFunctionality(functionalType));
					}
				}
				else
					policy.setProperty(PropertyType.REACHABILITY);
				
					
				policies.getPolicy().add(policy);
			}
		}
		
		return policies;
	}
	
	/*translate FunctionalType to NetworkFunctionalityType*/
	private NetworkFunctionalityType translateFunctionalityToNetworkFunctionality(FunctionalType functionalType){
		NetworkFunctionalityType networkFunctionalityType = NetworkFunctionalityType.WEB_SERVER; //default value
		
		switch (functionalType) {
		case CACHE:
			networkFunctionalityType = NetworkFunctionalityType.CACHE;
			break;
		case DPI:
			networkFunctionalityType = NetworkFunctionalityType.DPI;
			break;
		case FW:
			networkFunctionalityType = NetworkFunctionalityType.FW;
			break;
		case MAIL_CLIENT:
			networkFunctionalityType = NetworkFunctionalityType.MAIL_CLIENT;
			break;
		case MAIL_SERVER:
			networkFunctionalityType = NetworkFunctionalityType.MAIL_SERVER;
			break;
		case NAT:
			networkFunctionalityType = NetworkFunctionalityType.NAT;
			break;
		case SPAM:
			networkFunctionalityType = NetworkFunctionalityType.SPAM;
			break;
		case VPN:
			networkFunctionalityType = NetworkFunctionalityType.VPN;
			break;
		case WEB_CLIENT:
			networkFunctionalityType = NetworkFunctionalityType.WEB_CLIENT;
			break;
		case WEB_SERVER:
			networkFunctionalityType = NetworkFunctionalityType.WEB_SERVER;
			break;
		}
		
		return networkFunctionalityType;
	}
	
	private XMLGregorianCalendar translateCalendarToXMLGregorianCalendar(Calendar calendar){
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(calendar.getTime());
		gregorianCalendar.setTimeZone(calendar.getTimeZone());
		XMLGregorianCalendar xmlGregorianCalendar = new XMLGregorianCalendarImpl(gregorianCalendar);
		return xmlGregorianCalendar;
	}
	
	
	private void marshallAll(){
		//TODO: write something here!
	}
	
}

