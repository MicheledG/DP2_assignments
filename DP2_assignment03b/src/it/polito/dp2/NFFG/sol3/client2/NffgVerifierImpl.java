package it.polito.dp2.NFFG.sol3.client2;

import java.net.URI;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
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
			/* all possible exceptions are catched and rethrown in the only possible exception */ 
			throw new NffgVerifierException(e);
		}
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
	
	private Nffgs getNffgsFromNffgService() {
		
		Nffgs nffgs = new Nffgs();
		
		EntityPointers nffgPointers = this.getNffgPointersFromNffgService();
		if(nffgPointers==null)
			/* empty NffgService*/
			return nffgs;
		
		List<Nffgs.Nffg> nffgsList = nffgs.getNffg();
		for (EntityPointerType nffgPointer : nffgPointers.getEntityPointer()) {
			Nffgs.Nffg nffg = this.getNffgFromNffgService(nffgPointer.getPointer());
			if(nffg==null)
				continue;
			nffgsList.add(nffg);
		}
		
		return nffgs;
	}

	private EntityPointers getNffgPointersFromNffgService(){
		Client client = ClientBuilder.newClient();
		EntityPointers nffgPointers = client.target(this.baseURL+"/nffgs").request(MediaType.APPLICATION_XML).get(EntityPointers.class);
		return nffgPointers;
	}
	
	private Nffgs.Nffg getNffgFromNffgService(String nffgPointer) {
		
		Client client = ClientBuilder.newClient();
		Nffgs nffgs = client.target(nffgPointer).request(MediaType.APPLICATION_XML).get(Nffgs.class);
		Nffgs.Nffg nffg; 
		if(nffgs==null)
			nffg = null;
		else
			nffg = nffgs.getNffg().get(0);
		return nffg;
	}
	
	private Policies getPoliciesFromNffgService() {
		
		Policies policys = new Policies();
		
		EntityPointers policyPointers = this.getPolicyPointersFromNffgService();
		if(policyPointers==null)
			/* empty PolicyService*/
			return policys;
		
		List<Policies.Policy> policysList = policys.getPolicy();
		for (EntityPointerType policyPointer : policyPointers.getEntityPointer()) {
			Policies.Policy policy = this.getPolicyFromNffgService(policyPointer.getPointer());
			if(policy==null)
				continue;
			policysList.add(policy);
		}
		
		return policys;
	}

	private EntityPointers getPolicyPointersFromNffgService(){
		Client client = ClientBuilder.newClient();
		EntityPointers policyPointers = client.target(this.baseURL+"/policies").request(MediaType.APPLICATION_XML).get(EntityPointers.class);
		return policyPointers;
	}
	
	private Policies.Policy getPolicyFromNffgService(String policyPointer) {
		
		Client client = ClientBuilder.newClient();
		Policies policys = client.target(policyPointer).request(MediaType.APPLICATION_XML).get(Policies.class);
		Policies.Policy policy; 
		if(policys==null)
			policy = null;
		else
			policy = policys.getPolicy().get(0);
		return policy;
	}
	
	private Nffgs.Nffg getNffgFromNffgs(String nffgName) {
		for (Nffgs.Nffg nffg : this.nffgs.getNffg()) {
			if(nffg.getName().equals(nffgName))
				return nffg;
		}
		return null;
	}

}
