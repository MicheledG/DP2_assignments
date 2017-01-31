package it.polito.dp2.NFFG.sol3.client2;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Nffgs;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.NodeType;

public class NffgReaderImpl implements NffgReader, NamedEntityReader {
	
	Nffgs.Nffg nffg;
	
	public NffgReaderImpl(Nffgs.Nffg nffg) {
		this.nffg = nffg;
	}
	
	@Override
	public String getName() {
		return nffg.getName();
	}

	@Override
	public NodeReader getNode(String arg0) {
		
		NodeReader nodeReader = null;
		for (NodeType node : nffg.getNodes().getNode()) {
			if(node.getName().equals(arg0)){
				nodeReader = NodeReaderImpl.translateNodeTypeToNodeReader(nffg, node);
			}
		}
		
		return nodeReader;
	}

	@Override
	public Set<NodeReader> getNodes() {
		
		Set<NodeReader> nodeReaders = new HashSet<NodeReader>();
		for (NodeType node : nffg.getNodes().getNode()) {
			nodeReaders.add(NodeReaderImpl.translateNodeTypeToNodeReader(nffg, node));
		}
		
		return nodeReaders;
	}

	@Override
	public Calendar getUpdateTime() {
		
		return translateXMLGregorianCalendarToCalendar(nffg.getLastUpdate());
	}
	
	public static NffgReader translateNffgTypeToNffgReader(Nffgs.Nffg nffg){
		return new NffgReaderImpl(nffg);
	}
	
	public static Calendar translateXMLGregorianCalendarToCalendar(XMLGregorianCalendar xmlGregorianCalendar){
		
		return xmlGregorianCalendar.toGregorianCalendar();
	}

}
