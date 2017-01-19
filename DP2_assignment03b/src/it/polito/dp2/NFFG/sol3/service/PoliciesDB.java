package it.polito.dp2.NFFG.sol3.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;

public class PoliciesDB {
	
	private Map<String, Policies.Policy> mapPolicyNamePolicyObject = new HashMap<String, Policies.Policy>();
	
	private static PoliciesDB policiesDB = new PoliciesDB();
	
	private void updatePolicyEntry(String policyName, Policies.Policy policyUpdate) {
		
		/* remove the old policy and add the new one*/
		this.mapPolicyNamePolicyObject.remove(policyName);
		this.mapPolicyNamePolicyObject.put(policyName, policyUpdate);
		
	}
	
	
	public static PoliciesDB newPoliciesDB(){
		return policiesDB;
	}
	
	/* store or update the policies (1 or more) into the DB*/
	public void storePolicy(Policies.Policy policy){
		String policyName = policy.getName();
		if(this.containsPolicy(policyName)) {
			/* it is an update of a policy */
			this.updatePolicyEntry(policyName, policy);
		}
		else
			/* it is a new policy to store */
			this.mapPolicyNamePolicyObject.put(policyName, policy);			
	}

	/* return the list of all the nffg */
	public Set<String> getPolicyNames() throws NoPolicyException {
		if(this.mapPolicyNamePolicyObject.isEmpty())
			throw new NoPolicyException("no policy in the DB");

		return this.mapPolicyNamePolicyObject.keySet();
	}
	
	/* return policies containing a single nffg */
	public Policies.Policy getPolicy(String policyName) throws UnknownNameException{
		if(!this.mapPolicyNamePolicyObject.containsKey(policyName))
			throw new UnknownNameException("policy named " + policyName + " not found");
		else
			return this.mapPolicyNamePolicyObject.get(policyName);	
	}
	
	/* clear local map */
	public void deletePolicies(){
		this.mapPolicyNamePolicyObject.clear();
	}
	
	/* remove policy from the local map */
	public void deletePolicy(String policyName) throws UnknownNameException{		
		
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
	public boolean areReferringThisNffg(String nffgName){
		for (Map.Entry<String, Policies.Policy> mapEntry: this.mapPolicyNamePolicyObject.entrySet()) {
			if(mapEntry.getValue().getNffg().equals(nffgName))
				return true;
		}
		return false;
	}
	
	/* return the list of the policy names which refers a Nffg */
	public List<String> getPolicyNamesReferringNffg(String nffgName){
		List<String> policyNames = new ArrayList<String>();
		for (Map.Entry<String, Policies.Policy> mapPolicyNamePolicyObjectEntry: this.mapPolicyNamePolicyObject.entrySet()) {
			if(mapPolicyNamePolicyObjectEntry.getValue().getNffg().equals(nffgName))
				policyNames.add(mapPolicyNamePolicyObjectEntry.getKey());
		}
		return policyNames;
	}
	
	public boolean isEmpty(){
		return this.mapPolicyNamePolicyObject.isEmpty();
	}

}
