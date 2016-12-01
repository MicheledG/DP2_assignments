package it.polito.dp2.NFFG.sol2;

import it.polito.dp2.NFFG.*;
import it.polito.dp2.NFFG.lab2.NoGraphException;
import it.polito.dp2.NFFG.lab2.ReachabilityTester;
import it.polito.dp2.NFFG.lab2.ServiceException;
import it.polito.dp2.NFFG.lab2.UnknownNameException;

public class ReachabilityTesterImpl implements ReachabilityTester {
	
	//TODO: HOW HAVE WE TO RETRIEVE RANDOM DATA?
	NffgVerifier nffgVerifier;
	
	public ReachabilityTesterImpl() {
		try {
			//TODO: WHICH IS THE "NffgVerifierFactory" TO USE?
			this.nffgVerifier = NffgVerifierFactory.newInstance().newNffgVerifier();
		} catch (NffgVerifierException | FactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void loadNFFG(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean testReachability(String srcName, String destName)
			throws UnknownNameException, ServiceException, NoGraphException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCurrentGraphName() {
		// TODO Auto-generated method stub
		return null;
	}

}
