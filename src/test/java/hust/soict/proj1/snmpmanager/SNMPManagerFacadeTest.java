package hust.soict.proj1.snmpmanager;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

public class SNMPManagerFacadeTest {

	@Test
	public void testGet() {
		SNMPManagerFacade snmpManager = new SNMPManagerFacade();

		String communityString = "killme";
		String ipAddr = "localhost/161";
		int retries = 2;
		long timeout = 1000;
		int version = 2;
		snmpManager.createTarget(ipAddr, communityString, retries, timeout, version);

		String oid = "1.3.6.1.2.1.1.1.0";
		String result = snmpManager.get(oid, ipAddr);
		Assert.assertNotNull(result);
		System.out.println("Result: " + result);

		String getNextResult = snmpManager.getNext(oid, ipAddr);
		Assert.assertNotNull(getNextResult);
		System.out.println("Result next: " + getNextResult);

		Map<String, Integer> managedDevices = snmpManager.getManagedDevices();
		System.out.println("Managed Devices:");
		for (Map.Entry<String, Integer> entry : managedDevices.entrySet()) {
			System.out.println("IP Address: " + entry.getKey() + ", SNMP Version: " + entry.getValue());
		}

	}
}
