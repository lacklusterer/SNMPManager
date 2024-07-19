#!/bin/sh

if systemctl is-active --quiet jenkins.service; then
	echo "jenkins.service is running."
else
	echo "jenkins.service is not running. Starting the service..."
	sudo systemctl start jenkins.service

	if systemctl is-active --quiet jenkins.service; then
		echo "jenkins.service started successfully."
	else
		echo "Failed to start jenkins.service."
	fi
fi
