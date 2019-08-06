package com.esri.samples.custom_dictionary_style;

import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;

import com.esri.arcgisruntime.data.FeatureTable;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.portal.PortalItem;
import com.esri.arcgisruntime.symbology.DictionarySymbolStyle;

public class CustomDictionaryStyleController {

  @FXML
  private MapView mapView;

  private DictionarySymbolStyle restaurantStyle;

  @FXML
  public void initialize() {
    try {

      // create a new map with a streets basemap and display it
      ArcGISMap map = new ArcGISMap(Basemap.createStreetsVector());
      mapView.setMap(map);

      // open the custom style file
      restaurantStyle = DictionarySymbolStyle.createFromFile("./samples-data/stylx/Restaurant.stylx");

      // create a service feature table, and use it to create a feature layer
      ServiceFeatureTable serviceFeatureTable = new ServiceFeatureTable("https://services2.arcgis.com/ZQgQTuoyBrtmoGdP/arcgis/rest/services/Redlands_Restaurants/FeatureServer/0");
      FeatureLayer featureLayer = new FeatureLayer(serviceFeatureTable);
      featureLayer.addDoneLoadingListener(()->{
        mapView.setViewpointAsync(new Viewpoint(featureLayer.getFullExtent()));
      });
      // add the layer to the ArcGISMap
      map.getOperationalLayers().add(featureLayer);

      serviceFeatureTable.addDoneLoadingListener(() -> {
        List<Field> datasetFields = serviceFeatureTable.getFields();

        // build a list of numeric and text field names
        ArrayList<String> symbolFields = new ArrayList<>();
        datasetFields.forEach(field -> {
          if (field.getFieldType() == Field.Type.TEXT ||
                  field.getFieldType() == Field.Type.INTEGER ||
                  field.getFieldType() == Field.Type.DOUBLE) {
            symbolFields.add(field.getName());
          }
        });
      });


    } catch (Exception e) {
      e.printStackTrace();
    }
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
