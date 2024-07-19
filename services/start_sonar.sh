#!/bin/sh

if systemctl is-active --quiet sonarqube.service; then
	echo "sonarqube.service is running."
else
	echo "sonarqube.service is not running. Starting the service..."
	sudo systemctl start sonarqube.service

	if systemctl is-active --quiet sonarqube.service; then
		echo "sonarqube.service started successfully."
	else
		echo "Failed to start sonarqube.service."
	fi
fi
