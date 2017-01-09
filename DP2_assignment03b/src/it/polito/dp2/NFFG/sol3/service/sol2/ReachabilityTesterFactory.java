package it.polito.dp2.NFFG.sol3.service.sol2;

import it.polito.dp2.NFFG.sol3.service.lab2.ReachabilityTester;
import it.polito.dp2.NFFG.sol3.service.lab2.ReachabilityTesterException;

public class ReachabilityTesterFactory extends it.polito.dp2.NFFG.sol3.service.lab2.ReachabilityTesterFactory{

	@Override
	public ReachabilityTester newReachabilityTester() throws ReachabilityTesterException {
		return new ReachabilityTesterImpl();
	}

}
