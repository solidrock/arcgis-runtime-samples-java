/*
 * Copyright 2017 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.esri.samples.certificate_authentication;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;

/**
 * Custom dialog for getting an OAuthConfiguration.
 */
class PasswordDialog extends Dialog<String> {

  @FXML private PasswordField passwordField;
  @FXML private ButtonType continueButton;

  PasswordDialog() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/certificate_authentication_password_dialog.fxml"));
    loader.setRoot(this);
    loader.setController(this);

    setTitle("Authenticate");

    try {
      loader.load();
    } catch (Exception e) {
      e.printStackTrace();
    }

    setResultConverter(dialogButton -> {
      if (dialogButton == continueButton) {
        if (!passwordField.getText().equals("")) {
          try {
            return passwordField.getText();
          } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
          }
        } else {
          new Alert(Alert.AlertType.ERROR, "missing password").show();
        }
      }
      return null;
    });
  }
}
