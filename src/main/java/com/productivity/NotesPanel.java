package com.productivity;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.nio.file.Files;
import java.util.Scanner;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class NotesPanel extends JDesktopPane {
	
	private static final File kNamesFile = new File(Productivity.getInstance().getCurrentPath() + "Notes\\names.TXT");
	private static final String kFilePath = Productivity.getInstance().getCurrentPath() + "Notes\\";
	private static final int kTextLimit = 10;
	
	public NotesPanel() {
		String[] names = readData(kNamesFile);
		
		JComboBox<String> noteChoose = new JComboBox<>(names);
		noteChoose.setFocusable(false);
		
		JTextField nameField = new JTextField();
		nameField.setDocument(new JTextFieldLimit(kTextLimit));
		nameField.addActionListener(e -> {
			String text  = nameField.getText();
			if (!testValidFileName(text)) {
				nameField.setText((String)noteChoose.getSelectedItem());
				return;
			}
			File file = new File(kFilePath + (String)noteChoose.getSelectedItem() + ".txt");
			File newFile = new File(kFilePath + text + ".txt");
			file.renameTo(newFile);
			int index = noteChoose.getSelectedIndex();
			names[index] = text;
			writeData(names, kNamesFile);
			noteChoose.setModel(new JComboBox<>(names).getModel());
			noteChoose.setSelectedIndex(index);
		});
		
		JTextArea textArea = new JTextArea();
		textArea.addCaretListener(e -> {
			File file = new File(kFilePath + (String)noteChoose.getSelectedItem() + ".txt");
			String[] data = textArea.getText().split("\\r?\\n");
			writeData(data, file);
		});
		
		noteChoose.addActionListener(e -> {
			File file = new File(kFilePath + (String)noteChoose.getSelectedItem() + ".txt");
			String[] data;
			try {
				data = readData(file);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Error when loading file");
				file.delete();
				try {
					file.createNewFile();
				} catch (IOException e1) { e1.printStackTrace(); }
				return;
			}
			if (data.length > 0) {
				textArea.setText(data[0] + "\n");
				for (int i = 1; i < data.length; i++) {
					textArea.append(data[i] + "\n");
				}
			}
			else {
				textArea.setText("");
			}
			nameField.setText((String)noteChoose.getSelectedItem());
		});
		
		File file = new File(kFilePath + (String)noteChoose.getSelectedItem() + ".txt");
		String[] data;
		try {
			data = readData(file);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error when loading file");
			file.delete();
			try {
				file.createNewFile();
			} catch (IOException e1) { e1.printStackTrace(); }
			return;
		}
		if (data.length > 0) {
			textArea.setText(data[0] + "\n");
			for (int i = 1; i < data.length; i++) {
				textArea.append(data[i] + "\n");
			}
		}
		else {
			textArea.setText("");
		}
		nameField.setText((String)noteChoose.getSelectedItem());
		
		super.setLayout(new BorderLayout());
		Box top = Box.createHorizontalBox();
		top.add(noteChoose);
		top.add(nameField);
		super.add(BorderLayout.NORTH, top);
		super.add(BorderLayout.CENTER, textArea);
	}
	
	private boolean testValidFileName(String text) {
		return text.matches("^[a-zA-Z0-9._ ]+$");
	}
	
	private static String[] readData(File file) {
		String[] result = new String[0];
		try {
			result = new String[(int)Files.lines(file.toPath()).count()];
			Scanner scanner = new Scanner(file);
			int index = 0;
			while (scanner.hasNextLine()) {
				result[index] = scanner.nextLine();
				index++;
			}
			scanner.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	private static void writeData(String data, File file) {
		try  {
			FileWriter writer = new FileWriter(file);
			writer.write(data);
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void writeData(String[] dataArr, File file) {
		String data = "";
		for (int i = 0; i < dataArr.length; i++) {
			data += (dataArr[i] + "\n");
		}
		writeData(data, file);
	}
}