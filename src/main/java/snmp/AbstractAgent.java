package snmp;

import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;
import net.percederberg.mibble.*;
import net.percederberg.mibble.snmp.*;

import java.io.File;
import java.io.IOException;

public abstract class AbstractAgent {
	protected String ipAddress;
	protected int snmpVersion;
	protected Mib mib;
	protected CommunityTarget<Address> target;

	public AbstractAgent(String ipAddress, int snmpVersion, String community, String mibFilePath)
			throws IOException, MibLoaderException {
		this.ipAddress = ipAddress;
		this.snmpVersion = snmpVersion;
		this.target = new CommunityTarget<Address>();
		this.target.setAddress(new UdpAddress(ipAddress + "/161"));
		this.target.setCommunity(new OctetString(community));
		this.target.setRetries(2);
		this.target.setTimeout(1500);
		this.target.setVersion(snmpVersion);
		this.mib = loadMib(mibFilePath);
	}

	protected Mib loadMib(String mibFilePath) throws IOException, MibLoaderException {
		MibLoader loader = new MibLoader();
		File mibFile = new File(mibFilePath);
		return loader.load(mibFile);
	}

	public abstract ResponseEvent<Address> get(OID oid, Snmp snmp) throws IOException;

	public abstract ResponseEvent<Address> set(OID oid, Variable variable, Snmp snmp) throws IOException;

	public abstract ResponseEvent<Address> getNext(OID oid, Snmp snmp) throws IOException;

	public abstract String getDescription(OID oid);

	public abstract String getSymbolicName(OID oid);
}
