package com.esri.samples.custom_dictionary_style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;

import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.symbology.DictionaryRenderer;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;

public class CustomDictionaryStyleController {

  @FXML private MapView mapView;
  @FXML private ComboBox<String> foodStyleComboBox;
  @FXML private ComboBox<String> ratingComboBox;
  @FXML private ComboBox<String> priceComboBox;
  @FXML private ComboBox<String> healthGradeComboBox;
  @FXML private ComboBox<String> nameComboBox;
  @FXML private CheckBox showTextCheckbox;
  @FXML private ProgressIndicator progressIndicator;

  private DictionarySymbolStyle restaurantStyle;
  private FeatureLayer restaurantsFeatureLayer;

  @FXML
  public void initialize() {
    try {
      // create a new map with a streets basemap and display it
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());
      mapView.setMap(map);

      // open the custom style file
      restaurantStyle = DictionarySymbolStyle.createFromFile("./samples-data/stylx/Restaurant.stylx");

      // create a service feature table, and use it to create a feature layer
      ServiceFeatureTable restaurantsServiceFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/rest/services/Redlands_Restaurants/FeatureServer/0");
      restaurantsFeatureLayer = new FeatureLayer(restaurantsServiceFeatureTable);

      // add the layer to the ArcGISMap
      map.getOperationalLayers().add(restaurantsFeatureLayer);

      // zoom to the extent of the feature layer once it has finished loading
      restaurantsFeatureLayer.addDoneLoadingListener(() -> mapView.setViewpointAsync(new Viewpoint(restaurantsFeatureLayer.getFullExtent())));

      // wait for the feature table to load and get its fields
      restaurantsServiceFeatureTable.addDoneLoadingListener(() -> {
        List<Field> datasetFields = restaurantsServiceFeatureTable.getFields();

        // build a list of numeric and text field names
        ArrayList<String> symbolFields = new ArrayList<>();
        datasetFields.forEach(field -> {
          if (field.getFieldType() == Field.Type.TEXT ||
                  field.getFieldType() == Field.Type.INTEGER ||
                  field.getFieldType() == Field.Type.DOUBLE) {
            symbolFields.add(field.getName());
          }
        });

        // add all field names to each combobox
        List<ComboBox<String>> comboBoxes = Arrays.asList(foodStyleComboBox, ratingComboBox, priceComboBox, healthGradeComboBox, nameComboBox);
        comboBoxes.forEach(comboBox -> comboBox.setItems(FXCollections.observableArrayList(symbolFields)));

        // select the default values for the expected symbol attribute
        foodStyleComboBox.getSelectionModel().select("Style");
        ratingComboBox.getSelectionModel().select("Rating");
        priceComboBox.getSelectionModel().select("Price");
        healthGradeComboBox.getSelectionModel().select("Inspection");
        nameComboBox.getSelectionModel().select("Name");

        // add a listener to the combo boxes to apply the dictionary renderer whenever an item is selected
        comboBoxes.forEach(stringComboBox -> stringComboBox.getSelectionModel().selectedItemProperty().addListener(observable ->
                applyDictionaryRenderer()
        ));

        // apply the dictionary renderer with the values selected in the comboboxes
        applyDictionaryRenderer();

        // hide the progress indicator
        progressIndicator.setVisible(false);
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Applies the selected overrides to the dictionary renderer.
   */
  @FXML
  private void applyDictionaryRenderer() {

    // create overrides for expected field names that are different in this dataset
    HashMap<String, String> styleToFieldMappingOverrides = new HashMap<>() {{
      put("style", foodStyleComboBox.getSelectionModel().getSelectedItem());
      put("healthgrade", healthGradeComboBox.getSelectionModel().getSelectedItem());
      put("rating", ratingComboBox.getSelectionModel().getSelectedItem());
      put("price", priceComboBox.getSelectionModel().getSelectedItem());
      put("name", nameComboBox.getSelectionModel().getSelectedItem());
    }};

    // create overrides for expected text field names (if any)
    String labelField = nameComboBox.getSelectionModel().getSelectedItem() != null ? nameComboBox.getSelectionModel().getSelectedItem() : "";
    HashMap<String, String> textFieldOverrides = new HashMap<>() {{
      put("name", labelField);
    }};

    // create the dictionary renderer with the style file and the style overrides
    DictionaryRenderer dictionaryRenderer = new DictionaryRenderer(restaurantStyle, styleToFieldMappingOverrides, textFieldOverrides);

    // apply the dictionary renderer to the layer
    restaurantsFeatureLayer.setRenderer(dictionaryRenderer);
  }

  /**
   * Sets the text visibility configuration settings
   */
  @FXML
  private void handleShowTextTick() {

    // find the requested value
    String requestedValue = showTextCheckbox.isSelected() ? "ON" : "OFF";

    // get all style configurations
    restaurantStyle.getConfigurations().stream()
            // find the configurations which apply to 'text' values
            .filter(configuration -> configuration.getName().equals("text"))
            // set their values to correspond to the requested value
            .forEach(configuration -> configuration.setValue(requestedValue));
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
