package it.polito.dp2.NFFG.sol3.client2;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.TraversalPolicyReader;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Nffgs;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Policies;


public class TraversalPolicyReaderImpl extends ReachabilityPolicyReaderImpl implements TraversalPolicyReader, NamedEntityReader {

	public TraversalPolicyReaderImpl(Nffgs.Nffg nffg, Policies.Policy policy) {
		super(nffg, policy);
	}

	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		
		Set<FunctionalType> functionalTypes = new HashSet<FunctionalType>();
		
		for (String networkFunctionalityType : policy.getNetworkFunctionality()) {
			functionalTypes.add(NodeReaderImpl.translateNetworkFunctionalityTypeToFunctionalType(networkFunctionalityType));
		}
		
		return functionalTypes;
	}

}
