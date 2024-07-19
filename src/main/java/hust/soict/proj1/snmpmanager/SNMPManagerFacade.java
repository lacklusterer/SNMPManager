package hust.soict.proj1.snmpmanager;

import org.snmp4j.Snmp;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.AbstractTarget;
import org.snmp4j.Target;
import org.snmp4j.PDU;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

import hust.soict.proj1.frontend.Subscriber;

public class SNMPManagerFacade {

	private static volatile SNMPManagerFacade instance;

	private Snmp snmp;
	private Map<String, Target<?>> targetMap;
	private List<Subscriber> subscribers;

	public SNMPManagerFacade() {
		try {
			this.snmp = new Snmp(new DefaultUdpTransportMapping());
			snmp.listen();
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			return;
		}
		this.targetMap = new HashMap<>();
		this.subscribers = new ArrayList<>();
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

	public void addSubscriber(Subscriber subscriber) {
		subscribers.add(subscriber);
	}

	public void removeSubscriber(Subscriber subscriber) {
		subscribers.remove(subscriber);
	}

	private void notifySubscribers(String message) {
		for (Subscriber subscriber : subscribers) {
			subscriber.update(message);
		}
	}

	public void get(String oid, String ipAddr) {
		Target target = targetMap.get(ipAddr);
		String message = SNMPUtils.sendRequest(oid, PDU.GET, target, snmp);
		notifySubscribers(message);
	}

	public String getNext(String oid, String ipAddr) {
		Target target = targetMap.get(ipAddr);
		String message = SNMPUtils.sendRequest(oid, PDU.GETNEXT, target, snmp);
		notifySubscribers(message);
		return message;
	}

	public void walk(String oid, String ipAddr) {
		String message = SNMPUtils.walk(this, oid, ipAddr);
		notifySubscribers(message);
	}

	public void discover(String ipAddr) {
		System.out.println("Recursively walking with root oid");
		walk("1.3.6.1.2.1", ipAddr);
	}

	public void createTarget(String ipAddr, String communityString, int retries, long timeout, int version) {
		AbstractTarget target = SNMPUtils.createTarget(ipAddr, communityString, retries, timeout, version);
		targetMap.put(ipAddr, target);
	}

	public void removeTarget(String ipAddr) {
		targetMap.remove(ipAddr);
	}

	public Map<String, Integer> getManagedDevices() {
		Map<String, Integer> devices = new HashMap<>();
		for (Map.Entry<String, Target<?>> entry : targetMap.entrySet()) {
			devices.put(entry.getKey(), entry.getValue().getVersion() + 1);
		}
		return devices;
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
}
