#!/bin/sh

mvn clean verify sonar:sonar \
	-Dsonar.projectKey=snmp-manager \
	-Dsonar.projectName='snmp-manager' \
	-Dsonar.host.url=http://localhost:9000 \
	-Dsonar.token=sqp_c35ffe03b617656b9af65d6c6ea3ca2a4f6f9186
