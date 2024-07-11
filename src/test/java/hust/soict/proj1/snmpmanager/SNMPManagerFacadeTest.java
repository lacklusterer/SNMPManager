package hust.soict.proj1.snmpmanager;

import org.junit.Assert;
import org.junit.Test;

public class SNMPManagerFacadeTest {

	@Test
	public void testGet() {
		SNMPManagerFacade snmpManager = new SNMPManagerFacade();

		// Create the SNMP target
		String communityString = "killme";
		String ipAddr = "localhost/161";
		int retries = 2;
		long timeout = 1000;
		int version = 2;
		snmpManager.createTarget(ipAddr, communityString, retries, timeout, version);

		// Get the value for the specified OID
		String oid = "1.3.6.1.2.1.1.1.0";
		String result = snmpManager.get(oid, ipAddr);
		Assert.assertNotNull(result);
		System.out.println("Result: " + result);

		// Get next object
		String oidNext = "1.3.6.1.2.1.1.1.0";
		String resultNext = snmpManager.getNext(oidNext, ipAddr);
		Assert.assertNotNull(resultNext);
		System.out.println("Result next: " + resultNext);
	}
}
