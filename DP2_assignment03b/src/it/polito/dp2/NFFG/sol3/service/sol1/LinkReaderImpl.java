package it.polito.dp2.NFFG.sol3.service.sol1;

import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol3.service.sol1.jaxb.LinkType;
import it.polito.dp2.NFFG.sol3.service.sol1.jaxb.NffgType;

public class LinkReaderImpl implements LinkReader,NamedEntityReader {

	NffgType nffg;
	LinkType link;
	
	public LinkReaderImpl(NffgType nffg, LinkType link){
		this.nffg = nffg;
		this.link = link;
	}
	
	@Override
	public String getName() {
		return link.getName();
	}

	@Override
	public NodeReader getDestinationNode() {
		NffgReader nffgReader = NffgReaderImpl.translateNffgTypeToNffgReader(nffg);
		return nffgReader.getNode(link.getDestinationNode());
	}

	@Override
	public NodeReader getSourceNode() {
		NffgReader nffgReader = NffgReaderImpl.translateNffgTypeToNffgReader(nffg);
		return nffgReader.getNode(link.getSourceNode());
	}
	
	public static LinkReader translateLinkTypeToLinkReader(NffgType nffg, LinkType linkType){
		return new LinkReaderImpl(nffg, linkType);
	}
	
}
