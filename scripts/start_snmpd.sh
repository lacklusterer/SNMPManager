#!/bin/sh

if systemctl is-active --quiet snmpd.service; then
	echo "snmpd.service is running."
else
	echo "snmpd.service is not running. Starting the service..."
	sudo systemctl start snmpd.service

	if systemctl is-active --quiet snmpd.service; then
		echo "snmpd.service started successfully."
	else
		echo "Failed to start snmpd.service."
	fi
fi
