package hust.soict.proj1.frontend;

import java.util.Map;
import java.util.Scanner;

import hust.soict.proj1.snmpmanager.SNMPManagerFacade;

public class SNMPCLI {

	private static final Scanner scanner = new Scanner(System.in);
	private static final SNMPManagerFacade facade = new SNMPManagerFacade();

	public static void main(String[] args) {
		boolean running = true;
		while (running) {
			printMenu();
			int choice = Integer.parseInt(scanner.nextLine());

			switch (choice) {
				case 1:
					createTarget();
					break;
				case 2:
					listManagedDevices();
					break;
				case 3:
					makeRequest();
					break;
				case 4:
					running = false;
					break;
				default:
					System.out.println("Invalid choice, please try again.");
			}
		}
		facade.closeSnmp();
	}

	private static void printMenu() {
		System.out.println("\nSNMP Manager CLI");
		System.out.println("1. Create Target");
		System.out.println("2. List Managed Devices");
		System.out.println("3. Make Request");
		System.out.println("4. Exit");
		System.out.print("Select and option : ");
	}

	private static void createTarget() {
		System.out.print("Enter IP Address (default: localhost/161): ");
		String ipAddr = scanner.nextLine();
		if (ipAddr.isEmpty())
			ipAddr = "localhost/161";

		System.out.print("Enter Community String (default: public): ");
		String communityString = scanner.nextLine();
		if (communityString.isEmpty())
			communityString = "public";

		System.out.print("Enter Retries (default: 2): ");
		String retriesInput = scanner.nextLine();
		int retries = retriesInput.isEmpty() ? 2 : Integer.parseInt(retriesInput);

		System.out.print("Enter Timeout in ms (default: 15000): ");
		String timeoutInput = scanner.nextLine();
		long timeout = timeoutInput.isEmpty() ? 15000 : Long.parseLong(timeoutInput);

		System.out.print("Enter SNMP Version (1, 2, 3) (default: 2): ");
		String versionInput = scanner.nextLine();
		int version = versionInput.isEmpty() ? 2 : Integer.parseInt(versionInput);

		facade.createTarget(ipAddr, communityString, retries, timeout, version);
		System.out.println("Target created successfully.");
	}

	private static void listManagedDevices() {
		Map<String, Integer> devices = facade.getManagedDevices();
		if (devices.isEmpty()) {
			System.out.println("\nNo managed devices.");
		} else {
			System.out.println("\nManaged Devices:");
			int index = 1;
			for (Map.Entry<String, Integer> entry : devices.entrySet()) {
				System.out.println(index + ". IP Address: " + entry.getKey() + ", SNMP Version: " + entry.getValue());
				index++;
			}
		}
	}

	private static void makeRequest() {
		listManagedDevices();
		Map<String, Integer> devices = facade.getManagedDevices();
		if (devices.isEmpty()) {
			return;
		}

		int index = 1;
		String[] ipArray = new String[devices.size()];
		for (String ip : devices.keySet()) {
			ipArray[index - 1] = ip;
			index++;
		}

		System.out.print("Select a device : ");
		int targetIndex = Integer.parseInt(scanner.nextLine()) - 1;
		if (targetIndex < 0 || targetIndex >= ipArray.length) {
			System.out.println("Invalid input.");
			return;
		}

		String ipAddr = ipArray[targetIndex];

		System.out.println("\nCurrent selected device: " + ipAddr);
	}
}