package com.example.javafx_test.opc_ua;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import org.eclipse.milo.opcua.stack.core.UaException;

import java.util.concurrent.ExecutionException;

public class Login_Controller {

    @FXML
    private Text text_confirmar_envio;

    @FXML
    private TextField text_peca;

    @FXML
    private TextField text_peca_ware;

    @FXML
    private TextField text_peca_ware1;

    OpcUa n = OpcUa.getInstance();

    public Login_Controller() throws Exception {
    }

    @FXML
    void make_peca(ActionEvent event) throws UaException, ExecutionException, InterruptedException {
        n.mandarFazerPeca(1,6,1);
        //System.out.println(n.ReadOneVar(n.Gvl_pedir_peca_Ware).getClass());

    }

}
