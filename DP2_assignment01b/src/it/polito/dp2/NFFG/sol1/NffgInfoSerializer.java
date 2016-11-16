package it.polito.dp2.NFFG.sol1;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeType;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.sol1.jaxb.*;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType.Nodes;


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
		/* TODO needs to translate the nffgReader update time with mine */
		nffg.setLastUpdate(nffgReader.getUpdateTime());
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
				//needs reflections!!! to understand which type of policy interface we are using
				PolicyType policy = new PolicyType();
				policy.setName(policyReader.getName());
				policy.setNffg(policyReader.getNffg().getName());
				policy.setProperty(value);
				policy.getNetworkFunctionality();
				policy.setPositive(policyReader.isPositive());
				policy.setSourceNode(value);
				policy.setDestinationNode(value);
				if(policyReader.getResult().getVerificationResult())
					policy.setResult(ResultType.SATISFIED);
				else
					policy.setResult(ResultType.VIOLATED);
				policy.setLastVerification(policyReader.getResult().getVerificationTime());
				
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
	
	
	private void marshallAll(){
		
	}
	
	private void printPolicies() {

		// Get the list of policies
		Set<PolicyReader> set = monitor.getPolicies();
		
		/* Print the header of the table */
		System.out.println("#");
		System.out.println("#Number of Policies: "+set.size());
		System.out.println("#");
		String header = new String("#List of policies:");
		printHeader(header);
		
		// For each policy print related data
		for (PolicyReader pr: set) {
			System.out.println("Policy name: " + pr.getName());
			System.out.println("Policy nffg name: " + pr.getNffg().getName());
			if(pr.isPositive())
				System.out.println("Policy is positive.");
			else
				System.out.println("Policy is negative.");
			printVerificationResult(pr.getResult());
			System.out.println("#");
		}
		System.out.println("#End of Policies");
		System.out.println("#");
	}


	private void printVerificationResult(VerificationResultReader result) {
		if (result == null) {
			System.out.println("No verification result for policy");
			return;
		}
		if (result.getVerificationResult())
			System.out.println("Policy result is true");
		else
			System.out.println("Policy result is false");
		System.out.println("Verification result message: " + result.getVerificationResultMsg());
		System.out.println("Verification time (in local time zone): " + dateFormat.format(result.getVerificationTime().getTime()));
	}

	private void printNffgs() {
		// Get the list of NFFGs
		Set<NffgReader> set = monitor.getNffgs();
		
		/* Print the header of the table */
		System.out.println("#");
		System.out.println("#Number of Nffgs: "+set.size());
		System.out.println("#");
		String header = new String("#List of NFFgs:");
		printHeader(header);	
		
		// For each NFFG print related data
		for (NffgReader nffg_r: set) {
			System.out.println();
			printHeader("Data for NFFG " + nffg_r.getName());
			System.out.println();
			// Print update time
			Calendar updateTime = nffg_r.getUpdateTime();
			printHeader("Update time: "+dateFormat.format(updateTime.getTime()));

			// Print nodes
			Set<NodeReader> nodeSet = nffg_r.getNodes();
			printHeader("Number of Nodes: "+nodeSet.size(),'%');
			for (NodeReader nr: nodeSet) {
				System.out.println("Node " + nr.getName() +"\tType: "+nr.getFuncType().toString()+"\tNumber of links: "+nr.getLinks().size());
				Set<LinkReader> linkSet = nr.getLinks();
				System.out.println("List of Links for node "+nr.getName());
				printHeader("Link name \tsource \tdestination");
				for (LinkReader lr: linkSet)
					System.out.println(lr.getName()+"\t"+lr.getSourceNode().getName()+"\t"+lr.getDestinationNode().getName());
				System.out.println(makeLine('%'));;
			}
			System.out.println("#");
		}	
		System.out.println("#End of Nodes");
		System.out.println("#");
	}

	private void printHeader(String header, char c) {		
		System.out.println(header);
		System.out.println(makeLine(c));	
	}

	private StringBuffer makeLine(char c) {
		StringBuffer line = new StringBuffer(132);
		
		for (int i = 0; i < 132; ++i) {
			line.append(c);
		}
		return line;
	}

	private void printHeader(String header) {
		printHeader(header,'-');
	}
}

