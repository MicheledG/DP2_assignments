package it.polito.dp2.NFFG.sol3.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;

public class PoliciesDB {
	
	private Map<String, Policies.Policy> mapPolicyNamePolicyObject = new HashMap<String, Policies.Policy>();
	
	private static PoliciesDB policiesDB = new PoliciesDB();
	
	public static PoliciesDB newPoliciesDB(){
		return policiesDB;
	}
	
	/* store the policies (1 or more) into the DB*/
	public void storePolicies(Policies policies) throws AlreadyLoadedException{
		for (Policies.Policy policy : policies.getPolicy()) {
			String policyName = policy.getName();
			if(this.containsPolicy(policyName))
					throw new AlreadyLoadedException("policy named "+policyName+"already stored");
			this.mapPolicyNamePolicyObject.put(policyName, policy);
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
	
	public boolean containsPolicy(String policyName){
		return this.mapPolicyNamePolicyObject.containsKey(policyName);
	}
	
	public Set<String> getPoliciesNames(){
		return this.mapPolicyNamePolicyObject.keySet();
	}
	
	/* check if there is a policy referring a particular nffg */
	public boolean refersNffg(String nffgName){
		for (Map.Entry<String, Policies.Policy> mapEntry: this.mapPolicyNamePolicyObject.entrySet()) {
			if(mapEntry.getValue().getNffg().equals(nffgName))
				return true;
		}
		return false;
	}
	
	public boolean isEmpty(){
		return this.mapPolicyNamePolicyObject.isEmpty();
	}
}
