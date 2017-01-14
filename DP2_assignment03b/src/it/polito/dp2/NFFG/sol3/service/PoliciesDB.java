package it.polito.dp2.NFFG.sol3.service;

import java.util.HashMap;
import java.util.Map;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;

public class PoliciesDB {
	
	private Map<String, Policies.Policy> mapPolicyNamePolicyObject = new HashMap<String, Policies.Policy>();
	
	private static PoliciesDB policiesDB = new PoliciesDB();
	
	public static PoliciesDB newPoliciesDB(){
		return policiesDB;
	}
	
	/* store the policies (1 or more) into the DB*/
	public void storePolicies(Policies policies){
		for (Policies.Policy policy : policies.getPolicy()) {
			this.mapPolicyNamePolicyObject.put(policy.getName(), policy);
		}
	}
	
	/* return the list of all the nffg */
	public Policies getPolicies() throws NoPolicyException {
		if(this.mapPolicyNamePolicyObject.isEmpty())
			throw new NoPolicyException("no policy in the DB");
		
		Policies policies = new Policies();
		for (Map.Entry<String, Policies.Policy> mapEntry: this.mapPolicyNamePolicyObject.entrySet()) {
			policies.getPolicy().add(mapEntry.getValue());
		}
		
		return policies;
	}
	
	/* return nffgs containing a single nffg */
	public Policies getPolicies(String policyName) throws UnknownNameException{
		if(!this.mapPolicyNamePolicyObject.containsKey(policyName))
			throw new UnknownNameException("policy named " + policyName + " not found");
		else{
			Policies policies = new Policies();
			policies.getPolicy().add(this.mapPolicyNamePolicyObject.get(policyName));
			return policies;
		}		
	}
	
	/* clear local map */
	public void deletePolicies(){
		this.mapPolicyNamePolicyObject.clear();
		
	}
	
	/* remove policy from the local map */
	public void deletePolicies(String policyName) throws UnknownNameException{		
		
		if(!this.mapPolicyNamePolicyObject.containsKey(policyName))
			throw new UnknownNameException("policy named " + policyName + " not found");
		else
			this.mapPolicyNamePolicyObject.remove(policyName);
	
	}
	
}
