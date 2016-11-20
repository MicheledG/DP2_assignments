package it.polito.dp2.NFFG.sol1;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol1.jaxb.PolicyType;

public class PolicyReaderImpl implements PolicyReader {
	
	NffgType nffg;
	PolicyType policy;
	
	public PolicyReaderImpl(NffgType nffg, PolicyType policy) {
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
		return VerificationResultReaderImpl.translatePolicyResultToVerificationResultReader(nffg, policy);
	}

	@Override
	public Boolean isPositive() {
		return policy.isPositive();
	}
	
	public static PolicyReader translatePolicyTypeToPolicyReader(NffgType nffg, PolicyType policy){
		return new PolicyReaderImpl(nffg, policy);
	}
	
}
