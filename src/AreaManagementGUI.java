import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class AreaManagementGUI {
    private JFrame frame;
    private JTextArea outputTextArea;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JComboBox<String> choiceComboBox;

    private static final String FILE_PATH = "directory.txt";

    public AreaManagementGUI() {
        createGUI();
    }

    private void createGUI() {
        frame = new JFrame("Area Management");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        topPanel.add(usernameLabel);
        topPanel.add(usernameField);
        topPanel.add(passwordLabel);
        topPanel.add(passwordField);
        topPanel.add(loginButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputTextArea);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JLabel choiceLabel = new JLabel("Choose an option:");
        String[] choices = {"Display All Places", "Search Place by Name", "Add a Place", "Delete a Place", "Contact a Place", "Exit"};
        choiceComboBox = new JComboBox<>(choices);
        JButton executeButton = new JButton("Execute");
        bottomPanel.add(choiceLabel);
        bottomPanel.add(choiceComboBox);
        bottomPanel.add(executeButton);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (username.equals("admin") && password.equals("password")) {
                    outputTextArea.append("Login successful!\n");
                    usernameField.setEnabled(false);
                    passwordField.setEnabled(false);
                    loginButton.setEnabled(false);
                    choiceComboBox.setEnabled(true);
                    executeButton.setEnabled(true);
                } else {
                    outputTextArea.append("Invalid username or password. Please try again.\n");
                }
            }
        });

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = choiceComboBox.getSelectedIndex();

                switch (choice) {
                    case 0:
                        displayAllPlaces();
                        break;
                    case 1:
                        String searchName = JOptionPane.showInputDialog(frame, "Enter place name to search:");
                        if (searchName != null && !searchName.isEmpty()) {
                            searchPlaceByName(searchName);
                        } else {
                            outputTextArea.append("Invalid place name to search.\n");
                        }
                        break;
                    case 2:
                        String newPlaceName = JOptionPane.showInputDialog(frame, "Enter new place name:");
                        if (newPlaceName != null && !newPlaceName.isEmpty()) {
                            String newPlaceAddress = JOptionPane.showInputDialog(frame, "Enter new place address:");
                            if (newPlaceAddress != null && !newPlaceAddress.isEmpty()) {
                                String newPlacePhoneNumber = JOptionPane.showInputDialog(frame, "Enter administration contact number:");
                                if (newPlacePhoneNumber != null && !newPlacePhoneNumber.isEmpty()) {
                                    String[] categories = {"Educational", "Restaurant", "Office", "Services", "Healthcare", "Home", "Others"};
                                    String selectedCategory = (String) JOptionPane.showInputDialog(frame, "Select category:", "Add a Place",
                                            JOptionPane.QUESTION_MESSAGE, null, categories, categories[0]);

                                    if (selectedCategory != null && !selectedCategory.isEmpty()) {
                                        addPlace(newPlaceName + " - " + newPlaceAddress + " - " + newPlacePhoneNumber + " - " + selectedCategory);
                                        outputTextArea.append("Place added: " + newPlaceName + "\n");
                                    } else {
                                        outputTextArea.append("Invalid category.\n");
                                    }
                                } else {
                                    outputTextArea.append("Invalid administration contact number.\n");
                                }
                            } else {
                                outputTextArea.append("Invalid place address.\n");
                            }
                        } else {
                            outputTextArea.append("Invalid place name.\n");
                        }
                        break;
                    case 3:
                        String deletePlaceName = JOptionPane.showInputDialog(frame, "Enter place name to delete:");
                        if (deletePlaceName != null && !deletePlaceName.isEmpty()) {
                            deletePlace(deletePlaceName);
                        } else {
                            outputTextArea.append("Invalid place name to delete.\n");
                        }
                        break;
                    case 4:
                        String contactPlace = JOptionPane.showInputDialog(frame, "Enter place to contact:");
                        if (contactPlace != null && !contactPlace.isEmpty()) {
                            contactPlace(contactPlace);
                        } else {
                            outputTextArea.append("Invalid place to contact.\n");
                        }
                        break;
                    case 5:
                        exitProgram();
                        break;
                    default:
                        break;
                }
            }
        });

        choiceComboBox.setEnabled(false);
        executeButton.setEnabled(false);

        frame.setVisible(true);
    }

    private void displayAllPlaces() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            outputTextArea.setText(""); // Clear the output area
            boolean headerShown = false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                String placeName = parts[0];
                String address = parts[1];
                String phoneNumber = parts[2];
                String category = parts[3];

                if (!headerShown) {
                    outputTextArea.append(String.format("%-20s %-50s %-20s %-15s\n", "Place", "Address", "Phone Number", "Category"));
                    outputTextArea.append("-------------------------------------------------------------------------------------------------------------------\n");
                    headerShown = true;
                }

                outputTextArea.append(String.format("%-20s %-50s %-20s %-15s\n", placeName, address, phoneNumber, category));
            }
        } catch (IOException e) {
            outputTextArea.append("Error reading places from file.\n");
        }
    }

    private void searchPlaceByName(String name) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            outputTextArea.setText(""); // Clear the output area
            boolean found = false;
            outputTextArea.append(String.format("%-20s %-50s %-20s %-15s\n", "Place", "Address", "Phone Number", "Category"));
            outputTextArea.append("-------------------------------------------------------------------------------------------------------------------\n");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                String placeName = parts[0];
                String address = parts[1];
                String phoneNumber = parts[2];
                String category = parts[3];
                if (placeName.equalsIgnoreCase(name)) {
                    outputTextArea.append(String.format("%-20s %-50s %-20s %-15s\n", placeName, address, phoneNumber, category));
                    found = true;
                }
            }
            if (!found) {
                outputTextArea.append("Place not found: " + name + "\n");
            }
        } catch (IOException e) {
            outputTextArea.append("Error reading places from file.\n");
        }
    }

    private void addPlace(String place) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(place);
            writer.newLine();
        } catch (IOException e) {
            outputTextArea.append("Error adding place to file.\n");
        }
    }

    private void deletePlace(String name) {
        try {
            File inputFile = new File(FILE_PATH);
            File tempFile = new File("temp.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;
            boolean found = false;

            while ((currentLine = reader.readLine()) != null) {
                String[] parts = currentLine.split(" - ");
                String placeName = parts[0];
                if (placeName.equalsIgnoreCase(name)) {
                    found = true;
                } else {
                    writer.write(currentLine + System.getProperty("line.separator"));
                }
            }

            writer.close();
            reader.close();

            if (found) {
                if (inputFile.delete()) {
                    tempFile.renameTo(inputFile);
                    outputTextArea.append("Place deleted: " + name + "\n");
                } else {
                    outputTextArea.append("Error deleting place.\n");
                }
            } else {
                outputTextArea.append("Place not found: " + name + "\n");
            }
        } catch (IOException e) {
            outputTextArea.append("Error deleting place.\n");
        }
    }

    private void contactPlace(String place) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                String placeName = parts[0];
                if (placeName.equalsIgnoreCase(place)) {
                    outputTextArea.append("Connecting to " + placeName + "...\n");
                    found = true;
                    break;
                }
            }
            if (!found) {
                outputTextArea.append("Place not found: " + place + "\n");
            }
        } catch (IOException e) {
            outputTextArea.append("Error reading places from file.\n");
        }
    }

    private void exitProgram() {
        frame.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AreaManagementGUI();
            }
        });
    }
}
