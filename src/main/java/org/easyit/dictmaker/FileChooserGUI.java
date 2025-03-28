package org.easyit.dictmaker;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileChooserGUI extends JFrame {

    private JTextField folderPathField;
    private JTextField filePathField;

    public FileChooserGUI() {
        // 设置窗口标题
        setTitle("文件选择器示例");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 200);
        setLocationRelativeTo(null); // 居中显示

        // 创建主面板
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. 文件夹选择器部分
        JPanel folderPanel = new JPanel(new BorderLayout(5, 5));
        JLabel folderLabel = new JLabel("Qwerty Home:");
        folderPathField = new JTextField();
        folderPathField.setEditable(false);
        JButton folderButton = new JButton("选择文件夹");

        folderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(FileChooserGUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    folderPathField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        folderPanel.add(folderLabel, BorderLayout.WEST);
        folderPanel.add(folderPathField, BorderLayout.CENTER);
        folderPanel.add(folderButton, BorderLayout.EAST);

        // 2. 文件选择器部分（限制为txt格式）
        JPanel filePanel = new JPanel(new BorderLayout(5, 5));
        JLabel fileLabel = new JLabel("选择文本:");
        filePathField = new JTextField();
        filePathField.setEditable(false);
        JButton fileButton = new JButton("选择文件");

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("文本文件 (*.txt)", "txt");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(FileChooserGUI.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        filePanel.add(fileLabel, BorderLayout.WEST);
        filePanel.add(filePathField, BorderLayout.CENTER);
        filePanel.add(fileButton, BorderLayout.EAST);

        // 3. 按钮部分
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton actionButton = new JButton("执行操作");

        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String folderPath = folderPathField.getText();
                String filePath = filePathField.getText();

                if (folderPath.isEmpty() || filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(
                        FileChooserGUI.this,
                        "请先选择文件夹和文件", "警告", JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                String outputPath = Maker.makeDict(folderPath, filePath);

                // 这里可以添加按钮点击后的操作
                JOptionPane.showMessageDialog(
                    FileChooserGUI.this,
                    "文件夹: " + folderPath + "\n文件: " + filePath + "\n写入文件: " + outputPath,
                    "结果", JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        buttonPanel.add(actionButton);

        // 将所有面板添加到主面板
        mainPanel.add(folderPanel);
        mainPanel.add(filePanel);
        mainPanel.add(buttonPanel);

        // 添加主面板到窗口
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileChooserGUI().setVisible(true);
            }
        });
    }
}
