package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.LinkType;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Nffgs;

public class LinkReaderImpl implements LinkReader,NamedEntityReader {

	Nffgs.Nffg nffg;
	LinkType link;
	
	public LinkReaderImpl(Nffgs.Nffg nffg, LinkType link){
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
	
	public static LinkReader translateLinkTypeToLinkReader(Nffgs.Nffg nffg, LinkType linkType){
		return new LinkReaderImpl(nffg, linkType);
	}
	
}
