package it.polito.dp2.NFFG.sol3.client1;

import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.NffgVerifierFactory;
import it.polito.dp2.NFFG.lab3.AlreadyLoadedException;
import it.polito.dp2.NFFG.lab3.NFFGClient;
import it.polito.dp2.NFFG.lab3.ServiceException;
import it.polito.dp2.NFFG.lab3.UnknownNameException;

public class NFFGClientImpl implements NFFGClient {
	
	private NffgVerifier nffgVerifier;
	
	public NFFGClientImpl() throws NffgVerifierException {
		/* create a client with nffg data obtained from the random generator */
		it.polito.dp2.NFFG.NffgVerifierFactory nffgVerifierFactory = NffgVerifierFactory.newInstance();
		nffgVerifier = nffgVerifierFactory.newNffgVerifier();
	}
	
	@Override
	public void loadNFFG(String name) throws UnknownNameException, AlreadyLoadedException, ServiceException {

	}

	@Override
	public void loadAll() throws AlreadyLoadedException, ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadReachabilityPolicy(String name, String nffgName, boolean isPositive, String srcNodeName,
			String dstNodeName) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unloadReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean testReachabilityPolicy(String name) throws UnknownNameException, ServiceException {
		// TODO Auto-generated method stub
		return false;
	}

}
