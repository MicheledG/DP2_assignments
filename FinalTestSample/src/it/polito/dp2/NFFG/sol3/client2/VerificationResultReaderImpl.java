package it.polito.dp2.NFFG.sol3.client2;

import java.util.Calendar;

import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Nffgs;
import it.polito.dp2.NFFG.sol3.client2.nffgservice.Policies;

public class VerificationResultReaderImpl implements VerificationResultReader {
	
	Nffgs.Nffg nffg;
	Policies.Policy policy;
	
	public VerificationResultReaderImpl(Nffgs.Nffg nffg, Policies.Policy policy) {
		this.nffg = nffg;
		this.policy = policy;
	}
	
	@Override
	public PolicyReader getPolicy() {
		return PolicyReaderImpl.translatePolicyTypeToPolicyReader(nffg, policy);
	}

	@Override
	public Boolean getVerificationResult() {
		return policy.getVerificationResult().isSatisfied();
	}

	@Override
	public String getVerificationResultMsg() {
		return policy.getVerificationResult().getDescription();
	}

	@Override
	public Calendar getVerificationTime() {
		return NffgReaderImpl.translateXMLGregorianCalendarToCalendar(policy.getVerificationResult().getLastVerification());
	}
	
	
	public static VerificationResultReader translatePolicyResultToVerificationResultReader(Nffgs.Nffg nffg, Policies.Policy policy){
		return new VerificationResultReaderImpl(nffg, policy);
	}
}
