package it.polito.dp2.NFFG.sol3.service;

import javax.management.relation.RelationException;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;

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
	
}
