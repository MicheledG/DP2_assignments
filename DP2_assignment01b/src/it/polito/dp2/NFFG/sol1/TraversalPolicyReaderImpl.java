package it.polito.dp2.NFFG.sol1;

import java.util.HashSet;
import java.util.Set;

import it.polito.dp2.NFFG.FunctionalType;
import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.TraversalPolicyReader;
import it.polito.dp2.NFFG.sol1.jaxb.NetworkFunctionalityType;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.PolicyType;
import it.polito.dp2.NFFG.sol1.NodeReaderImpl;

public class TraversalPolicyReaderImpl extends ReachabilityPolicyReaderImpl implements TraversalPolicyReader, NamedEntityReader {

	public TraversalPolicyReaderImpl(NffgType nffg, PolicyType policy) {
		super(nffg, policy);
	}

	@Override
	public Set<FunctionalType> getTraversedFuctionalTypes() {
		
		Set<FunctionalType> functionalTypes = new HashSet<FunctionalType>();
		
		for (NetworkFunctionalityType networkFunctionalityType : policy.getNetworkFunctionality()) {
			functionalTypes.add(NodeReaderImpl.translateNetworkFunctionalityTypeToFunctionalType(networkFunctionalityType));
		}
		
		return functionalTypes;
	}

}
