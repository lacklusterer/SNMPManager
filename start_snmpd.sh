#!/bin/sh

# Check the status of snmpd.service
if systemctl is-active --quiet snmpd.service; then
	echo "snmpd.service is running."
else
	echo "snmpd.service is not running. Starting the service..."
	# Start the snmpd.service
	sudo systemctl start snmpd.service

	# Check if the service started successfully
	if systemctl is-active --quiet snmpd.service; then
		echo "snmpd.service started successfully."
	else
		echo "Failed to start snmpd.service."
	fi
fi
