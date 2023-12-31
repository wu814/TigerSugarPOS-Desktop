import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * This class is a custom input dialog that takes in two inputs.
 * 
 * @author Josh Hare
 */
public class TwoInputDialog extends JDialog{
    // Attributes
    private JTextField textField1; // First input
    private JTextField textField2; // Second input
    private JButton okButton; // OK button
    private TwoInputs inputs; // both inputs


    /**
     * Constructor
     * @param parent the parent frame
     * @param label1 the first label
     * @param label2 the second label
     */
    public TwoInputDialog(Frame parent,String label1, String label2){
        super(parent, "Input Dialog", true);
        setLayout(new FlowLayout());

        textField1 = new JTextField(10);
        textField2 = new JTextField(10);
        okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
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


    /**
     * Showing the input dialog
     * @return the input dialog
     */
    public TwoInputs showInputDialog(){
        setVisible(true);
        return inputs;
    }
}


/**
 * This class is a helper class for the TwoInputDialog class.
 * 
 * @author Josh Hare
 */
class TwoInputs{
    // Attributes
    public String input1;
    public String input2;


    /**
     * Constructor
     * @param input1 the first input
     * @param input2 the second input
     */
    public TwoInputs(String input1, String input2){
        this.input1 = input1;
        this.input2 = input2;
    }
}
