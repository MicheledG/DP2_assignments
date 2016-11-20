package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.ReachabilityPolicyReader;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.PolicyType;

public class ReachabilityPolicyReaderImpl extends PolicyReaderImpl implements ReachabilityPolicyReader {

	public ReachabilityPolicyReaderImpl(NffgType nffg, PolicyType policy) {
		super(nffg, policy);
	}

	@Override
	public NodeReader getDestinationNode() {
		NffgReader nffgReader = NffgReaderImpl.translateNffgTypeToNffgReader(nffg);
		return nffgReader.getNode(policy.getDestinationNode());
	}

	@Override
	public NodeReader getSourceNode() {
		NffgReader nffgReader = NffgReaderImpl.translateNffgTypeToNffgReader(nffg);
		return nffgReader.getNode(policy.getSourceNode());
	}
	
}
