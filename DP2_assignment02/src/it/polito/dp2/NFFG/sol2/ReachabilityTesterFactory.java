package it.polito.dp2.NFFG.sol2;

import it.polito.dp2.NFFG.lab2.ReachabilityTester;
import it.polito.dp2.NFFG.lab2.ReachabilityTesterException;

public class ReachabilityTesterFactory extends it.polito.dp2.NFFG.lab2.ReachabilityTesterFactory{

	@Override
	public ReachabilityTester newReachabilityTester() throws ReachabilityTesterException {
		return new ReachabilityTesterImpl();
	}

}
