import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


//creates a two input dialog pop up
public class TwoInputDialog extends JDialog {
    private JTextField textField1;
    private JTextField textField2;
    private JButton okButton;
    private TwoInputs inputs;

    public TwoInputDialog(Frame parent,String label1, String label2) {
        super(parent, "Input Dialog", true);
        setLayout(new FlowLayout());

        textField1 = new JTextField(20);
        textField2 = new JTextField(20);
        okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputs = new TwoInputs(textField1.getText(), textField2.getText());
                dispose();
            }
        });

        add(new JLabel(label1));
        add(textField1);
        add(new JLabel(label2));
        add(textField2);
        add(okButton);

        pack();
        setLocationRelativeTo(parent);
    }

    public TwoInputs showInputDialog() {
        setVisible(true);
        return inputs;
    }
}

class TwoInputs {
    public String input1;
    public String input2;

    public TwoInputs(String input1, String input2) {
        this.input1 = input1;
        this.input2 = input2;
    }
}
