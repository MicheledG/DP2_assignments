package it.polito.dp2.NFFG.sol3.service;

import javax.management.relation.RelationException;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;

public class NffgService {
	
	private NffgsDB nffgsDB = NffgsDB.newNffgsDB();
	private PoliciesDB policiesDB = PoliciesDB.newPoliciesDB();
	
	/* store nffgs into the DB */
	public void storeNffgs(Nffgs nffgs) throws AlreadyLoadedException, ServiceException {
		/* obtain from NffgDB the names of the loaded NFFGs */
		nffgsDB.storeNffgs(nffgs);
	}
	
	/* get the list of nffg stored into the DB */
	public Nffgs getNffgs() throws NoGraphException, ServiceException {
		Nffgs nffgs = nffgsDB.getNffgs();
		return nffgs;
	}
	
	/* get a single nffg stored into the DB */
	public Nffgs getSingleNffgs(String nffgName) throws UnknownNameException, ServiceException{
		Nffgs nffgs = nffgsDB.getNffgs(nffgName);
		return nffgs;
	}
	
	/* delete all the data from the DB */
	public void deleteNffgs() throws ServiceException {
		/* delete both nffgs and policies */
		nffgsDB.deleteNffgs();
		policiesDB.deletePolicies();
		return;
	}
	
	public void deleteSingleNffgs(String nffgName, boolean deletePolicies) throws UnknownNameException,
	RelationException, ServiceException{
		if(!nffgsDB.containsNffg(nffgName))
			throw new UnknownNameException("missing nffg named "+nffgName);
		
		if(policiesDB.refersNffg(nffgName))
			throw new RelationException("there are policies referring nffg named "+nffgName);
		
		/* otherwise delete the nffg */
		nffgsDB.deleteNffgs(nffgName);
		
		return;
	}
	
	/* store the policies into the DB */
	public void storePolicies(Policies policies) throws RelationException, AlreadyLoadedException{
		
		/* check if a policy to store refers an Nffg not loaded on NffgDB */
		for (Policies.Policy policy : policies.getPolicy()) {
			if(!nffgsDB.containsNffg(policy.getNffg()))
				throw new RelationException("missing nffg "+policy.getNffg()+" for policy " + policy.getName());
		}
		
		/* otherwise store the policies into policiesDB */
		policiesDB.storePolicies(policies);
		return;
	}
	
	//TODO: update policies
	
	/* get the list of policies loaded on the DB */
	public Policies getPolicies() throws NoPolicyException{
		/* obtain from PoliciesDB all the policies stored */
		Policies policies = policiesDB.getPolicies();
		return policies;
	}
	
	/* get a single policy from policiesDB */
	public Policies getSinglePolicies(String policyName) throws UnknownNameException{
		Policies policies = policiesDB.getPolicies(policyName);
		return policies;
	}
	
	/* delete all the policies */
	public void deletePolicies(){
		policiesDB.deletePolicies();
	}
	
	/* delete a single policy */
	public void deleteSinglePolicies(String policyName) throws UnknownNameException{
		policiesDB.deletePolicies(policyName);
		return;
	}
	
}
