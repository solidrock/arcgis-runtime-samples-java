/*
 * Copyright 2019 Esri.
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

package com.esri.samples.portal.certificate_authentication;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.AuthenticationChallenge;
import com.esri.arcgisruntime.security.AuthenticationChallengeHandler;
import com.esri.arcgisruntime.security.AuthenticationChallengeResponse;
import com.esri.arcgisruntime.security.AuthenticationManager;
import com.esri.arcgisruntime.security.CertificateCredential;

public class CertificateAuthenticationController {

  @FXML
  private TextField portalUrlTextField;
  @FXML
  private Label portalStatusLabel;
  @FXML
  private TextField certificatePathTextField;
  @FXML
  private MapView mapView;

  private FileChooser fileChooser;
  private CertificateCredential certificateCredential;

  @FXML
  public void initialize() {
    try {

      // create a map view and add an imagery basemap
      mapView = new MapView();
      ArcGISMap map = new ArcGISMap(Basemap.createImageryWithLabels());
      mapView.setMap(map);

      // create a file chooser for opening certificate files
      fileChooser = new FileChooser();
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PKCS#12", "*.pfx"));
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PKCS#12", "*.p12"));

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Uses the provided Certificate path and password to create a Credential and connect to the requested Portal.
   */
  @FXML
  private void handleAuthenticateClick() {
    // store the portal url for later reference.
    String portalUrl;

    if (!portalUrlTextField.getText().equals("") && !certificatePathTextField.getText().equals("")) {
      // show portal url in UI
      portalUrl = portalUrlTextField.getText();
      // prompt for certificate path
      String certificatePath = fileChooser.showOpenDialog(Stage.getWindows().get(0)).getAbsolutePath();
      certificatePathTextField.setText(certificatePath);

      // prompt for certificate password
      PasswordDialog passwordDialog = new PasswordDialog();
      passwordDialog.show();
      passwordDialog.setOnCloseRequest(r -> {
        String certificatePassword = passwordDialog.getResult();

        // create a Certificate Credential
        certificateCredential = new CertificateCredential(certificatePath, certificatePassword);

        // configure the authentication challenge handler
        AuthenticationManager.setAuthenticationChallengeHandler(new CustomChallengeHandler());
        AuthenticationManager.setTrustAllSigners(true);

        // create and load the portal
        Portal portal = new Portal(portalUrl, true);
        portal.loadAsync();
        portal.addDoneLoadingListener(() -> {
          if (portal.getLoadStatus() == LoadStatus.LOADED) {

            // update the UI with the logged in user
            portalStatusLabel.setText("Logged in to portal as user: " + portal.getUser().getFullName());

          } else {
            new Alert(Alert.AlertType.ERROR, "Failed to load Portal: " + portal.getLoadError().getCause().getMessage()).show();
          }
        });
      });
    } else if (portalUrlTextField.getText().equals("")) {
      new Alert(Alert.AlertType.ERROR, "No Portal URL provided.").show();
    } else if (certificatePathTextField.getText().equals("")) {
      new Alert(Alert.AlertType.ERROR, "No certificate provided.").show();
    }
  }

  /**
   * Handler to be used when accessing a secured resource.
   */
  class CustomChallengeHandler implements AuthenticationChallengeHandler {

    @Override
    public AuthenticationChallengeResponse handleChallenge(AuthenticationChallenge authenticationChallenge) {
      try {
        if (authenticationChallenge.getType() == AuthenticationChallenge.Type.CERTIFICATE_CHALLENGE) {
          // Clear OAuth configurations before using normal credential

          return new AuthenticationChallengeResponse(
                  AuthenticationChallengeResponse.Action.CONTINUE_WITH_CREDENTIAL, certificateCredential);
        }
        return null;

      } catch (Exception e) {
        return new AuthenticationChallengeResponse(AuthenticationChallengeResponse.Action.CANCEL, null);
      }
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}


