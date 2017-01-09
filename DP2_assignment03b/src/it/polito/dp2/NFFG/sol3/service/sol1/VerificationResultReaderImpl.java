package it.polito.dp2.NFFG.sol3.service.sol1;

import java.util.Calendar;

import it.polito.dp2.NFFG.PolicyReader;
import it.polito.dp2.NFFG.VerificationResultReader;
import it.polito.dp2.NFFG.sol3.service.sol1.jaxb.NffgType;
import it.polito.dp2.NFFG.sol3.service.sol1.jaxb.PolicyType;

public class VerificationResultReaderImpl implements VerificationResultReader {
	
	NffgType nffg;
	PolicyType policy;
	
	public VerificationResultReaderImpl(NffgType nffg, PolicyType policy) {
		this.nffg = nffg;
		this.policy = policy;
	}
	
	@Override
	public PolicyReader getPolicy() {
		return PolicyReaderImpl.translatePolicyTypeToPolicyReader(nffg, policy);
	}

	@Override
	public Boolean getVerificationResult() {
		return policy.isResult();
	}

	@Override
	public String getVerificationResultMsg() {
		return policy.getDescription();
	}

	@Override
	public Calendar getVerificationTime() {
		return NffgReaderImpl.translateXMLGregorianCalendarToCalendar(policy.getLastVerification());
	}
	
	
	public static VerificationResultReader translatePolicyResultToVerificationResultReader(NffgType nffg, PolicyType policy){
		return new VerificationResultReaderImpl(nffg, policy);
	}
}
