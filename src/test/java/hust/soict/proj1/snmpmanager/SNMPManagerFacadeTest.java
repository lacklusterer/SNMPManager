package hust.soict.proj1.snmpmanager;

import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import hust.soict.proj1.frontend.Subscriber;

public class SNMPManagerFacadeTest implements Subscriber {

	private String lastMessage;

	@Override
	public void update(String message) {
		this.lastMessage = message;
		System.out.println("Update received: " + message);
	}

	@Test
	public void testGet() {
		SNMPManagerFacade snmpManager = SNMPManagerFacade.getInstance();
		snmpManager.addSubscriber(this);

		String communityString = "killme";
		String ipAddr = "localhost/161";
		int retries = 2;
		long timeout = 1000;
		int version = 2;
		snmpManager.createTarget(ipAddr, communityString, retries, timeout, version);

		String oid = "1.3.6.1.2.1.1.1.0";
		snmpManager.get(oid, ipAddr);

		Assert.assertNotNull(lastMessage);
		System.out.println("Result: " + lastMessage);

		snmpManager.getNext(oid, ipAddr);

		Assert.assertNotNull(lastMessage);
		System.out.println("Result next: " + lastMessage);

		Map<String, Integer> managedDevices = snmpManager.getManagedDevices();
		System.out.println("Managed Devices:");
		for (Map.Entry<String, Integer> entry : managedDevices.entrySet()) {
			System.out.println("IP Address: " + entry.getKey() + ", SNMP Version: " + entry.getValue());
		}

		snmpManager.removeSubscriber(this);
	}
}
