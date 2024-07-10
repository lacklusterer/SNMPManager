package hust.soict.proj1.snmpmanager;

public class Main {
	public static void main(String[] args) {
		SNMPManagerFacade snmpManager = new SNMPManagerFacade();

		// Create the SNMP target
		String communityString = "killme";
		String ipAddr = "localhost";
		int retries = 2;
		long timeout = 3000;
		int version = 2;
		snmpManager.createTarget(ipAddr, communityString, retries, timeout, version);

		// Get the value for the specified OID
		String oid = "1.3.6.1.2.1.1.1.0";
		String result = snmpManager.get(oid, ipAddr);
		System.out.println("Result: " + result);

		snmpManager.closeSnmp();
	}
}
