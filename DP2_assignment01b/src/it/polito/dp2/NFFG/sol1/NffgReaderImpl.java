package it.polito.dp2.NFFG.sol1;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.NodeType;

public class NffgReaderImpl implements NffgReader {
	
	NffgType nffg;
	
	public NffgReaderImpl(NffgType nffg) {
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
	
	public static NffgReader translateNffgTypeToNffgReader(NffgType nffg){
		
		return new NffgReaderImpl(nffg);
	}
	
	public static Calendar translateXMLGregorianCalendarToCalendar(XMLGregorianCalendar xmlGregorianCalendar){
		
		return xmlGregorianCalendar.toGregorianCalendar();
	}

}
