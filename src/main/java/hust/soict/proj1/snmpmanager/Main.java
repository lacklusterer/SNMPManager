package hust.soict.proj1.snmpmanager;

public class Main {
	public static void main(String[] args) {
		SNMPManagerFacade snmpManager = new SNMPManagerFacade();

		// Create the SNMP target
		String communityString = "killme";
		String ipAddr = "localhost/161";
		int retries = 2;
		long timeout = 15000;
		int version = 2;
		snmpManager.createTarget(ipAddr, communityString, retries, timeout, version);

		// Get the value for the specified OID
		String oid = "1.3.6.1.2.1.1.1.0";
		String getResult = snmpManager.get(oid, ipAddr);
		System.out.println(getResult);

		String result = snmpManager.walk(".1.3.6.1.2.1", ipAddr);
		System.out.println(result);

		snmpManager.closeSnmp();
	}
}
