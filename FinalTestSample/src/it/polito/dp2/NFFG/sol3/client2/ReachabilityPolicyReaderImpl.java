package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NodeReader;
import it.polito.dp2.NFFG.ReachabilityPolicyReader;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Nffgs;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Policies;

public class ReachabilityPolicyReaderImpl extends PolicyReaderImpl implements ReachabilityPolicyReader, NamedEntityReader {

	public ReachabilityPolicyReaderImpl(Nffgs.Nffg nffg, Policies.Policy policy) {
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
