[class diagram](https://drive.google.com/file/d/18bs7_Xo0Qfw2SHyhk0s_6W5daaPKrk3D/view?usp=sharing) 

## Dependencies:
- maven
- net-snmp (for testing only)

## Usage:
Compile source code:

```
mvn clean install
```

Run the app:

```
mvn exec:java
```

## Testing:
1. Configure snmp agent on localhost with the following script:

```bash
# Create the directory for snmp configuration files
sudo mkdir -p /etc/snmp

# Create and edit snmpd.conf
sudo tee /etc/snmp/snmpd.conf >/dev/null <<EOF
rocommunity killme
trapsink localhost killme
EOF

# Create and edit snmptrapd.conf
sudo tee /etc/snmp/snmptrapd.conf >/dev/null <<EOF
authCommunity log,execute,net killme
EOF
```

2. Start services:

```
# service snmpd restart
# service snmptrapd restart
```

3. Send an example get request:

```
snmpget -v2c -c killme localhost 1.3.6.1.2.1.1.1.0
```

Confirm that the java application return the same value

4. Tail system log of `snmptrapd.service` for trap listener:

```
# journalctl -u snmptrapd -f
```

5. Send an example trap:

```
snmptrap -v 2c -c killme localhost '' SNMPv2-MIB::coldStart
```

Confirm that the java application return the same value
