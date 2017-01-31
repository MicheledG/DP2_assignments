package it.polito.dp2.NFFG.sol3.client2;

import it.polito.dp2.NFFG.NamedEntityReader;
import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Nffgs;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Policies;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.PropertyType;


public class PolicyReaderImpl implements PolicyReader, NamedEntityReader {
	
	Nffgs.Nffg nffg;
	Policies.Policy policy;
	
	public PolicyReaderImpl(Nffgs.Nffg nffg, Policies.Policy policy) {
		this.nffg = nffg;
		this.policy = policy;
	}
	
	@Override
	public String getName() {
		return policy.getName();
	}

	@Override
	public NffgReader getNffg() {
		return NffgReaderImpl.translateNffgTypeToNffgReader(nffg);
	}

	@Override
	public VerificationResultReader getResult() {
		if(policy.getVerificationResult() == null)
			return null;
		else 
			return VerificationResultReaderImpl.translatePolicyResultToVerificationResultReader(nffg, policy);
	}

	@Override
	public Boolean isPositive() {
		return policy.isPositive();
	}
	
	public static PolicyReader translatePolicyTypeToPolicyReader(Nffgs.Nffg nffg, Policies.Policy policy){
		
		PolicyReader policyReader;
		PropertyType policyProperty = PropertyType.valueOf(policy.getProperty());
		switch (policyProperty) {
		case REACHABILITY:
			policyReader = new ReachabilityPolicyReaderImpl(nffg, policy);
			break;
		case TRAVERSAL:
			policyReader = new TraversalPolicyReaderImpl(nffg, policy);
			break;
		default:
			policyReader = new PolicyReaderImpl(nffg, policy);
			break;
		}
		
		return policyReader;
	}
	
}
