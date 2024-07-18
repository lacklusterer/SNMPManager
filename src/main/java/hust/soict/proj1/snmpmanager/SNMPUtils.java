package hust.soict.proj1.snmpmanager;

import org.snmp4j.PDU;
import org.snmp4j.AbstractTarget;
import org.snmp4j.Target;
import org.snmp4j.CommunityTarget;
import org.snmp4j.UserTarget;
import org.snmp4j.smi.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class SNMPUtils {

	public static AbstractTarget createTarget(String ipAddr, String communityString, int retries, long timeout,
			int version) {
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

		return target;
	}

	public static PDU constructPDU(String oid, int pduType) {
		PDU pdu = new PDU();
		pdu.add(new VariableBinding(new OID(oid)));
		pdu.setType(pduType);
		return pdu;
	}

	public static String parseResponse(ResponseEvent<?> response) {
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

	public static String getNextOidFromResponse(String response) {
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

	public static String walk(SNMPManagerFacade manager, String oid, String ipAddr) {
		StringBuilder result = new StringBuilder();
		String nextOid = oid;
		while (nextOid != null) {
			String response = manager.getNext(nextOid, ipAddr);
			if (response.startsWith("Error:")) {
				break;
			}
			result.append(response);
			nextOid = getNextOidFromResponse(response);
			if (nextOid == null || !nextOid.startsWith(oid)) {
				break;
			}
		}
		return result.toString();
	}
}
