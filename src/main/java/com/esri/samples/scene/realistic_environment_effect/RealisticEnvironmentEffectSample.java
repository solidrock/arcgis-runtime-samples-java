/*
 * Copyright 2018 Esri.
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

package com.esri.samples.scene.realistic_environment_effect;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISSceneLayer;
import com.esri.arcgisruntime.mapping.view.LightingMode;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import com.esri.arcgisruntime.mapping.ArcGISScene;
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Surface;
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect;
import com.esri.arcgisruntime.mapping.view.SceneView;
import com.esri.arcgisruntime.mapping.view.Camera;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class RealisticEnvironmentEffectSample extends Application {

  private SceneView sceneView;

  @Override
  public void start(Stage stage) {

    try {
      // create stack pane and JavaFX app scene
      StackPane stackPane = new StackPane();
      Scene fxScene = new Scene(stackPane);
      fxScene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

      // set title, size, and add JavaFX scene to stage
      stage.setTitle("Realistic Environmental Atmosphere Sample");
      stage.setWidth(800);
      stage.setHeight(700);
      stage.setScene(fxScene);
      stage.show();

      // create a scene and add a basemap to it
      ArcGISScene scene = new ArcGISScene();
      scene.setBasemap(Basemap.createImagery());

      // set the scene to a scene view
      sceneView = new SceneView();
      sceneView.setArcGISScene(scene);

      // add base surface for elevation data
      Surface surface = new Surface();
      ArcGISTiledElevationSource elevationSource = new ArcGISTiledElevationSource( "http://elevation3d.arcgis" +
              ".com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer");
      surface.getElevationSources().add(elevationSource);
      scene.setBaseSurface(surface);

      // add a scene layer
      // final String buildings = "http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Boston/SceneServer/layers/0";
      final String buildings = "http://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Buildings_Brest/SceneServer/layers/0";
      ArcGISSceneLayer sceneLayer = new ArcGISSceneLayer(buildings);
      scene.getOperationalLayers().add(sceneLayer);


      // add a camera and initial camera position
      // for Boston Camera camera = new Camera(42.351984, -71.073553, 150.0, 70, 80, 0.0);
      Camera camera = new Camera(48.37, -4.50, 1000.0, 10.0, 70, 0.0);

      sceneView.setViewpointCamera(camera);

      // set atmosphere effect to realistic
      //sceneView.setAtmosphereEffect(AtmosphereEffect.REALISTIC);

      // set the calendar to show shadows in the morning in Boston

      //TimeZone timeZone = TimeZone.getTimeZone("EST");
//
//
      Calendar calendar = new GregorianCalendar();
//      calendar.setTimeZone(timeZone);
      // for boston calendar.set(2018, 6, 30, 15, 44);
      calendar.set(2018, 6, 30, 12, 00);
//
      System.out.println("The time is " + calendar.getTime());
      sceneView.setSunTime(calendar);


      // create a control panel
      VBox controlsVBox = new VBox(6);
      controlsVBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("rgba(0, 0, 0, 0.3)"),
              CornerRadii.EMPTY, Insets.EMPTY)));
      controlsVBox.setPadding(new Insets(10.0));
      controlsVBox.setMaxSize(260, 110);
      controlsVBox.getStyleClass().add("panel-region");

      // create buttons to set each atmosphere effect
      Button noSunButton = new Button("No sun light effect");
      Button sunOnlyButton = new Button ("Sun light only");
      Button sunAndShadowsButton = new Button ("Sun light with shadows");
      noSunButton.setMaxWidth(Double.MAX_VALUE);
      sunOnlyButton.setMaxWidth(Double.MAX_VALUE);
      sunAndShadowsButton.setMaxWidth(Double.MAX_VALUE);

      noSunButton.setOnAction(event -> sceneView.setSunLighting(LightingMode.NO_LIGHT));
      sunOnlyButton.setOnAction(event -> sceneView.setSunLighting(LightingMode.LIGHT));
      sunAndShadowsButton.setOnAction(event -> sceneView.setSunLighting(LightingMode.LIGHT_AND_SHADOWS));

      Button noAtmosphereButton = new Button("No Atmosphere Effect");
      Button realisticAtmosphereButton = new Button ("Realistic Atmosphere Effect");
      Button horizonAtmosphereButton = new Button ("Horizon Only Atmosphere Effect");
      noAtmosphereButton.setMaxWidth(Double.MAX_VALUE);
      realisticAtmosphereButton.setMaxWidth(Double.MAX_VALUE);
      horizonAtmosphereButton.setMaxWidth(Double.MAX_VALUE);

      noAtmosphereButton.setOnAction(event -> sceneView.setAtmosphereEffect(AtmosphereEffect.NONE));
      realisticAtmosphereButton.setOnAction(event -> sceneView.setAtmosphereEffect(AtmosphereEffect.REALISTIC));
      horizonAtmosphereButton.setOnAction(event -> sceneView.setAtmosphereEffect(AtmosphereEffect.HORIZON_ONLY));

      // add buttons to the control panel
      controlsVBox.getChildren().addAll(noSunButton, sunOnlyButton, sunAndShadowsButton, noAtmosphereButton, realisticAtmosphereButton, horizonAtmosphereButton);

      // add scene view and control panel to the stack pane
      stackPane.getChildren().addAll(sceneView, controlsVBox);
      StackPane.setAlignment(controlsVBox, Pos.TOP_RIGHT);
      StackPane.setMargin(controlsVBox, new Insets(10, 0, 0, 10));



      sceneView.setOnMouseClicked(event -> {

        Point2D pointClicked = new Point2D(event.getX(), event.getY());

        ListenableFuture<Point> identifyPoint = sceneView.screenToLocationAsync(pointClicked);

        identifyPoint.addDoneListener(() -> {

          try {
            System.out.println(identifyPoint.get().toString());
          } catch (InterruptedException e) {
            e.printStackTrace();
          } catch (ExecutionException e) {
            e.printStackTrace();
          }

        });

      });

    } catch (Exception e) {
      // on any error, display the stack trace.
      e.printStackTrace();
    }
  }

  /**
   * Stops and releases all resources used in application.
   */
  @Override
  public void stop() {
    // release resources when the application closes
    if (sceneView != null) {
      sceneView.dispose();
    }
  }

  /**
   * Opens and runs application.
   *
   * @param args arguments passed to this application
   */
  public static void main(String[] args) {

    Application.launch(args);
  }


}
