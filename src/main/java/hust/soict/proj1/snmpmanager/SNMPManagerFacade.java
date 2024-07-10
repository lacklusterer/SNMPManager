package hust.soict.proj1.snmpmanager;

import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.AbstractTarget;
import org.snmp4j.CommunityTarget;
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
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
		}
		this.targetMap = new HashMap<>();
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
		target.setAddress(GenericAddress.parse("udp:" + ipAddr + "/161"));
		target.setRetries(retries);
		target.setTimeout(timeout);

		targetMap.put(ipAddr, target);
	}

	public void removeTarget(String ipAddr) {
		targetMap.remove(ipAddr);
	}

	public String get(String oid, String ipAddr) {
		try {
			snmp.listen();

			PDU pdu = new PDU();
			pdu.add(new VariableBinding(new OID(oid)));
			pdu.setType(PDU.GET);

			Target<?> target = targetMap.get(ipAddr);
			ResponseEvent<?> response = snmp.get(pdu, target);

			if (response != null && response.getResponse() != null) {
				PDU responsePDU = response.getResponse();
				if (responsePDU.getErrorStatus() == PDU.noError) {
					for (VariableBinding vb : responsePDU.getVariableBindings()) {
						return vb.toString();
					}
				} else {
					return "Error: " + responsePDU.getErrorStatusText();
				}
			} else {
				return "Error: No response received.";
			}

		} catch (IOException e) {
			// Handle SNMP-related IOException
			return "Error: " + e.getMessage();
		} finally {
			if (snmp != null) {
				try {
					snmp.close();
				} catch (IOException e) {
					System.err.println("Error closing SNMP session: " + e.getMessage());
				}
			}
		}

		return null;
	}

}
