/*
 * Copyright 2019 Esri.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.esri.samples.subnetwork_trace;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;

import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.ColorUtil;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;
import com.esri.arcgisruntime.symbology.SimpleRenderer;
import com.esri.arcgisruntime.utilitynetworks.UtilityElement;
import com.esri.arcgisruntime.utilitynetworks.UtilityNetwork;
import com.esri.arcgisruntime.utilitynetworks.UtilityTraceParameters;

public class SubnetworkTraceController {

  @FXML
  private Button resetButton;
  @FXML
  private Button traceButton;
  @FXML
  private Label statusLabel;
  @FXML
  private MapView mapView;
  @FXML
  private ProgressIndicator progressIndicator;

  private ArrayList<UtilityElement> barriers;
  private ArrayList<UtilityElement> startingLocations;
  private GraphicsOverlay graphicsOverlay;
  private SimpleMarkerSymbol barrierPointSymbol;
  private SimpleMarkerSymbol startingPointSymbol;
  private UtilityNetwork utilityNetwork;
  private UtilityTraceParameters utilityTraceParameters;

  public void initialize() {
    try {
      progressIndicator = new ProgressIndicator();
      progressIndicator.setVisible(true);
      statusLabel.setText("Loading Utility Network...");

      // create a basemap and set it to the map view
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsNightVector());
      mapView.setMap(map);
      mapView.setViewpointAsync(new Viewpoint(
        new Envelope(-9813547.35557238, 5129980.36635111, -9813185.0602376, 5130215.41254146,
          SpatialReferences.getWebMercator())));

      // create symbols for the starting point and barriers
      startingPointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CROSS, ColorUtil.colorToArgb(Color.GREEN), 20);
      barrierPointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.X, ColorUtil.colorToArgb(Color.RED), 20);

      // load the utility network data from the feature service and create feature layers
      String featureServiceURL =
        "https://sampleserver7.arcgisonline.com/arcgis/rest/services/UtilityNetwork/NapervilleElectric/FeatureServer";

      ServiceFeatureTable distributionLineFeatureTable = new ServiceFeatureTable(featureServiceURL + "/115");
      FeatureLayer distributionLineLayer = new FeatureLayer(distributionLineFeatureTable);

      ServiceFeatureTable electricDeviceFeatureTable = new ServiceFeatureTable(featureServiceURL + "/100");
      FeatureLayer electricDeviceLayer = new FeatureLayer(electricDeviceFeatureTable);

      // create and apply a renderer for the electric distribution lines feature layer
      distributionLineLayer.setRenderer(new SimpleRenderer(
        new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, ColorUtil.colorToArgb(Color.DARKCYAN), 3)));

      // add the feature layers to the map
      map.getOperationalLayers().addAll(Arrays.asList(distributionLineLayer, electricDeviceLayer));

      // set the map view's selection color
      mapView.getSelectionProperties().setColor(0xFFFFFF00);

      // create and load the utility network
      utilityNetwork = new UtilityNetwork(featureServiceURL, map);
      utilityNetwork.loadAsync();
      utilityNetwork.addDoneLoadingListener(() -> {

        if (utilityNetwork.getLoadStatus() == LoadStatus.LOADED) {


          // enable the UI
          enableUI();

          // update the status text
          statusLabel.setText("Click on the network lines or points to add a utility element.");

        } else {
          new Alert(Alert.AlertType.ERROR, "Error loading Utility Network.").show();
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void enableUI() {
  }

  @FXML
  private void handleTraceClick() {

    progressIndicator.setVisible(false);
  }

  @FXML
  private void handleResetClick() {

  }

  /**
   * Stops and releases all resources used in application.
   */
  public void terminate() {

    if (mapView != null) {
      mapView.dispose();
    }
  }
}
