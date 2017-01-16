package it.polito.dp2.NFFG.sol3.client2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.LinkType;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Nffgs;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.NodeType;

public class NodeReaderImpl implements NodeReader, NamedEntityReader {
	
	Nffgs.Nffg nffg;
	NodeType node;
	
	public NodeReaderImpl(Nffgs.Nffg nffg, NodeType node){
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
	
	
	public static NodeReader translateNodeTypeToNodeReader(Nffgs.Nffg nffg, NodeType node){
		return new NodeReaderImpl(nffg, node);
	}
	
	/*translate FunctionalType to NetworkFunctionalityType*/
	public static FunctionalType translateNetworkFunctionalityTypeToFunctionalType(String networkFunctionalityType){
		FunctionalType functionalType = FunctionalType.valueOf(networkFunctionalityType.toString());		
		return functionalType;
	}

}
