package hust.soict.proj1.snmpmanager;

import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.AbstractTarget;
import org.snmp4j.Target;
import org.snmp4j.CommunityTarget;
import org.snmp4j.UserTarget;
import org.snmp4j.PDU;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;

public class SNMPManagerFacade {

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

	public String get(String oid, String ipAddr) {
		return sendRequest(oid, PDU.GET, ipAddr);
	}

	public String getNext(String oid, String ipAddr) {
		return sendRequest(oid, PDU.GETNEXT, ipAddr);
	}

	public String getBulk(String oid, String ipAddr) {
		// TODO: Implement
		return null;
	}

	public String walk(String oid, String ipAddr) {
		StringBuilder result = new StringBuilder();
		String nextOid = oid;
		while (nextOid != null) {
			String response = getNext(nextOid, ipAddr);
			if (response.startsWith("Error:") || !response.contains(oid)) {
				break;
			}
			result.append(response);
			nextOid = getNextOidFromResponse(response);
		}
		return result.toString();
	}

	public String discover(String ipAddr) {
		System.out.println("Recursively walking with root oid");
		return walk(".1.3.6.1.2.1", ipAddr);
	}

	public void createTarget(String ipAddr, String communityString, int retries, long timeout, int version) {
		AbstractTarget target = null;
		int snmpVersion = 0;
		switch (version) {
			case 1:
				snmpVersion = SnmpConstants.version1;
				break;
			case 2:
				snmpVersion = SnmpConstants.version2c;
				break;
			case 3:
				snmpVersion = SnmpConstants.version3;
				break;
			default:
				throw new IllegalArgumentException("Invalid SNMP version: " + version);
		}

		if (version == 3) {
			target = new UserTarget();
			// TODO: Add the remaining arguments for version3
		} else {
			target = new CommunityTarget();
			((CommunityTarget) target).setCommunity(new OctetString(communityString));
		}

		target.setVersion(snmpVersion);
		target.setAddress(GenericAddress.parse("udp:" + ipAddr));
		target.setRetries(retries);
		target.setTimeout(timeout);

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

	private PDU constructPDU(String oid, int pduType) {
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid)));
		pdu.setType(pduType);
		return pdu;
	}

	private String sendRequest(String oid, int pduType, String ipAddr) {
		Target target = targetMap.get(ipAddr);
		if (target == null) {
			return "Error: Target not found.";
		}
		try {
			PDU pdu = constructPDU(oid, pduType);
			ResponseEvent<?> response = snmp.get(pdu, target);
			return parseResponse(response);
		} catch (IOException e) {
			return "Error: " + e.getMessage();
		}
	}

	private String parseResponse(ResponseEvent response) {
		if (response != null && response.getResponse() != null) {
			PDU responsePDU = response.getResponse();
			if (responsePDU.getErrorStatus() == PDU.noError) {
				StringBuilder result = new StringBuilder();
				for (VariableBinding vb : responsePDU.getVariableBindings()) {
					result.append(vb.toString()).append("\n");
				}
				return result.toString();
			} else {
				return "Error: " + responsePDU.getErrorStatusText();
			}
		} else {
			return "Error: No response received.";
		}
	}

	private String getNextOidFromResponse(String response) {
		String[] lines = response.split("\n");
		if (lines.length > 0) {
			String lastLine = lines[lines.length - 1];
			String[] parts = lastLine.split(" = ");
			if (parts.length > 0) {
				return parts[0].trim();
			}
		}
		return null;
	}
}
