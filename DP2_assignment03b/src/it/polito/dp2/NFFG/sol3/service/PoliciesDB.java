package it.polito.dp2.NFFG.sol3.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;
import it.polito.dp2.NFFG.sol3.service.jaxb.PropertyType;

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
	
	/* store the policies (1 or more) into the DB*/
	public void storePolicies(Policies policies) throws AlreadyLoadedException{
		for (Policies.Policy policy : policies.getPolicy()) {
			String policyName = policy.getName();
			if(this.containsPolicy(policyName))
					throw new AlreadyLoadedException("policy named "+policyName+"already stored");
			this.mapPolicyNamePolicyObject.put(policyName, policy);
		}
	}
	
	/* update the policies (1 or more) into the DB*/
	public void updatePolicies(Policies policiesUpdate) throws UnknownNameException{
		for (Policies.Policy policyUpdate : policiesUpdate.getPolicy()) {
			String policyUpdateName = policyUpdate.getName();
			if(!this.containsPolicy(policyUpdateName))
					throw new UnknownNameException("missing policy named "+policyUpdateName);
			this.updatePolicyEntry(policyUpdateName, policyUpdate);
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
	
	/* return policies containing a single nffg */
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


	public boolean isReachabilityPolicy(String policyName) {
		Policies.Policy policy = this.mapPolicyNamePolicyObject.get(policyName);
		if(policy.getProperty().equals(PropertyType.REACHABILITY))
			return true;
		else
			return false;
	}
}
