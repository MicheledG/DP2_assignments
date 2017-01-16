package it.polito.dp2.NFFG.sol3.service;

import java.lang.invoke.WrongMethodTypeException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.management.relation.RelationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;

import it.polito.dp2.NFFG.sol3.service.exceptions.*;
import it.polito.dp2.NFFG.sol3.service.jaxb.NamedEntities;
import it.polito.dp2.NFFG.sol3.service.jaxb.Nffgs;
import it.polito.dp2.NFFG.sol3.service.jaxb.Policies;
import it.polito.dp2.NFFG.sol3.service.jaxb.VerificationResultType;


public class NffgService {
	
	private NffgsDB nffgsDB = NffgsDB.newNffgsDB();
	private PoliciesDB policiesDB = PoliciesDB.newPoliciesDB();
	
	private static final String POSITIVE_POLICY_RESULT_DESCRIPTION = "Policy verification result true";
	private static final String NEGATIVE_POLICY_RESULT_DESCRIPTION = "Policy verification result not true";
	
	private void checkNffgsReferencedByPolicies(Policies policies) throws RelationException{
		/* check if a policy refers an Nffg not loaded on NffgDB */
		for (Policies.Policy policy : policies.getPolicy()) {
			if(!nffgsDB.containsNffg(policy.getNffg()))
				throw new RelationException("missing nffg "+policy.getNffg()+" for policy " + policy.getName());
		}
	}
	
	private XMLGregorianCalendar getNowTimeXMLGregorianCalendar(){
		Date now = new Date(System.currentTimeMillis());
		/* create the xml gregorian calendar */
		GregorianCalendar nowGregorianCalendar = new GregorianCalendar();
		nowGregorianCalendar.setTime(now);
		XMLGregorianCalendarImpl nowXMLGregorianCalendar = new XMLGregorianCalendarImpl(nowGregorianCalendar);
		return nowXMLGregorianCalendar;
	}
	
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
			/* missing nffg */
			throw new UnknownNameException("missing nffg named "+nffgName);
		
		if(deletePolicies){
			/* delete eventual policies referring to this nffg */
			if(policiesDB.areReferringThisNffg(nffgName)){
				/* get the names of the policies that refers to this nffg */
				List<String> policyNames = policiesDB.getPolicyNamesReferringNffg(nffgName);
				/* remove the policies referring this nffg from policiesDB */
				for (String policyName : policyNames) {
					policiesDB.deletePolicy(policyName);
				}
			}
		}	
		else{
			/* don't have to remove the policies but you must check if there are policies referring this nffg */
			if(policiesDB.areReferringThisNffg(nffgName))
				throw new RelationException("there are policies referring nffg "+nffgName);
		}
		
		/* eventually delete the nffg */
		nffgsDB.deleteNffg(nffgName);
		
		return;
	}
	
	/* store or update already present policies into the DB */
	public void storePolicies(Policies policies) throws RelationException {
		
		/* check if the policies to store refers to an Nffg not stored inside the DB*/
		this.checkNffgsReferencedByPolicies(policies);
		/* check if the nodes referenced by the policies are present in the nffg */
		for (Policies.Policy policy : policies.getPolicy()) {
			String policyName = policy.getName();
			String nffgName = policy.getNffg();
			/* check if the referenced nffg contains the src node */
			String srcNodeName = policy.getSourceNode();
			if(!nffgsDB.nffgContainsNode(nffgName, srcNodeName))
				throw new RelationException("Missing node "+srcNodeName+" in nffg "+nffgName+" referenced by policy "+policyName);
			/* check if the referenced nffg contains the dst node */
			String dstNodeName = policy.getDestinationNode();
			if(!nffgsDB.nffgContainsNode(nffgName, dstNodeName))
				throw new RelationException("Missing node "+dstNodeName+" in nffg "+nffgName+" referenced by policy "+policyName);
		}
		/* store the policies into policiesDB */
		policiesDB.storePolicies(policies);
		return;
	}
	
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
		policiesDB.deletePolicy(policyName);
		return;
	}
	
	/* verify a reachability policy already loaded on policiesDB */
	public Policies verifyReachabilityPolicies(NamedEntities policyNamesPosted) throws UnknownNameException, ServiceException {
		
		List<String> policyNames = policyNamesPosted.getName();
		/* check if all the policies are stored on policiesDB and if they are of type "Reachability"*/
		for (String policyName: policyNames) {
			if(!policiesDB.containsPolicy(policyName))
				throw new UnknownNameException("missing policy named "+policyName);
			else
				if(!policiesDB.isReachabilityPolicy(policyName))
					throw new WrongMethodTypeException("policy "+policyName+"is not of type reachability");
		}
		
		/*verify each policy*/
		Policies policiesVerified = new Policies();
		for (String policyName: policyNames) {
			Policies.Policy policyToVerify = policiesDB.getPolicies(policyName).getPolicy().get(0);
			String nffgName = policyToVerify.getNffg();
			String srcNodeName = policyToVerify.getSourceNode();
			String dstNodeName = policyToVerify.getDestinationNode();
			boolean pathIsPresent = nffgsDB.isTherePath(nffgName, srcNodeName, dstNodeName);
			XMLGregorianCalendar nowTime = this.getNowTimeXMLGregorianCalendar();
			/* compute the result of the verification */
			boolean satisfied;
			if(policyToVerify.isPositive())	
				satisfied = pathIsPresent;
			else 
				satisfied = !pathIsPresent;
			String verificationDescription;
			if(satisfied)
				verificationDescription = NffgService.POSITIVE_POLICY_RESULT_DESCRIPTION;
			else
				verificationDescription = NffgService.NEGATIVE_POLICY_RESULT_DESCRIPTION;
			VerificationResultType verificationResult = new VerificationResultType();
			verificationResult.setSatisfied(satisfied);
			verificationResult.setLastVerification(nowTime);
			verificationResult.setDescription(verificationDescription);
			
			/* add the verified policy to the set of the policies verified */
			Policies.Policy policyVerified = policyToVerify;
			policyVerified.setVerificationResult(verificationResult);
			policiesVerified.getPolicy().add(policyVerified);
		}
		
		/* update the policies on policiesDB */
		policiesDB.storePolicies(policiesVerified);
		
		return policiesVerified;
	}
	
}
