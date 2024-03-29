package it.polito.dp2.NFFG.sol3.client1;

import it.polito.dp2.NFFG.NffgVerifierException;
import it.polito.dp2.NFFG.lab3.NFFGClient;
import it.polito.dp2.NFFG.lab3.NFFGClientException;

public class NFFGClientFactory extends it.polito.dp2.NFFG.lab3.NFFGClientFactory {

	@Override
	public NFFGClient newNFFGClient() throws NFFGClientException {
		try{
			NFFGClientImpl nffgClient = new NFFGClientImpl();
			return nffgClient;
		} catch (NffgVerifierException e) {
			throw new NFFGClientException(e);
		}
	}

}
