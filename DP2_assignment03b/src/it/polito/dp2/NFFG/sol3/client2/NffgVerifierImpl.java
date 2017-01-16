package it.polito.dp2.NFFG.sol3.client2;

import java.net.URI;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import it.polito.dp2.NFFG.NffgReader;
import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.PolicyReader;

import it.polito.dp2.NFFG.sol3.client2.nffgservice.*;

public class NffgVerifierImpl implements NffgVerifier {
	
	
	private URI baseURL;
	private Nffgs nffgs;
	private Policies policies;
	
	public NffgVerifierImpl() throws NffgVerifierException {
		
		/* check base url from the system property */
		String baseURL = System.getProperty("it.polito.dp2.NFFG.lab3.URL");
		if(baseURL == null)
			this.baseURL = URI.create("http://localhost:8080/NffgService/rest");
		else
			this.baseURL = URI.create(baseURL);
		
		try{
			/* get all nffgs loaded on NffgService */
			this.nffgs = this.getNffgsFromNffgService();
			/* get all policies loaded on NffgService */
			this.policies = this.getPoliciesFromNffgService(); 
		} catch(Exception e){
			throw new NffgVerifierException(e);
		}
	}
	
	private Nffgs getNffgsFromNffgService() {
		Client client = ClientBuilder.newClient();
		Nffgs nffgs = client.target(this.baseURL+"/nffgs").request(MediaType.APPLICATION_XML).get(Nffgs.class);
		if(nffgs == null)
			nffgs = new Nffgs();
		return nffgs;
	}
	
	private Policies getPoliciesFromNffgService() {
		Client client = ClientBuilder.newClient();
		Policies policies = client.target(this.baseURL+"/policies").request(MediaType.APPLICATION_XML).get(Policies.class);
		if(policies == null)
			policies = new Policies();
		return policies;
	}
	
	private Nffgs.Nffg getNffgFromNffgs(String nffgName) {
		for (Nffgs.Nffg nffg : this.nffgs.getNffg()) {
			if(nffg.getName().equals(nffgName))
				return nffg;
		}
		return null;
	}
	
	@Override
	public NffgReader getNffg(String arg0) {
		
		NffgReader nffgReader = null;
		for (Nffgs.Nffg nffg: this.nffgs.getNffg()) {
			if(nffg.getName().equals(arg0)){
				nffgReader = NffgReaderImpl.translateNffgTypeToNffgReader(nffg);
				break;
			}
		}
		
		return nffgReader;
	}

	@Override
	public Set<NffgReader> getNffgs() {
		
		Set<NffgReader> nffgReaders = new HashSet<NffgReader>();
		for (Nffgs.Nffg nffg: this.nffgs.getNffg()) {
			nffgReaders.add(NffgReaderImpl.translateNffgTypeToNffgReader(nffg));
		}
		
		return nffgReaders;
	}

	@Override
	public Set<PolicyReader> getPolicies() {
		
		
		Set<PolicyReader> policyReaders = new HashSet<PolicyReader>();
		for (Policies.Policy policy: this.policies.getPolicy()) {
			Nffgs.Nffg policyNffg = this.getNffgFromNffgs(policy.getNffg());
			policyReaders.add(PolicyReaderImpl.translatePolicyTypeToPolicyReader(policyNffg, policy));
		}
		
		return policyReaders;
	
	}

	@Override
	public Set<PolicyReader> getPolicies(String arg0) {
		
		Nffgs.Nffg nffg = this.getNffgFromNffgs(arg0);
		Set<PolicyReader> policyReaders = new HashSet<PolicyReader>();
				
		for (Policies.Policy policy : this.policies.getPolicy()) {
			if(policy.getNffg().equals(arg0)){
				PolicyReader policyReader = PolicyReaderImpl.translatePolicyTypeToPolicyReader(nffg, policy);
				policyReaders.add(policyReader);
			}
		}
		
		return policyReaders;
	}

	@Override
	public Set<PolicyReader> getPolicies(Calendar arg0) {
		
		Set<PolicyReader> policyReaders = new HashSet<PolicyReader>();
				
		for (Policies.Policy policy : this.policies.getPolicy()) {
			VerificationResultType verificationResult = policy.getVerificationResult();
			if(verificationResult != null){
				Calendar lastVerification = NffgReaderImpl.translateXMLGregorianCalendarToCalendar(verificationResult.getLastVerification());
				if(lastVerification.after(arg0)){
					Nffgs.Nffg nffg = this.getNffgFromNffgs(policy.getNffg());
					PolicyReader policyReader = PolicyReaderImpl.translatePolicyTypeToPolicyReader(nffg, policy);
					policyReaders.add(policyReader);
				}
			}
		}
	
		return policyReaders;	
	}

}
