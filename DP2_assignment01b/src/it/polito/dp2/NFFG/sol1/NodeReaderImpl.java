package it.polito.dp2.NFFG.sol1;

import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.LinkReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.sol1.jaxb.NetworkFunctionalityType;
import it.polito.dp2.NFFG.sol1.jaxb.NodeType;

public class NodeReaderImpl implements NodeReader {
	
	NodeType node;
	
	public NodeReaderImpl(NodeType node){
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
		// TODO Auto-generated method stub
		return null;
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
