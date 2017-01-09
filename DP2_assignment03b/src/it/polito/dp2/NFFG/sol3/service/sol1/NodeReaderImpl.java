package it.polito.dp2.NFFG.sol3.service.sol1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol3.service.sol1.jaxb.LinkType;
import it.polito.dp2.NFFG.sol3.service.sol1.jaxb.NetworkFunctionalityType;
import it.polito.dp2.NFFG.sol3.service.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol3.service.sol1.jaxb.NodeType;

public class NodeReaderImpl implements NodeReader, NamedEntityReader {
	
	NffgType nffg;
	NodeType node;
	
	public NodeReaderImpl(NffgType nffg, NodeType node){
		this.nffg = nffg;
		this.node = node;
	}
	
	@Override
	public String getName() {
		/* return the name of an object NodeType */
		return node.getName();
	}

	@Override
	public FunctionalType getFuncType() {
		/* return the functional type of an object NodeType*/
		return translateNetworkFunctionalityTypeToFunctionalType(node.getNetworkFunctionality());
	}

	@Override
	public Set<LinkReader> getLinks() {
		
		Set<LinkReader> links = new HashSet<LinkReader>();
		for (LinkType link : getOutgoingNodeLinks(nffg.getLinks().getLink())) {
			links.add(LinkReaderImpl.translateLinkTypeToLinkReader(nffg, link));
		}
		return links;
	}
	
	/*filter among all the nffg links only the outgoing links from the node*/
	private List<LinkType> getOutgoingNodeLinks(List<LinkType> nffgLinks){
		List<LinkType> nodeOutgoingLinks = new ArrayList<LinkType>();
		for(LinkType link: nffgLinks){
			if(link.getSourceNode().equals(this.node.getName()))
				nodeOutgoingLinks.add(link);
				
		}
		return nodeOutgoingLinks;
	}
	
	
	public static NodeReader translateNodeTypeToNodeReader(NffgType nffg, NodeType node){
		return new NodeReaderImpl(nffg, node);
	}
	
	/*translate FunctionalType to NetworkFunctionalityType*/
	public static FunctionalType translateNetworkFunctionalityTypeToFunctionalType(NetworkFunctionalityType networkFunctionalityType){
		FunctionalType functionalType;
		
		switch (networkFunctionalityType) {
		case CACHE:
			functionalType = FunctionalType.CACHE;
			break;
		case DPI:
			functionalType = FunctionalType.DPI;
			break;
		case FW:
			functionalType = FunctionalType.FW;
			break;
		case MAIL_CLIENT:
			functionalType = FunctionalType.MAIL_CLIENT;
			break;
		case MAIL_SERVER:
			functionalType = FunctionalType.MAIL_SERVER;
			break;
		case NAT:
			functionalType = FunctionalType.NAT;
			break;
		case SPAM:
			functionalType = FunctionalType.SPAM;
			break;
		case VPN:
			functionalType = FunctionalType.VPN;
			break;
		case WEB_CLIENT:
			functionalType = FunctionalType.WEB_CLIENT;
			break;
		case WEB_SERVER:
			functionalType = FunctionalType.WEB_SERVER;
			break;
		default:
			functionalType = FunctionalType.WEB_SERVER;
			break;
		}
		
		return functionalType;
	}

}
