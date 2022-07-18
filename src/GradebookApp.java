import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GradebookApp {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Gradebook Manager");
        JPanel panel = new JPanel(new BorderLayout());
        frame.add(panel);

        Gradebook gradebook = new Gradebook();

        DefaultTableModel model = new DefaultTableModel(new Object[] {"ID", "Name", "Grade"},0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0 ;//&& column != 2; <- if assignment rows are editable grade column should not be...
            }
        };

        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane);


        TextField nameField = new TextField("Enter student's name");
        panel.add(nameField, BorderLayout.SOUTH);
        Button addStudentButton = new Button("Add Student");

        JPanel buttonPanel = new JPanel(new GridLayout(2,2));
        panel.add(buttonPanel, BorderLayout.EAST);
        buttonPanel.add(addStudentButton);

        addStudentButton.addActionListener(e -> {
            String name = nameField.getText();
            if (name.isBlank() || name.startsWith("Deleted")){
                nameField.setText("Enter student's name");
            } else if (name.equals("Enter student's name")) {
                //do nothing
            } else {
                int ID;
                if (gradebook.isEmpty()){
                    ID = 1;
                } else {
                    ID = Integer.parseInt(model.getValueAt(model.getRowCount() - 1, 0).toString()) + 1;
                }
                Student student = new Student(ID, nameField.getText());
                if (gradebook.addStudent(student)){
                    model.addRow(new Object[]{student.getId(), student.getName(), gradebook.get(student)});
                    nameField.setText("");
                }
            }
        });


        Button deleteStudentButton = new Button("Delete Selected Students");
        deleteStudentButton.addActionListener(e -> {
            int[] rows = table.getSelectedRows();
            for (int i = rows.length - 1; i >= 0; i--) {
                gradebook.deleteStudent(Integer.parseInt(model.getValueAt(rows[i], 0).toString()));
                model.removeRow(rows[i]);
            }
            nameField.setText("Deleted " + rows.length + " students");
        });
        buttonPanel.add(deleteStudentButton);

        Button saveGradebookButton = new Button("Save Gradebook");
        saveGradebookButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setSelectedFile(new File("Untitled Gradebook.txt"));
            if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String filename = chooser.getSelectedFile().toString();
                if (!filename.endsWith(".txt")) {
                    filename += ".txt";
                }
                try(FileWriter fw = new FileWriter(filename)) {
                    fw.write(gradebook.printGrades(new IDComparator()));
                } catch (IOException ioException) {
                    nameField.setText("File not saved, try again with another name.");
                }
            }
        });
        buttonPanel.add(saveGradebookButton);

        Button loadGradebookButton = new Button("Load Gradebook");
        loadGradebookButton.addActionListener(e -> {
            String[] fileContents;
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter( new FileNameExtensionFilter(".txt", "txt"));
            if (chooser.showOpenDialog(null) == JFileChooser.OPEN_DIALOG) {
                try (Scanner scanner = new Scanner(chooser.getSelectedFile())) {
                    model.setRowCount(0);
                    model.setColumnCount(3);
                    gradebook.clear();
                    while (scanner.hasNextLine()) {
                        fileContents = scanner.nextLine().split("\\.");
                        model.addRow(fileContents);
                        gradebook.addStudent(new Student(Integer.parseInt(fileContents[0]), fileContents[1]), fileContents[2].charAt(0));
                    }
                } catch (FileNotFoundException notFoundException) {
                    nameField.setText("File not found");
                }
            }
        });
        buttonPanel.add(loadGradebookButton);


        TableModelListener modelListener = e -> {
            if (e.getType() == TableModelEvent.UPDATE) {

                if (e.getColumn() == 1) {
                    gradebook.updateStudent(Integer.parseInt(model.getValueAt(e.getFirstRow(), 0).toString()), model.getValueAt(e.getFirstRow(), 1).toString());
                } else if (e.getColumn() == 2) {
                    if (table.getValueAt(e.getFirstRow(), e.getColumn()).toString().length() > 1 || !Gradebook.isValidGrade(table.getValueAt(e.getFirstRow(), e.getColumn()).toString().charAt(0))){
                        model.setValueAt(gradebook.get(new Student(Integer.parseInt(model.getValueAt(e.getFirstRow(), 0).toString()))), e.getFirstRow(), e.getColumn()); // if the new grade is invalid, replace with current grade in gradebook
                    } else {
                        gradebook.updateGrade(Integer.parseInt(model.getValueAt(e.getFirstRow(), 0).toString()), table.getValueAt(e.getFirstRow(), e.getColumn()).toString().charAt(0));
                    }
                }

            }
        };

        model.addTableModelListener(modelListener);

        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
