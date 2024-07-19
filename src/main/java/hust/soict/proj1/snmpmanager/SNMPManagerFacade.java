package hust.soict.proj1.snmpmanager;

import org.snmp4j.Snmp;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.AbstractTarget;
import org.snmp4j.Target;
import org.snmp4j.PDU;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

public class SNMPManagerFacade {

	private static volatile SNMPManagerFacade instance;

	private Snmp snmp;
	private Map<String, Target<?>> targetMap;

	public SNMPManagerFacade() {
		try {
			this.snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
		this.targetMap = new HashMap<>();
	}

	public static SNMPManagerFacade getInstance() {
		if (instance == null) {
			synchronized (SNMPManagerFacade.class) {
				if (instance == null) {
					instance = new SNMPManagerFacade();
				}
			}
		}
		return instance;
	}

	public String get(String oid, String ipAddr) {
		Target target = targetMap.get(ipAddr);
		return SNMPUtils.sendRequest(oid, PDU.GET, target, snmp);
	}

	public String getNext(String oid, String ipAddr) {
		Target target = targetMap.get(ipAddr);
		return SNMPUtils.sendRequest(oid, PDU.GETNEXT, target, snmp);
	}

	public String getBulk(String oid, String ipAddr) {
		// TODO: Implement
		return null;
	}

	public String walk(String oid, String ipAddr) {
		return SNMPUtils.walk(this, oid, ipAddr);
	}

	public String discover(String ipAddr) {
		System.out.println("Recursively walking with root oid");
		return walk("1.3.6.1.2.1", ipAddr);
	}

	public void createTarget(String ipAddr, String communityString, int retries, long timeout, int version) {
		AbstractTarget target = SNMPUtils.createTarget(ipAddr, communityString, retries, timeout, version);
		targetMap.put(ipAddr, target);
	}

	public void removeTarget(String ipAddr) {
		targetMap.remove(ipAddr);
	}

	public void closeSnmp() {
		if (snmp != null) {
			try {
				snmp.close();
			} catch (IOException e) {
				System.out.println("Error: " + e.getMessage());
			}
		}
	}

	public Map<String, Integer> getManagedDevices() {
		Map<String, Integer> devices = new HashMap<>();
		for (Map.Entry<String, Target<?>> entry : targetMap.entrySet()) {
			devices.put(entry.getKey(), entry.getValue().getVersion());
		}
		return devices;
	}
}
