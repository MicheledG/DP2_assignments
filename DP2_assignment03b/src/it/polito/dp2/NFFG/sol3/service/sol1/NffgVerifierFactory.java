package it.polito.dp2.NFFG.sol3.service.sol1;

import it.polito.dp2.NFFG.NffgVerifier;
import it.polito.dp2.NFFG.NffgVerifierException;

public class NffgVerifierFactory extends it.polito.dp2.NFFG.NffgVerifierFactory {

	@Override
	public NffgVerifier newNffgVerifier() throws NffgVerifierException {
		/* returns a NffgVerifierImpl object */
		return new NffgVerifierImpl();
	}

}
