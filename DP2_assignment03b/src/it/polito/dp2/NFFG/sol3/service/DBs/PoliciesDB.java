package it.polito.dp2.NFFG.sol3.service.DBs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;

public class PoliciesDB {
	
	private Map<String, Policies.Policy> mapPolicyNamePolicyObject = new ConcurrentHashMap<String, Policies.Policy>();
	
	private static PoliciesDB policiesDB = new PoliciesDB();
	
	public static PoliciesDB getPoliciesDB(){
		return policiesDB;
	}
	
	/* store or update the policies (1 or more) into the DB*/
	public void storePolicy(Policies.Policy policy){
		String policyName = policy.getName();
		/* also if there is already a policy the put works correctly*/
		this.mapPolicyNamePolicyObject.put(policyName, policy);
	}

	/* return the list of all the policy names */
	public Set<String> getPolicyNames() throws NoPolicyException {
		Set<String> policyNames = this.mapPolicyNamePolicyObject.keySet();
		if(policyNames.isEmpty())
			throw new NoPolicyException("no policy in the DB");

		return policyNames;
	}
	
	/* return policy */
	public Policies.Policy getPolicy(String policyName) throws UnknownNameException{
		Policies.Policy policy = this.mapPolicyNamePolicyObject.get(policyName);
		if(policy == null)
			throw new UnknownNameException("policy named " + policyName + " not found");
		else
			return policy;	
	}
	
	/* clear local map */
	public void deletePolicies(){
		this.mapPolicyNamePolicyObject.clear();
	}
	
	/* remove policy from the local map */
	public void deletePolicy(String policyName) throws UnknownNameException{		
		Policies.Policy policyRemoved = this.mapPolicyNamePolicyObject.remove(policyName);
		if(policyRemoved == null)
			throw new UnknownNameException("policy named " + policyName + " not found");
	}
	
	public boolean containsPolicy(String policyName){
		return this.mapPolicyNamePolicyObject.containsKey(policyName);
	}
	
	public Set<String> getPoliciesNames(){
		return this.mapPolicyNamePolicyObject.keySet();
	}
	
	/* check if there is a policy referring a particular nffg */
	/* using concurrent hash map there are no problem during iteration => no concurrent modification exception (and also no semantic problem) */
	public boolean areReferringThisNffg(String nffgName){
		for (Map.Entry<String, Policies.Policy> entryPolicyNamePolicyObjectEntry: this.mapPolicyNamePolicyObject.entrySet()) {
			if(entryPolicyNamePolicyObjectEntry.getValue().getNffg().equals(nffgName))
				return true;
		}
		return false;
	}
	
	/* return the list of the policy names which refers a Nffg */
	/* using concurrent hash map there are no problem during iteration => no concurrent modification exception (and also no semantic problem) */
	public List<String> getPolicyNamesReferringNffg(String nffgName){
		List<String> policyNames = new ArrayList<String>();
		for (Map.Entry<String, Policies.Policy> entryPolicyNamePolicyObjectEntry: this.mapPolicyNamePolicyObject.entrySet()) {
			if(entryPolicyNamePolicyObjectEntry.getValue().getNffg().equals(nffgName))
				policyNames.add(entryPolicyNamePolicyObjectEntry.getKey());
		}
		return policyNames;
	}
	
	public boolean isEmpty(){
		return this.mapPolicyNamePolicyObject.isEmpty();
	}
	
}
